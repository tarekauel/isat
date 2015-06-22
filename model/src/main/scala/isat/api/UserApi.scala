package isat.api

import java.rmi.{Remote, RemoteException}

import isat.model.{TweetResolved, User}

/**
 * @author Tarek Auel
 * @since June 10, 2015.
 */
trait UserApi extends Remote with Serializable {

  @throws(classOf[RemoteException])
  def addUser(handle: String): Option[User]

  @throws(classOf[RemoteException])
  def addUser(userId: Long): Option[User]

  @throws(classOf[RemoteException])
  def getUser(handle: String): Option[User]

  @throws(classOf[RemoteException])
  def getUser(userId: Long): Option[User]

  @throws(classOf[RemoteException])
  def updateUser(handle: String): Option[User]

  @throws(classOf[RemoteException])
  def updateUser(userId: Long): Option[User]

  @throws(classOf[RemoteException])
  def updateTweets(handle: String): Seq[TweetResolved]

  @throws(classOf[RemoteException])
  def getAllUnwatchedUsers: Seq[User]

  @throws(classOf[RemoteException])
  def getAllWatchedUsers: Seq[User]

  @throws(classOf[RemoteException])
  def getAll: Seq[User]

}
