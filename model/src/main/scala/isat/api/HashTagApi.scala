package isat.api

import java.rmi.{Remote, RemoteException}
import java.util.Date

import isat.model.User

/**
 * @author Tarek Auel
 * @since June 13, 2015.
 */
trait HashTagApi extends Remote with Serializable {

  @throws(classOf[RemoteException])
  def topKByFrequency(
      k: Int,
      ignoreHandle: Seq[String],
      handlesToConsider: Seq[String],
      validFrom: Option[Date],
      validTo: Option[Date])
    : Seq[(String, Long)]

  @throws(classOf[RemoteException])
  def topKUsersOfHashTag(
      k: Int,
      hashTag: String, 
      ignoreHandle: Seq[String],
      handlesToConsider: Seq[String])
    : Seq[(User, Long)]
}
