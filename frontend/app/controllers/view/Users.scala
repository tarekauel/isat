package controllers.view

import misc.RmiBridge
import play.api.mvc.{Action, _}

/**
 * @author Tarek Auel
 * @since June 12, 2015.
 */
class Users extends Controller with RmiBridge {

  def index = Action {
    Ok(views.html.users.render("users overview"))
  }

  def displayUser(user: String) = Action {
    Ok(views.html.user.render("display user", s"/api/user/$user/hashtags"))
  }
}
