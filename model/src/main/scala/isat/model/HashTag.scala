package isat.model

import java.util.Objects

import twitter4j.HashtagEntity

/**
 * @author Tarek Auel
 * @since June 05, 2015.
 */
class HashTag(var text: String) extends Twitter {


  override def getId: Long = text.toCharArray.sum

  override def getLabel: String = text

  override def equals(other: Any): Boolean = other match {
    case h: HashTag if Objects.equals(h.text.toUpperCase, text.toUpperCase) => true
    case _ => false
  }
}

object HashTag {

  val collectionName = "twitterHashTags"

  def getHashTag(in: String): HashTag = {
    Gson.gson.fromJson(in, classOf[HashTag])
  }

  def getHashTag(in: HashtagEntity): HashTag = {
    new HashTag(in.getText)
  }

  /*def getHashTag(in: Imports.DBObject): HashTag = {
    new HashTag(in.get("text").asInstanceOf[String])
  }

  def getMongoDB(in: HashTag): Imports.DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "text" -> in.text
    builder.result()
  }*/

}
