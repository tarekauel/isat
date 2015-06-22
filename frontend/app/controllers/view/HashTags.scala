package controllers.view

import misc.RmiBridge
import play.api.mvc._

/**
 * @author Tarek Auel
 * @since June 13, 2015.
 */
class HashTags extends Controller with RmiBridge {

  def index = Action {
    Ok(views.html.hashtags.render("hashtags overview", "/api/hashtags"))
  }

  def tagDetails(tag: String) = Action {
    Ok(views.html.hashtags.render("hashtags overview", "/api/hashtags/" + tag))
  }
}
