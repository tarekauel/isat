package misc

import java.rmi.registry.{LocateRegistry, Registry}

import isat.api._

/**
 * @author Tarek Auel
 * @since June 10, 2015.
 */
trait RmiBridge {

  private[this] def registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT)

  lazy val userApi = registry.lookup(classOf[UserApi].getCanonicalName).asInstanceOf[UserApi]
  lazy val tweetApi = registry.lookup(classOf[TweetsApi].getCanonicalName).asInstanceOf[TweetsApi]
  lazy val hashTagApi = registry.lookup(classOf[HashTagApi].getCanonicalName)
    .asInstanceOf[HashTagApi]
  lazy val mgmtApi = registry.lookup(classOf[ManagementApi].getCanonicalName)
    .asInstanceOf[ManagementApi]

}
