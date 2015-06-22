package de.orangecode.entitymanager

import de.orangecode.Context
import isat.model.User
import org.apache.spark.Logging

import scala.collection.mutable

/**
 * @author Tarek Auel
 * @since June 05, 2015.
 */
class UserManager private(ctx: Context)
    extends EntityManager[User](ctx, User.getUser)
    with Logging {

  override val filename = "parquet/users"

  private[this] val mapping = mutable.Map[Long, Option[User]]().withDefaultValue(None)

  private[this] def addUser(user: User): User = {
    logInfo(s"User ${user.screenName} (${user.userId})  has been added to the data set")
    addEntity(List(user)).head
  }

  private[this] def getFromTwitterById(userId: Long): User = {
    try {
      User.getUser(ctx.twitter.showUser(userId))
    } catch {
      case _: Throwable => null
    }
  }

  private[this] def getFromTwitterByHandle(handle: String): User = {
    User.getUser(ctx.twitter.showUser(handle))
  }

  def getUnwatchedUser: Seq[User] = getAll.filter(!_.watched).collect()

  def getWatchedUser: Seq[User] = getAll.filter(_.watched).collect()

  def getAllUser: Seq[User] = getAll.collect()

  def addUserById(userId: Long): User = {
    val user = getUserById(userId)
    if (user.isDefined) {
      logInfo(s"User $userId does already exist")
      user.get
    } else {
      addUser(getFromTwitterById(userId))
    }
  }

  def addUserByHandle(handle: String): Option[User] = {
    val user = getUserByHandle(handle)
    if (user.isDefined) {
      logInfo(s"User $handle does already exist")
      Some(user.get)
    } else {
      Some(addUser(getFromTwitterByHandle(handle)))
    }
  }

  def updateUserById(userId: Long): Option[User] = {
    val user = getFromTwitterById(userId)
    if (user != null) {
      updateEntity(List(user))
      Some(user)
    } else {
      None
    }
  }

  def updateUserbyHandle(handle: String): Option[User] = {
    val user = getFromTwitterByHandle(handle)
    updateEntity(List(user))
    Some(user)
  }

  def getOrAddUser(userId: Long): User = addUserById(userId)

  def getUserById(userId: Long): Option[User] = {
    val lookup = mapping(userId)
    if (lookup.isDefined) {
      lookup
    } else {
      val user = getAll.filter(_.userId == userId).cache()
      if (user.isEmpty()) None else {
        val u = user.first()
        mapping.put(u.userId, Some(u))
        Some(u)
      }
    }
  }
  
  def getUserByHandle(handle: String): Option[User] = {
    val user = getAll.filter(_.screenName == handle).cache()
    if (user.isEmpty()) None else Option(user.first())
  }

  def getUserByHandle(handles: Seq[String]): Seq[Long] = {
    handles.map(getUserByHandle).filter(_.isDefined).map(_.get.userId)
  }

  def setUpdateNow(userId: Long): Unit = {
    val user = getUserById(userId).get
    user.lastUpdate = System.currentTimeMillis()
    updateEntity(Seq(user))
  }
}

object UserManager {

  var INSTANCE: UserManager = null

  def get = INSTANCE

  def apply(ctx: Context): Unit = {
    INSTANCE = new UserManager(ctx)
  }

}
