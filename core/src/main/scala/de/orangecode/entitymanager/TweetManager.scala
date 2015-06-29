package de.orangecode.entitymanager

import java.io.File
import java.util.Scanner

import com.typesafe.scalalogging.LazyLogging
import de.orangecode.Context
import isat.model.{Tweet, TweetResolved, User}
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import twitter4j.{Paging, ResponseList}

import scala.collection.JavaConversions
import scala.io.Source

/**
 * @author Tarek Auel
 * @since June 05, 2015.
 */
class TweetManager private(ctx: Context)
    extends EntityManager[Tweet](ctx, Tweet.getTweet)
    with LazyLogging {

  override val filename = "parquet/tweets"

  override protected val typename: String = "Tweet"

  private[this] def addFromFile(filename: String): Unit = {
    val strings = Source.fromFile(filename, "UTF-8").getLines().toSeq

    val tweets = strings.dropRight(1).map(s =>
      if (s.charAt(0) != '[') s.substring(0, s.length - 1) else s.substring(1, s.length - 1)
    ).map(Tweet.getTweet)

    addEntity(tweets)
  }

  addFromFile(System.getenv("dataPath") + "/data/status_basic.json")
  addFromFile(System.getenv("dataPath") + "/data/status.json")

  private[this] val tweetByDate = new Ordering[Tweet]{
    override def compare(x: Tweet, y: Tweet): Int = x.createdAt.compareTo(y.createdAt)
  }

  private[this] def getLatestTweet(ctx: Context, userId: Long, time: Long): Option[Tweet] = {
    getLatestTweet((t: Tweet) => t.userId == userId, time)
  }

  private[this] def getLatestTweet(f: Tweet => Boolean, time: Long)
      : Option[Tweet] = {

    val byTimeDistance = new Ordering[Tweet]{
      override def compare(x: Tweet, y: Tweet): Int
      = Math.abs(time - x.createdAt.getTime).compareTo(Math.abs(time - y.createdAt.getTime))
    }

    val filteredTweets = getAll.filter(f)

    if (filteredTweets.isEmpty()) {
      None
    } else {
      Some(filteredTweets.min()(byTimeDistance))
    }
  }

  private[this] def updateTweets(userId: Long, maxId: Long): Seq[Tweet] = {
    val p = new Paging().count(200).maxId(maxId)
    val tweetsTwitter = ctx.twitter.getUserTimeline(userId, p)
    val tweets = resListToList(tweetsTwitter).map(Tweet.getTweet)
    val res = addEntity(tweets)
    if (tweetsTwitter.size() == 200) {
      val maxId = tweets.map(_.tweetId).min
      res ++ updateTweets(userId, maxId)
    } else {
      res
    }
  }

  private[this] def resListToList[T](in: ResponseList[T]): Seq[T] = {
    JavaConversions.collectionAsScalaIterable(in).toList
  }

  override def addEntity(list: Seq[Tweet]): Seq[Tweet] = {
    val addedTweets = super.addEntity(list)
    val hashTags = addedTweets.flatMap(_.hashTags)
    HashTagManager.get.addEntity(hashTags)
    addedTweets
    List() //TODO change this
  }

  def updateTweets(userId: Long): Seq[Tweet] = {
    val lastUpdate = UserManager.get.getUserById(userId).get.lastUpdate
    val lt: Option[Tweet] = {
      if (lastUpdate == 0) None
      else {
        getLatestTweet(ctx, userId, lastUpdate)
      }
    }
    val p = new Paging().count(200)
    if (lt.isDefined) p.setSinceId(lt.get.tweetId)
    val tweetsTwitter = ctx.twitter.getUserTimeline(userId, p)
    val tweets = resListToList(tweetsTwitter).map(Tweet.getTweet)
    val res = addEntity(tweets)
    UserManager.get.setUpdateNow(userId)
    if (tweetsTwitter.size() == 200) {
      if (lt.isDefined) {
        // the user has more than 200 new tweets
        res ++ updateTweets(userId)
      } else {
        // We miss some old tweets
        val maxId = tweets.map(_.tweetId).min
        res ++ updateTweets(userId, maxId)
      }
    } else {
      res
    }
  }

  def filter(tweets: RDD[Tweet], userIds: Seq[Long], whiteList: Boolean = true): RDD[Tweet] = {
    if (userIds.isEmpty) {
      tweets
    } else {
      val bl = ctx.sc.broadcast(userIds)
      if (whiteList) {
        tweets.filter((t) => bl.value.contains(t.userId))
      } else {
        tweets.filter((t) => !bl.value.contains(t.userId))
      }
    }
  }

  def filter(tweets: RDD[Tweet], hashTags: List[String]): RDD[Tweet] = {
    val tags = ctx.sc.broadcast(hashTags)
    tweets.filter(_.hashTags.exists((h) => tags.value.contains(h.text.toLowerCase)))
  }

  def recentK(tweets: RDD[Tweet], k: Int): Seq[Tweet] = {
    tweets.takeOrdered(k)(tweetByDate.reverse)
  }

  def get(skip: Int): Seq[Tweet] = {
    getAll.sortBy(_.createdAt, ascending = false)
      .take(20 + skip).takeRight(20)
  }

  def get(k: Int, user: String, tag: String): Seq[TweetResolved] = {
    val u: User = UserManager.get.getUserByHandle(user).getOrElse(UserManager.get.addUserByHandle(user).get)
    filter(getAll, List(tag)).filter(_.userId == u.userId).take(k).map(resolveTweet)
  }

  def resolveTweet(tweet: Tweet): TweetResolved = {
    val user = UserManager.get.getUserById(tweet.userId).getOrElse(new User(tweet.userId))
    TweetResolved.getResolved(tweet, user)
  }
}

object TweetManager {

  private[this] var INSTANCE: TweetManager = null

  def get = INSTANCE

  def apply(ctx: Context): Unit = {
    INSTANCE = new TweetManager(ctx)
  }

}
