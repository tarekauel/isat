package controllers

import misc.RmiBridge
import play.api.mvc.{Action, Controller}

class Application extends Controller with RmiBridge {

  def index = Action {
    Ok(views.html.timeseries.render("Hello World"))
  }

}
