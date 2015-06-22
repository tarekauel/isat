package controllers.view

import play.api.mvc.{Action, Controller}

/**
 * @author Tarek Auel
 * @since June 15, 2015.
 */
class Statistics extends Controller {

  def index = Action {

    Ok(views.html.statistics.render("Statistics"))
  }

}
