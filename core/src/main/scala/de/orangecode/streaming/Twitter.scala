package de.orangecode.streaming

import de.orangecode.Context
import de.orangecode.entitymanager.TweetManager
import de.orangecode.rmi.HashTagApiImpl
import isat.model.Tweet
import org.apache.spark.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.Seconds


/**
 * @author Tarek Auel
 * @since June 15, 2015.
 */
object Twitter extends Logging {

  def start(ctx: Context) {

    val tracks =
      Array("bigdata",
            "#nlp",
            "datascience",
            "#ai",
            "machinelearning",
            "machineintelligence",
            "predictiveanalysis",
            "artificial intelligence",
            "artificialintelligence")

    val hti = new HashTagApiImpl()
    val topHT = hti.topKByFrequency(40, Seq[String](), Seq[String](), None, None).map("#" + _._1)
    val lookFor = tracks ++ topHT

    logInfo("Subscribe to: " + lookFor)

    val twitterReceiver = new TwitterReceiver(ctx.twitter.getAuthorization, lookFor,
      StorageLevel.MEMORY_AND_DISK)

    val stream = ctx.ssc.receiverStream(twitterReceiver)

    val tm = TweetManager.get

    stream.map(Tweet.getTweet).window(Seconds(20)).foreachRDD(rdd => {
      tm.addEntity(rdd)
    })

    ctx.ssc.start()
    stream.start()
  }
}
