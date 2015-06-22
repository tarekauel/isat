package de.orangecode.streaming

import org.apache.spark.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver
import twitter4j._
import twitter4j.auth.Authorization

/**
 * @author Tarek Auel
 * @since June 15, 2015.
 */
class TwitterReceiver(auth: Authorization, track: Array[String], storageLevel: StorageLevel)
  extends Receiver[Status](storageLevel) with Logging {

  override def onStart(): Unit = {
    new Thread("Twitter receiver") {
      override def run() { receive() }
    }.start()
  }

  override def onStop(): Unit = {}

  private def receive(): Unit = {

    val tStream = new TwitterStreamFactory().getInstance(auth)
    tStream.addListener(new StatusListener {

      override def onStallWarning(warning: StallWarning): Unit = {
        logWarning(warning.getMessage + " " + warning.getPercentFull + "%")
      }

      override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {

      }

      override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {
        logWarning("On scrub geo " + userId + " up to status " + upToStatusId)
      }

      override def onStatus(status: Status): Unit = {
        logInfo("Received a status")
        store(status)
      }

      override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {
        logWarning("Limited status: " + numberOfLimitedStatuses)
      }

      override def onException(ex: Exception): Unit = {
        logError(ex.getMessage)
      }
    })

    val fq = new FilterQuery().track(track).language(Array("en"))
    tStream.filter(fq)
  }
}
