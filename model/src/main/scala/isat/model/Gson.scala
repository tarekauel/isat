package isat.model

import java.lang.reflect.Type
import java.util.Date

import com.google.gson._

/**
 * @author Tarek Auel
 * @since June 07, 2015.
 */
object Gson {

  private[this] def parseListLong(e: JsonElement): List[Long] = {
    if (e.isJsonArray) {
      var res = List[Long]()
      val it = e.getAsJsonArray.iterator()
      while (it.hasNext) {
        res ::= it.next().getAsLong
      }
      res
    } else {
      List[Long]()
    }
  }

  private[this] def parseHastagList(e: JsonElement, ctx: JsonDeserializationContext)
    : List[HashTag] = {
    if (e.isJsonArray) {
      var res = List[HashTag]()
      val it = e.getAsJsonArray.iterator()
      while (it.hasNext) {
        res ::= ctx.deserialize(it.next(), classOf[HashTag])
      }
      res
    } else {
      List[HashTag]()
    }
  }

  val gson =
    new GsonBuilder()
      .registerTypeAdapter(classOf[Tweet], new JsonDeserializer[Tweet] {
      override def deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): Tweet = {
        val jo = json.getAsJsonObject
        val t = new Tweet(jo.get("tweetId").getAsLong)
        t.text = jo.get("text").getAsString
        t.source = jo.get("source").getAsString
        t.isTruncated = jo.get("isTruncated").getAsBoolean
        t.inReplyToStatusId = jo.get("inReplyToStatusId").getAsLong
        t.inReplyToUserId = jo.get("inReplyToUserId").getAsLong
        t.favoriteCount = jo.get("favoriteCount").getAsInt
        t.isRetweet = jo.get("isRetweet").getAsBoolean
        t.retweetCount = jo.get("retweetCount").getAsInt
        t.userId = jo.get("userId").getAsLong
        t.contributors = parseListLong(jo.get("contributors"))
        t.lang = jo.get("lang").getAsString
        t.hashTags = parseHastagList(jo.get("hashTags"), ctx)
        t.mentionedUsers = parseListLong(jo.get("mentionedUsers"))
        t.createdAt = ctx.deserialize(jo.get("createdAt"), classOf[Date])
        t
      }
    }).registerTypeAdapter(classOf[List[Long]], new JsonDeserializer[List[Long]] {
        override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List[Long] = {
         parseListLong(json)
        }
      }).registerTypeAdapter(classOf[List[Any]], new JsonSerializer[List[Any]] {
      override def serialize(src: List[Any], typeOfSrc: Type, ctx: JsonSerializationContext): JsonElement = {
        val ja = new JsonArray()
        src.foreach((x) => ja.add(ctx.serialize(x)))
        ja
      }
    }).registerTypeAdapter(classOf[Date], new JsonSerializer[Date] {
      override def serialize(src: Date, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
        new JsonPrimitive(src.getTime)
      }
    }).registerTypeAdapter(classOf[Date], new JsonDeserializer[Date] {
      override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date = {
        val j = json.getAsJsonPrimitive
        if (j.isNumber) {
          new Date(j.getAsLong)
        } else {
          new Gson().fromJson[Date](json, classOf[Date])
        }
      }
    })
    .create()

}
