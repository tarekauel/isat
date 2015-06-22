package controllers.api

import java.text.SimpleDateFormat
import java.util.{Calendar, Date, Locale}

import controllers.api
import misc.RmiBridge
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
 * @author Tarek Auel
 * @since June 14, 2015.
 */
class Tweet extends Controller with RmiBridge {

  private[this] val resSize = 10

  def recentTweets = Action {

    val tweets = tweetApi.topKByDate(k = resSize)

    Ok(api.parseSeqToJson(tweets))
  }

  def relatedToUserAndTag(user: String, tag: String) = Action {

    val tweets = tweetApi.relatedToUserHashTag(k = 10, tag = tag.toLowerCase, user = user)

    Ok(api.parseSeqToJson(tweets))
  }

  def timeseries(handle: String) = Action {

    val start = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(s"12/01/2014")
    var i = 0
    var res = List[(Date, Seq[(String, Long)])]()
    (0 to 26).foreach( i => {
      val c = Calendar.getInstance()
      c.setTime(start)
      c.add(Calendar.DATE, i * 7)
      val myStart = c.getTime
      c.add(Calendar.DATE, 1 * 7)
      c.add(Calendar.DATE, -1)
      val myEnd = c.getTime

      val handleList = if (handle == "") List() else List(handle)
      res = res :+ (myEnd, tweetApi
        .topKByFrequencyMentioned(k = 15, ignoreHandle = List(), handlesToConsider = handleList,
          validFrom = Option(myStart), validTo = Option(myEnd)))
    })

    var json = "["

    res = res.sortBy(_._1)
    val topK = res.flatMap(x => x._2.sortBy(_._2).takeRight(5).map(_._1)).distinct

    val allTags = res.flatMap(_._2.map(_._1)).filter(topK.contains).distinct

    res = res.map( t => {
      val existing = t._2.map(_._1)
      val missing = allTags.filterNot(existing.contains)
      (t._1, t._2 ++ missing.map(s => (s, 0L)))
    })

    val sdf = new SimpleDateFormat("yyyMMdd")
    json += res.map(t => {
      "{\"date\": \"" + sdf.format(t._1) + "\"" +
        t._2.map(x => ", \"" + x._1 + "\": " + x._2 + "").reduce(_ + _) + "}"
    }).reduce((a, b) => a + "," + b) + "]"



    Ok(Json.parse(json))
  }

}
