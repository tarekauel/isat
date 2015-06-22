package de.orangecode

import de.orangecode.entitymanager.{HashTagManager, TweetManager, UserManager}
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import twitter4j.Twitter

/**
 * @author Tarek Auel
 * @since June 05, 2015.
 */
class Context(val twitter: Twitter,
              val conf: SparkConf) {

  lazy val sq: SQLContext = null //new SQLContext(sc)
  lazy val ssc: StreamingContext = new StreamingContext(conf, Seconds(20))
  lazy val sc: SparkContext = ssc.sparkContext //new SparkContext(conf)

  def persistAllChanges(): Unit = {
    HashTagManager.get.persist()
    TweetManager.get.persist()
    UserManager.get.persist()
  }
}

object Context {



}
