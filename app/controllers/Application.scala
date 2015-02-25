package controllers

import models.Event
import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def blog = Action {
    Ok(views.html.blog("Public Bubble"))
  }

  def events = Action {
    val event = new Event("Nicky Morgan - Faith schools and literacy", "Westminster Abbey",
      "Nicky Morgan will be talking about the role faith schools can play in reaching national literacy targets.")
    Ok(views.html.events(event))

  }

}