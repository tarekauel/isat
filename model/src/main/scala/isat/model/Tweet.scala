package isat.model

import java.util.Date
import twitter4j.{HashtagEntity, Status, UserMentionEntity}

/**
 * @author Tarek Auel
 * @since June 03, 2015.
 */
class Tweet (val tweetId: Long) extends Twitter {


  override def getId: Long = tweetId

  var text: String = null
  var source: String = null
  var isTruncated: Boolean = false
  var inReplyToStatusId: Long = -1
  var inReplyToUserId: Long = -1
  var favoriteCount: Int = 0
  var isRetweet: Boolean = false
  var retweetCount: Int = 0
  var userId: Long = 0
  var contributors: List[Long] = List()
  var lang: String = null
  var hashTags: List[HashTag] = List()
  var mentionedUsers: List[Long] = List()
  var createdAt: Date = null


  override def getLabel: String = text

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case t: Tweet if t.tweetId == tweetId => true
      case _ => false
    }
  }
}

object Tweet {

  val collectionName = "twitterTweet"

  private[this] def arrayToList(in: Array[Long]) = in.toList
  private[this] def parseHashTags(in: Array[HashtagEntity]) = in.toList.map(HashTag.getHashTag)
  private[this] def parseMenUsers(in: Array[UserMentionEntity]) = in.toList.map(_.getId)

  def getTweet(in: String): Tweet = {
    Gson.gson.fromJson(in, classOf[Tweet])
  }

  def getTweet(s: Status):Tweet =  {
    val t = new Tweet(s.getId)
    t.text = s.getText
    t.source = s.getSource
    t.isTruncated = s.isTruncated
    t.inReplyToStatusId = s.getInReplyToStatusId
    t.inReplyToUserId = s.getInReplyToUserId
    t.favoriteCount = s.getFavoriteCount
    t.isRetweet = s.isRetweet
    t.retweetCount = s.getRetweetCount
    t.userId = s.getUser.getId
    t.contributors = arrayToList(s.getContributors)
    t.lang = s.getLang
    t.hashTags = parseHashTags(s.getHashtagEntities)
    t.mentionedUsers = parseMenUsers(s.getUserMentionEntities)
    t.createdAt = new Date(s.getCreatedAt.getTime)
    t
  }
}


