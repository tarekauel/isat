package controllers

import java.text.SimpleDateFormat
import java.util.Date

import isat.model.Vertex
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.QueryStringBindable

/**
 * @author Tarek Auel
 * @since June 14, 2015.
 */
package object api {

  def parseSeqToJson(in: Seq[Vertex]): JsValue = {

    val json = "[" + {
      if (in.nonEmpty) {
        in.map(_.getJson).reduce((a, b) => a + "," + b)
      } else {
        ""
      }
    } + "]"

    Json.parse(json)
  }

  def parseSeqToJsonString(in: Seq[String]): JsValue = {

    val json = "[" + {
      if (in.nonEmpty) {
        in.reduce((a, b) => a + "," + b)
      } else {
        ""
      }
    } + "]"

    Json.parse(json)
  }

}
