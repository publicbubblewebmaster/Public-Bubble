package controllers

import models.Event
import play.api.mvc._
import play.api.libs.json._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def blog = Action {
    Ok(views.html.blog("Public Bubble"))
  }

  def events = Action {
    val event = Event.getLatest
    Ok(views.html.events(event))

  }

  def twitter = Action {
    Ok(views.html.twitter("Twitter"))
  }

  def eventsJson = Action {
    val jsonEvents : List[JsValue] =
              Event.getAll.map(
                event =>
                  Json.obj(
                    "id" -> event.id,
                    "title" -> event.title
                  )
              )
    Ok(JsArray(jsonEvents))
  }

}