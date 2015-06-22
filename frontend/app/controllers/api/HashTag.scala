package controllers.api

import java.text.SimpleDateFormat
import java.util.{Calendar, Locale, Date}

import misc.RmiBridge
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{Future, Promise}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Tarek Auel
 * @since June 13, 2015.
 */
class HashTag extends Controller with RmiBridge {

  private val resSize = 10

  def topKByFrequency(
    k: Int, ignoreHandle: List[String],
    handlesToConsider: List[String],
    from: String,
    to: String) = Action {

    val fromDate = if (from == null) null else new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(from)
    val toDate = if (to == null) null else new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(to)

    val sorted = hashTagApi
      .topKByFrequency(k = k, ignoreHandle = ignoreHandle, handlesToConsider = handlesToConsider,
        validFrom = Option(fromDate), validTo = Option(toDate))

    val json = {
      if (sorted.isEmpty) {
        "[]"
      } else {
        "[" + sorted.map((x) =>
          s"""
            |{"text": "${x._1}", "frequency": ${x._2}, "url": "hashtags/${x._1}"}
          """.stripMargin).reduce((a, b) => a + "," + b) + "]"
      }
    }

    Ok(Json.parse(json))
  }

  def topKUsersOfHashTag(
    hashTag: String, ignoreHandle: List[String], handlesToConsider: List[String]) = Action {

    val sorted = hashTagApi.topKUsersOfHashTag(k = resSize, hashTag = hashTag.toLowerCase,
      ignoreHandle = ignoreHandle, handlesToConsider = handlesToConsider)

    val json = {
      if (sorted.isEmpty) {
        "[]"
      } else {
        "[" + sorted.map((x) =>
          s"""
             |{"text": "${x._1.screenName}", "frequency": ${x._2},
             |"url": "/tweets/${x._1.screenName}/${hashTag.toLowerCase}"}
          """.stripMargin).reduce((a, b) => a + "," + b) + "]"
      }
    }
    Ok(Json.parse(json))
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

      res = res :+ (myEnd, hashTagApi
          .topKByFrequency(k = 15, ignoreHandle = List(), handlesToConsider = List(),
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
