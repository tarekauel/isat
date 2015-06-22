package isat.api

import java.rmi.{Remote, RemoteException}
import java.util.Date

import isat.model.TweetResolved

/**
 * @author Tarek Auel
 * @since June 12, 2015.
 */
trait TweetsApi extends Remote with Serializable {

  @throws(classOf[RemoteException])
  def topKByDate(k: Int): Seq[TweetResolved]

  @throws(classOf[RemoteException])
  def relatedToUserHashTag(k: Int, tag: String, user: String)
    : Seq[TweetResolved]

  @throws(classOf[RemoteException])
  def topKByFrequencyMentioned(
     k: Int,
     ignoreHandle: Seq[String],
     handlesToConsider: Seq[String],
     validFrom: Option[Date],
     validTo: Option[Date])
  : Seq[(String, Long)]


}
