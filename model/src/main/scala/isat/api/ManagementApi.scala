package isat.api

import java.rmi.{Remote, RemoteException}

/**
 * @author Tarek Auel
 * @since June 14, 2015.
 */
trait ManagementApi extends Remote with Serializable {

  /**
   * Trigger manually a persistence of the changed data
   * @throws java.rmi.RemoteException
   */
  @throws(classOf[RemoteException])
  def persist(): Unit

  /**
   * Calculates the number of tweets, hashtags and users in the system
   * @throws java.rmi.RemoteException
   * @return
   */
  @throws(classOf[RemoteException])
  def stats(): Map[String, Long]

  @throws(classOf[RemoteException])
  def shutdown(): Unit

}
