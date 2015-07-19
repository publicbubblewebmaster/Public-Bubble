package controllers

import java.sql.Timestamp

import com.cloudinary.{Transformation, Cloudinary}
import com.cloudinary.utils.ObjectUtils
import dao.{SlickEventDao}
import models.{EventFormData, Event}
import play.api.{Play, Logger}
import play.api.data.{Form, Forms}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.mvc._
import play.api.cache.{Cached, Cache}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Promise, Future}

object EventsController extends Controller {

  lazy val eventDao = new SlickEventDao

  val BLOGS_CACHE = "events"
  val BLOGS_JSON_CACHE = "eventsJson"
  lazy val CLOUD_NAME : String = Play.current.configuration.getString("cloudinary.name").get
  lazy val CLOUD_KEY : String = Play.current.configuration.getString("cloudinary.key").get
  lazy val CLOUD_SECRET : String = Play.current.configuration.getString("cloudinary.secret").get

  def events = Action.async { implicit request =>

    Logger.info(JsObject(Map("message" -> JsString("events retrieved"))).toString)

    eventDao.sortedByEndTime.map {case (eventList) =>
      eventList match {
        case IndexedSeq() => print(eventList); NotFound
        case _ => Ok(views.html.events(eventList.head, eventList.tail))
      }
    }
  }

  def getEvent(id : Long) = Action.async {implicit request =>
    eventDao.sortedByEndTime.map {
      eventList =>
        val (foundEvent, remainingEvents) = eventList partition(_.id.get == id)
        Logger.info(s"foundEvents = $foundEvent remaining=$remainingEvents")
        Ok(views.html.events(foundEvent.head, remainingEvents))
    }
  }

  def createEvent = Authenticated {
    //    clearCache
    Ok(views.html.createEvent(eventForm))
  }

  def updateEvent(id : Long) = Action.async {
    //    clearCache
    val futureEventOption = Event.getById(id)

    val result : Future[Result] = futureEventOption.map(
      _ match {
        case eventOption : Some[Event] => {
            val eventFormData = EventFormData(
                  eventOption.get.id,
                  eventOption.get.title,
                  eventOption.get.location,
                  eventOption.get.startTime,
                  eventOption.get.endTime,
                  eventOption.get.description);
            Ok(views.html.createEvent(eventForm.fill(eventFormData)))}
        case _ => NotFound
      })

    result
  }

  def save = Action { implicit request =>
    eventForm.bindFromRequest.fold(
      formWithErrors =>     {

        Logger.warn("formErrors=" + formWithErrors.errorsAsJson);

        Ok(views.html.createEvent(formWithErrors))},

      createdEvent => {
        if (createdEvent.id.isEmpty) {
          Event.create(Event.createFrom(createdEvent))
        }
        else {
          Event.update(Event.createFrom(createdEvent))
        }
        Ok(views.html.createEvent(eventForm.fill(createdEvent)))
      }
    )
  }

  def deleteEvent(id : Int)= Action { implicit request =>
    //    clearCache
    eventDao delete(id)
    Ok(views.html.createEvent(eventForm))
  }

  def eventsJson = Action.async { implicit request =>

    val futureEvents : Future[Seq[Event]] = eventDao.sortedById
    val futureJson : Future[Seq[JsValue]] = futureEvents.map(_.map(event => Json.obj("id" -> event.id, "title" -> event.title)))

    futureJson.map(jsList => Ok(JsArray(jsList)))
  }

  val eventForm = Form(
    Forms.mapping(
      "id" -> Forms.optional(Forms.longNumber()),
      "title" -> Forms.text,
      "location" -> Forms.text,
      "startTime" -> Forms.date("yyyy-MM-dd'T'HH:mm"),
      "endTime" -> Forms.date("yyyy-MM-dd'T'HH:mm"),
      "description" -> Forms.nonEmptyText verifying ("enter something", !_.isEmpty)
    )(EventFormData.apply)(EventFormData.unapply)
  )

  // here we are calling the ActionBuilder apply method
  // the apply method can accept a function
  def authenticatedEventForm = Authenticated { request  =>
    Ok(views.html.createEvent(eventForm))
  }

  import java.nio.file.{Path, Paths, Files}
  def uploadImage = Action.async(parse.multipartFormData) { request =>


    val id: String = request.body.dataParts.get("id").get.head
    val domainObject: String = request.body.dataParts.get("domainObject").get.head

    request.body.file("image1").map { file =>

      val cloudinary: Cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", CLOUD_NAME,
        "api_key", CLOUD_KEY,
        "api_secret", CLOUD_SECRET));

      val uploadResult = cloudinary.uploader().upload(file.ref.file,
        ObjectUtils.asMap("transformation", new Transformation().width(800), "transformation", new Transformation().height(370))
      );

      val imageUrl = uploadResult.get("url").asInstanceOf[String]

      val eventWithImage = eventDao.addImage(id.toLong, imageUrl);

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

