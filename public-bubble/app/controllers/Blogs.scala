package controllers

import com.cloudinary._
import com.cloudinary.utils.ObjectUtils
import controllers.Application._
import models.Event
import play.api.Play.current
import play.api.data.{Form, Forms}
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.mvc._

object Blogs extends Controller {

  def events = Action {
    val event = Event.getLatest
    print(event)
    Ok(views.html.events(event))

  }


  def createEvent = Authenticated {
    Ok(views.html.createEvent(eventForm))
  }


  def updateEvent(id : Int) = Action {

    val event = Event.getById(id);
    print("event.id: " + event.id);
    Ok(views.html.createEvent(eventForm.fill(event)))
  }

  def save = Action { implicit request =>
    eventForm.bindFromRequest.fold(
      formWithErrors =>     {
        print(formWithErrors.errors)
        Ok(views.html.createEvent(formWithErrors))},

      createdEvent => {
        if (createdEvent.id.isEmpty) {
          Event.create(createdEvent)
        }
        else {
          Event.update(createdEvent)
        }
        Ok(views.html.createEvent(eventForm.fill(createdEvent)))
      }
    )
  }

  def deleteEvent(id : Int)= Action { implicit request =>
    Event.delete(id)
    Ok(views.html.createEvent(eventForm))
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

  val eventForm = Form(
    Forms.mapping(
      "id" -> Forms.optional(Forms.longNumber()),
      "title" -> Forms.text,
      "location" -> Forms.text,
      "description" -> Forms.text,
      "displayFrom" -> Forms.date("dd-MM-yyyy").verifying(),
      "displayUntil" -> Forms.date("dd-MM-yyyy")
    )(Event.apply)(Event.extract)
  )

  // here we are calling the ActionBuilder apply method
  // the apply method can accept a function
  def authenticatedEventForm = Authenticated { request  =>
    Ok(views.html.createEvent(eventForm))
  }


}