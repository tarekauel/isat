package de.orangecode

import java.rmi.registry.{LocateRegistry, Registry}

import com.typesafe.scalalogging.LazyLogging
import de.orangecode.entitymanager.{HashTagManager, TweetManager, UserManager}
import de.orangecode.rmi._
import isat.api._
import org.apache.spark.{Logging, SparkConf}


/**
 * @author Tarek Auel
 * @since June 01, 2015.
 */
object Starter extends App with LazyLogging {

  def join(a: String, b: String) = a + "\n" + b

  val conf = new SparkConf()
    .setAppName("ISAT")
    .setMaster("spark://sparkdriver:7077")

  val twitter = ApiConnector.connect()
  val ctx = new Context(twitter, conf)

  HashTagManager(ctx)
  TweetManager(ctx)
  UserManager(ctx)

  val registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT)
  registry.rebind(classOf[UserApi].getCanonicalName, new UserApiImpl())
  registry.rebind(classOf[TweetsApi].getCanonicalName, new TweetsApiImpl())
  registry.rebind(classOf[HashTagApi].getCanonicalName, new HashTagApiImpl())
  registry.rebind(classOf[ManagementApi].getCanonicalName, new ManagementApiImpl())

  logger.info("Registry launched and api registered")

  //println(new HashTagApiImpl().topKByFrequency(40, List(), List(), None, None)
  //  .map(t => s""""${t._1}"""").reduce((a, b) => s"$a,\n$b"))

  new Thread() {
    override def run(): Unit = while (true) {
      Thread.sleep(10 * 60 * 1000)
    }
  }.start()

  //ctx.persistAllChanges()
/*
  Twitter.start(ctx)

  def shutdown(): Unit = {

    ctx.ssc.awaitTerminationOrTimeout(15000)
    ctx.persistAllChanges()
    logInfo("Shutting down")
    ctx.sc.stop()

    Thread.sleep(5000)
    System.exit(0)
  }*/

}
