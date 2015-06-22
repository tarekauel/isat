package de.orangecode

import java.util.Date

import de.orangecode.entitymanager.{HashTagManager, UserManager, TweetManager}
import isat.model.Tweet
import org.apache.spark.rdd.RDD

/**
 * @author Tarek Auel
 * @since June 18, 2015.
 */
package object rmi  {

  private[this] lazy val tm = TweetManager.get
  private[this] lazy val um = UserManager.get
  private[this] lazy val hm = HashTagManager.get

  private[rmi] def getFilteredTweets(all: RDD[Tweet],
                                       ignoreHandle: Seq[String],
                                       handlesToConsider: Seq[String])
  : RDD[Tweet] = {

    if (handlesToConsider.nonEmpty) {
      tm.filter(all, um.getUserByHandle(handlesToConsider))
    } else {
      tm.filter(all, um.getUserByHandle(ignoreHandle), whiteList = false)
    }
  }

  private[rmi] def filterOnTime(rdd: RDD[Tweet], validFrom: Option[Date], validTo: Option[Date]): RDD[Tweet] = {
    val a = if (validFrom.isDefined) rdd.filter(_.createdAt.after(validFrom.get)) else rdd
    if (validTo.isDefined) a.filter(_.createdAt.before(validTo.get)) else a
  }

  private[rmi] val stringLongOrder = new Ordering[(String, Long)] {
    override def compare(x: (String, Long), y: (String, Long)): Int = x._2.compareTo(y._2)
  }

}
