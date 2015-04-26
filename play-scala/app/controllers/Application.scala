package controllers

import models.Event
import play.api.data.{Form, Forms}
import play.api.libs.json._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def blog = Action {
    Ok(views.html.blog("Public Bubble"))
  }

  def events = Action {
    val event = Event.getLatest
    print(event)
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

  def createEvent = Action {

    Ok(views.html.createEvent(eventForm))
  }

  def updateEvent(id : Int) = Action {

    val event = Event.getById(id);
    Ok(views.html.createEvent(eventForm.fill(event)))
  }

  def save = Action { implicit request =>
    eventForm.bindFromRequest.fold(
      formWithErrors =>     {
        print(formWithErrors.errors)

        Ok(views.html.createEvent(formWithErrors))},



      createdEvent => {
        print(createdEvent)
        val event = Event.insert(createdEvent)
        eventForm.fill(createdEvent)
        Ok(views.html.createEvent(eventForm.fill(createdEvent)))}
    )
  }

  def deleteEvent(id : Int)= Action { implicit request =>
    Event.delete(id)
    Ok(views.html.createEvent(eventForm))
  }

  val eventForm = Form(
    Forms.mapping(
      "id" -> Forms.optional(Forms.longNumber()),
      "title" -> Forms.text,
      "location" -> Forms.text,
      "description" -> Forms.text,
      "displayFrom" -> Forms.date("yyyy-MM-dd"),
      "displayUntil" -> Forms.date("yyyy-MM-dd")
    )(Event.apply)(Event.unapply)
  )
}