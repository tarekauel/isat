package controllers.api

import misc.RmiBridge
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
 * @author Tarek Auel
 * @since June 14, 2015.
 */
class Management extends Controller with RmiBridge {

  def persist = Action {

    mgmtApi.persist()

    Ok("<h3>success</h3>")
  }

  def stats = Action {
    val json = "{" + mgmtApi.stats().map((t) =>
      s""""${t._1}": ${t._2}""").reduce((a, b) => a + "," + b) + "}"
    Ok(Json.parse(json))
  }

  def shutdown = Action {
    mgmtApi.shutdown()
    Ok("App closed")
  }

}
