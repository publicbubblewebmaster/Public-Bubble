package controllers

import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def twitter = Action {
    Ok(views.html.twitter("Twitter"))
  }

  def facebook = Action {
    Ok(views.html.facebook())
  }
}