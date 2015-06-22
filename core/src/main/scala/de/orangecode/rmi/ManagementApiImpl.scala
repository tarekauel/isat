package de.orangecode.rmi

import java.rmi.RemoteException
import java.rmi.server.UnicastRemoteObject

import de.orangecode.entitymanager.{HashTagManager, TweetManager, UserManager}
import isat.api.ManagementApi

/**
 * @author Tarek Auel
 * @since June 14, 2015.
 */
class ManagementApiImpl extends UnicastRemoteObject with ManagementApi{

  private[this] lazy val tm = TweetManager.get
  private[this] lazy val um = UserManager.get
  private[this] lazy val hm = HashTagManager.get

  @throws(classOf[RemoteException])
  override def persist(): Unit = {
    hm.persist()
    tm.persist()
    um.persist()
  }

  @throws(classOf[RemoteException])
  override def stats(): Map[String, Long] = {
    Map(
      "HashTags" -> hm.getAll.map(_.text.toLowerCase).distinct().count(),
      "Tweets" -> tm.getAll.count(),
      "User" -> um.getAll.count()
    )
  }

  @throws(classOf[RemoteException])
  override def shutdown(): Unit = {
    //Starter.shutdown()
  }
}
