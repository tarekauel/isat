package de.orangecode.rmi

import java.rmi.RemoteException
import java.rmi.server.UnicastRemoteObject
import java.util.Date

import de.orangecode.entitymanager.{HashTagManager, TweetManager, UserManager}
import de.orangecode.rmi
import isat.api.TweetsApi
import isat.model.TweetResolved

/**
 * @author Tarek Auel
 * @since June 12, 2015.
 */
class TweetsApiImpl extends UnicastRemoteObject with TweetsApi {

  private[this] lazy val tm = TweetManager.get
  private[this] lazy val um = UserManager.get
  private[this] lazy val hm = HashTagManager.get

  @throws(classOf[RemoteException])
  override def topKByDate(k: Int): Seq[TweetResolved] = {
    tm.recentK(tm.getAll, k).map(tm.resolveTweet)
  }

  @throws(classOf[RemoteException])
  override def relatedToUserHashTag(k: Int, tag: String, user: String): Seq[TweetResolved] = {
    tm.get(k, user, tag)
  }

  @throws(classOf[RemoteException])
  override def topKByFrequencyMentioned(
     k: Int,
     ignoreHandle: Seq[String],
     handlesToConsider: Seq[String],
     validFrom: Option[Date],
     validTo: Option[Date]): Seq[(String, Long)] = {

    val preFiltered = getFilteredTweets(tm.getAll, ignoreHandle, handlesToConsider)

    val tweets = filterOnTime(preFiltered, validFrom, validTo)

    tweets.flatMap(_.mentionedUsers).map((h) =>
      (h.toString, 1L))
      .reduceByKey(_ + _).takeOrdered(if (k != 0) k else 10)(stringLongOrder.reverse)
  }
}
