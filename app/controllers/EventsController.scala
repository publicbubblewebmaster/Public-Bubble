package controllers

import java.util.Date

import _root_.util.{GooglePlace, GooglePlaceFinder}
import dao.{SlickEventDao}
import models.{EventFormData, Event}
import play.api.{Play, Logger}
import play.api.data.{Form, Forms}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.mvc._
import play.api.cache.{Cached, Cache}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object EventsController extends Controller {

  lazy val eventDao = new SlickEventDao
  lazy val placeFinder = new GooglePlaceFinder

  val BLOGS_CACHE = "events"
  val BLOGS_JSON_CACHE = "eventsJson"

  def maybeMainEventPlace(futureMainEvent: Future[Option[Event]]): Future[Option[GooglePlace]] = futureMainEvent.flatMap(
    maybeEvent => {
      if (maybeEvent.isEmpty) {
        Future {
          None
        }
      } else {
        placeFinder.findPlace(maybeEvent.get.location)
      }
    }
  )

  def getResult(futureMainEvent: Future[Option[Event]], futureAndPastEvents: Future[(Seq[Event], Seq[Event])]): Future[Result] = for {
    (upcomingEvents, pastEvents) <- futureAndPastEvents
    maybeMainEvent <- futureMainEvent
    maybePlace <- maybeMainEventPlace(futureMainEvent)
  } yield {

      (upcomingEvents.toList, pastEvents.toList) match {
        case (a +: b, c +: d) => Ok(views.html.events(maybeMainEvent, maybePlace, Some(upcomingEvents), Some(pastEvents)))
        case (a +: b, Nil) => Ok(views.html.events(maybeMainEvent, maybePlace, Some(upcomingEvents), None))
        case (Nil, c +: d) => Ok(views.html.events(maybeMainEvent, maybePlace, None, Some(pastEvents)))
        case (Nil, Nil) => Ok(views.html.events(maybeMainEvent, maybePlace, None, None))
      }
    }

  private def sortIntoFutureAndPast(input: Future[Seq[Event]]): Future[(Seq[Event], Seq[Event])] = {
    input.map {
      _.partition(_.endTime.after(new Date))
    }
  }

  def events = Action.async {
    implicit request => {
      val partitionedEvents: Future[(Seq[Event], Seq[Event])] = sortIntoFutureAndPast(eventDao.allEvents)
      val futureMainEvent: Future[Option[Event]] = partitionedEvents.map { case (future, past) => future.reverse.headOption }

      getResult(futureMainEvent, partitionedEvents)
    }
  }

  def getEvent(id: Long) = Action.async { implicit request => {

    val allEvents = eventDao.allEvents
    val foundEvents = allEvents.map(_.filter(_.id.get.equals(id)))
    val maybeMainEvent = foundEvents.map(_.headOption)
    val remainingEvents = allEvents.map(_.filter(_.id.get != id))

    val partitionedEvents: Future[(Seq[Event], Seq[Event])] = sortIntoFutureAndPast(remainingEvents)

    getResult(maybeMainEvent, partitionedEvents)
  }

  }

  def image(id : Long) = Action.async {
    eventDao.imageById(id).map(img => if (img.isDefined) {Ok(img.get)} else {NotFound("No image found")})
  }

  //Authenticated extends ActionBuilder - here we're calling ACtionBuilder's apply method.
  def createEvent = Authenticated {
    //    clearCache
    Ok(views.html.createEvent(eventForm))
  }

  def updateEvent(id: Long) = Action.async {
    //    clearCache
    val futureEventOption = Event.getById(id)

    val result: Future[Result] = futureEventOption.map(
      _ match {
        case eventOption: Some[Event] => {
          val eventFormData = EventFormData(
            eventOption.get.id,
            eventOption.get.title,
            eventOption.get.location,
            eventOption.get.startTime,
            eventOption.get.endTime,
            eventOption.get.description);
          Ok(views.html.createEvent(eventForm.fill(eventFormData)))
        }
        case _ => NotFound
      })

    result
  }

  def save = Action { implicit request =>
    eventForm.bindFromRequest.fold(
      formWithErrors => {

        Logger.warn("formErrors=" + formWithErrors.errorsAsJson);

        Ok(views.html.createEvent(formWithErrors))
      },

      createdEvent => {
        if (createdEvent.id.isEmpty) {


          Event.create(Event.createFrom(createdEvent))
        }
        else {
          Event.update(Event.createFrom(createdEvent))
        }
        val filledForm: Form[EventFormData] = eventForm.fill(createdEvent)
        println("filled form:")
        println(filledForm.data)

        Ok(views.html.createEvent(filledForm))
      }
    )
  }

  def deleteEvent(id: Int) = Action { implicit request =>
    //    clearCache
    eventDao delete (id)
    Ok(views.html.createEvent(eventForm))
  }

  def eventsJson = Action.async { implicit request =>

    val futureAllEvents: Future[Seq[Event]] = eventDao.sortedById
    val futureJson: Future[Seq[JsValue]] = futureAllEvents.map(_.map(event => Json.obj("id" -> event.id, "title" -> event.title)))

    futureJson.map(jsList => Ok(JsArray(jsList)))
  }

  val eventForm = Form(
    Forms.mapping(
      "id" -> Forms.optional(Forms.longNumber()),
      "title" -> Forms.text,
      "location" -> Forms.text,
      "startTime" -> Forms.date("yyyy-MM-dd'T'HH:mm"),
      "endTime" -> Forms.date("yyyy-MM-dd'T'HH:mm"),
      "description" -> Forms.nonEmptyText.verifying("Description must not be empty", !_.isEmpty)
    )(EventFormData.apply)(EventFormData.unapply)
  )

  // here we are calling the ActionBuilder apply method
  // the apply method can accept a function
  def authenticatedEventForm = Authenticated { request =>
    Ok(views.html.createEvent(eventForm))
  }

  import java.nio.file.{Path, Paths, Files}

  def uploadImage = Action.async(parse.multipartFormData) { request =>

    val id: String = request.body.dataParts.get("id").get.head
    val domainObject: String = request.body.dataParts.get("domainObject").get.head

    request.body.file("image1").map { file =>

      val image = java.nio.file.Files.readAllBytes(file.ref.file.toPath)
      val eventWithImage = eventDao.addImage(id.toLong, image);

      eventWithImage.map(
        b => if (b) {
          Ok("image uploaded")
        } else {
          InternalServerError("upload failed")
        }
      )
    }.getOrElse(Future(BadRequest("image upload")))
  }

  private def clearCache = {
    Cache.remove(BLOGS_CACHE)
    Cache.remove(BLOGS_JSON_CACHE)
  }
}

