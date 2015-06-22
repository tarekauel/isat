package controllers.view

import misc.RmiBridge
import play.api.mvc._

/**
 * @author Tarek Auel
 * @since June 12, 2015.
 */
class Tweets extends Controller with RmiBridge {

  def index = Action {
    Ok(views.html.tweets.render("latest tweets", "/api/tweets"))
  }

  def relatedToUserAndTag(user: String, tag: String) = Action {

    Ok(views.html.tweets.render("tweets user tag", s"/api/tweets/$user/$tag"))
  }

}
