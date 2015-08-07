package controllers

import play.api.mvc._

object Application extends Controller {

  def aboutUs = Action {
    Ok(views.html.autocompleteTag())
  }

  def twitter = Action {
    Ok(views.html.twitter("Twitter"))
  }

  def facebook = Action {
    Ok(views.html.facebook())
  }
}