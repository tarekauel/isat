package de.orangecode.rmi

import java.rmi.RemoteException
import java.rmi.server.UnicastRemoteObject

import de.orangecode.entitymanager.{HashTagManager, TweetManager, UserManager}
import isat.api.UserApi
import isat.model.{TweetResolved, User}

/**
 * @author Tarek Auel
 * @since June 10, 2015.
 */
class UserApiImpl extends UnicastRemoteObject with UserApi {

  private[this] lazy val tm = TweetManager.get
  private[this] lazy val um = UserManager.get
  private[this] lazy val hm = HashTagManager.get

  @throws(classOf[RemoteException])
  override def addUser(handle: String): Option[User] = um.addUserByHandle(handle)

  @throws(classOf[RemoteException])
  override def addUser(userId: Long): Option[User] = Some(um.addUserById(userId))

  @throws(classOf[RemoteException])
  override def getUser(handle: String): Option[User] = um.getUserByHandle(handle)

  @throws(classOf[RemoteException])
  override def getUser(userId: Long): Option[User] = um.getUserById(userId)

  @throws(classOf[RemoteException])
  override def updateUser(handle: String): Option[User] = um.updateUserbyHandle(handle)

  @throws(classOf[RemoteException])
  override def updateUser(userId: Long): Option[User] = um.updateUserById(userId)

  @throws(classOf[RemoteException])
  override def updateTweets(handle: String): Seq[TweetResolved] =
    tm
      .updateTweets(getUser(handle).getOrElse(addUser(handle).get).userId)
      .map(tm.resolveTweet)

  @throws(classOf[RemoteException])
  override def getAllUnwatchedUsers: Seq[User] = um.getUnwatchedUser

  @throws(classOf[RemoteException])
  override def getAllWatchedUsers: Seq[User] = um.getWatchedUser

  @throws(classOf[RemoteException])
  override def getAll: Seq[User] = um.getAllUser
}
