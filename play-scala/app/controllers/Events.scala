package controllers

import play.api.data.{Form, Forms}
import play.api.mvc.{Controller, Action}
import play.api.Logger
import models.Event


object Events extends Controller {

  val eventForm = Form(
      Forms.mapping(
          "id" -> Forms.optional(Forms.longNumber()),
          "title" -> Forms.text,
        "location" -> Forms.text,
        "description" -> Forms.text
      )(Event.apply)(Event.unapply)
  )

  def createEvent = Action {

    Ok(views.html.createEvent(eventForm))
    }

  def updateEvent(id : Int) = Action {

    val event = Event.getById(id);
    Ok(views.html.createEvent(eventForm.fill(event)))
  }

  def save = Action { implicit request =>
    eventForm.bindFromRequest.fold(
      formWithErrors => BadRequest("Oh noes, invalid submission!"),

      createdEvent => {
        Event.insert(createdEvent)
        eventForm.fill(createdEvent)
        Ok(views.html.createEvent(eventForm.fill(createdEvent)))}
    )
  }

  def deleteEvent(id : Int)= Action { implicit request =>

    Event.delete(id)


    Ok("Event deleted")
  }


  }