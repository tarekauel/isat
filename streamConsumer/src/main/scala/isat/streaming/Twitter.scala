package isat.streaming

import java.io.{PrintWriter, FileWriter, BufferedWriter}

import com.typesafe.scalalogging.LazyLogging
import isat.model.Tweet
import orangecode.ApiConnector
import twitter4j._

/**
 * @author Tarek Auel
 * @since June 21, 2015.
 */
object Twitter extends App with LazyLogging {

 val auth = ApiConnector.connect().getAuthorization

  val tStream = new TwitterStreamFactory().getInstance(auth)

  val pWriter = new PrintWriter(new BufferedWriter(new FileWriter("status.json", true)))

  tStream.addListener(new StatusListener {

    override def onStallWarning(warning: StallWarning): Unit = logger.warn(warning.toString)

    override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit =
      logger.warn(statusDeletionNotice.toString)

    override def onScrubGeo(userId: Long, upToStatusId: Long): Unit =
      logger.warn(s"$userId: $upToStatusId")

    override def onStatus(status: Status): Unit = {
      logger.info(s"Received status")
      pWriter.append(Tweet.getTweet(status).getJson + ",\n")
    }

    override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit =
      logger.warn(s"Track limitation notices: $numberOfLimitedStatuses")

    override def onException(ex: Exception): Unit =
      logger.error(ex.toString)

  })

  tStream.filter(Query.getQuery)

  System.in.read()

  pWriter.close()
  tStream.cleanUp()
  tStream.shutdown()



}
