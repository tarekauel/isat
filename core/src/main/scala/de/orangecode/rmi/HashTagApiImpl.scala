package de.orangecode.rmi

import java.rmi.RemoteException
import java.rmi.server.UnicastRemoteObject
import java.util.Date

import de.orangecode.entitymanager.{HashTagManager, TweetManager, UserManager}
import isat.api.HashTagApi
import isat.model.{Tweet, User}
import org.apache.spark.rdd.RDD

/**
 * @author Tarek Auel
 * @since June 13, 2015.
 */
class HashTagApiImpl extends UnicastRemoteObject with HashTagApi {

  private[this] lazy val tm = TweetManager.get
  private[this] lazy val um = UserManager.get
  private[this] lazy val hm = HashTagManager.get

  private[this] val longPairOrder = new Ordering[(Long, Long)]{
    override def compare(x: (Long, Long), y: (Long, Long)): Int = x._2.compareTo(y._2)
  }

  @throws(classOf[RemoteException])
  override def topKByFrequency(
      k: Int,
      ignoreHandle: Seq[String],
      handlesToConsider: Seq[String],
      validFrom: Option[Date],
      validTo: Option[Date])
    : Seq[(String, Long)] = {

    val preFiltered = getFilteredTweets(tm.getAll, ignoreHandle, handlesToConsider)

    val tweets = filterOnTime(preFiltered, validFrom, validTo)

    tweets.flatMap(_.hashTags.map((h) => (h.text.toLowerCase, 1L)))
      .reduceByKey(_ + _).takeOrdered(if (k != 0) k else 10)(stringLongOrder.reverse)
  }


  @throws(classOf[RemoteException])
  override def topKUsersOfHashTag(
      k: Int,
      hashTag: String,
      ignoreHandle: Seq[String],
      handlesToConsider: Seq[String])
    : Seq[(User, Long)] = {

    val userFilteredTweets = {
      if (ignoreHandle.isEmpty && handlesToConsider.isEmpty) tm.getAll
      else getFilteredTweets(tm.getAll, ignoreHandle, handlesToConsider)
    }

    val htFilteredTweets = tm.filter(userFilteredTweets, List(hashTag))

    htFilteredTweets
      .map((t) => (t.userId, 1L)).reduceByKey(_ + _).takeOrdered(k)(longPairOrder.reverse)
      .map((x) => (um.getOrAddUser(x._1), x._2))
  }
}
