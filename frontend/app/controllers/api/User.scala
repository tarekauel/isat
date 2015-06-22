package controllers.api

import controllers.api
import misc.RmiBridge
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json.Json
import play.api.mvc._

/**
 * @author Tarek Auel
 * @since June 12, 2015.
 */
class User extends Controller with RmiBridge {

  def updateUser(handle: String) = Action {

    val user = userApi.updateUser(handle)

    Ok(Json.parse(user.get.getJson))
  }

  def add() = Action { implicit request =>

    val userForm = Form("handle" -> text)

    val handle = userForm.bindFromRequest.get

    userApi.updateUser(handle)

    Redirect("/users")
  }

  def updateTweets(handle: String) = Action {

    val tweets = userApi.updateTweets(handle)

    if (tweets.isEmpty) {
      Ok(Json.parse("[]"))
    } else {
      Ok(Json.parse("[" + tweets.map(_.getJson).reduce((a, b) => a + "," + b)  + "]"))
    }
  }

  def all = Action {
    val user = userApi.getAllUnwatchedUsers
    Ok(api.parseSeqToJson(user))
  }

  def hashtags(user: String) = Action {

    val tags = hashTagApi.topKByFrequency(10, List(), List(user), None, None)

    val json = {
      if (tags.isEmpty) {
        "[]"
      } else {
        "[" + tags.map((x) =>
          s"""
             |{"text": "${x._1}", "frequency": ${x._2}, "url": "/tweets/$user/${x._1}"}
          """.stripMargin).reduce((a, b) => a + "," + b) + "]"
      }
    }

    Ok(Json.parse(json))
  }

}
