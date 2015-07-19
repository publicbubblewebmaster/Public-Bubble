package controllers

import com.cloudinary._
import com.cloudinary.utils.ObjectUtils
import models.{EventFormData, Event}
import play.api.Play.current
import play.api.data.{Form, Forms}
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.mvc._
import play.api.cache.{Cache, Cached}

object EventsController extends Controller {

  val EVENTS_CACHE = "events"

  def events = Cached(EVENTS_CACHE) {
    Action {

      val eventOption = Event.getLatest

      eventOption match {
        case _: Some[Event] => Ok(views.html.events(eventOption.get))
        case _ => Ok(views.html.noContent("events"))
      }
    }
  }

  def createEvent = Authenticated {
    Ok(views.html.createEvent(eventForm))
  }

  def updateEvent(eventId : Long) = Action {
    clearCache
    val event : Event = Event.getById(eventId);

    val (id, title, location, description, displayFrom, displayUntil, image1Url) = Event.unapply(event).get

    val eventFormData = EventFormData(id, title, location, description, displayFrom, displayUntil)

    Ok(views.html.createEvent(eventForm.fill(eventFormData)))
  }

  def save = Action { implicit request =>
    eventForm.bindFromRequest.fold(
      formWithErrors =>     {
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
    clearCache
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
      "displayFrom" -> Forms.sqlDate("yyyy-MM-dd"),
      "displayUntil" -> Forms.sqlDate("yyyy-MM-dd")
    )(EventFormData.apply)(EventFormData.unapply)
  )

  // here we are calling the ActionBuilder apply method
  // the apply method can accept a function
  def authenticatedEventForm = Authenticated { request  =>
    Ok(views.html.createEvent(eventForm))
  }

  import java.nio.file.{Path, Paths, Files}
  def uploadImage = Action(parse.multipartFormData) { request =>

    val id : String = request.body.dataParts.get("id").get.head
    val domainObject : String = request.body.dataParts.get("domainObject").get.head


    request.body.file("image1").map { file =>

      val cloudinary : Cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", "hhih43y5p",
        "api_key", "135878543169511",
        "api_secret", "aLT-f0E8uZ4WdPT20gY9eKoGeYc"));

      val uploadResult = cloudinary.uploader().upload(file.ref.file,
        ObjectUtils.asMap("transformation", new Transformation().width(400))
      );

      val imageUrl = uploadResult.get("url").asInstanceOf[String]

      val eventWithImage = models.Event.addImage(id.toLong, imageUrl);

      Ok("Retrieved file %s" format eventWithImage.image1Url)
    }.getOrElse(BadRequest("File missing!"))
  }

  private def clearCache = Cache.remove(EVENTS_CACHE)


}
