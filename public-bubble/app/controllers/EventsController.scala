package controllers

import java.sql.Timestamp

import _root_.util.{GooglePlace, GooglePlaceFinder}
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
import scala.concurrent.{Promise, Future}
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global

object EventsController extends Controller {

  lazy val eventDao = new SlickEventDao
  lazy val placeFinder = new GooglePlaceFinder

  val BLOGS_CACHE = "events"
  val BLOGS_JSON_CACHE = "eventsJson"
  lazy val CLOUD_NAME : String = Play.current.configuration.getString("cloudinary.name").get
  lazy val CLOUD_KEY : String = Play.current.configuration.getString("cloudinary.key").get
  lazy val CLOUD_SECRET : String = Play.current.configuration.getString("cloudinary.secret").get

  def events = Action.async { implicit request =>

    val eventsList = eventDao.sortedByEndTime

    val result : Future[Result] = for {
      list     <- eventsList
      maybePlace <- placeFinder.findPlace(list.head.location)
    } yield {
        Ok(views.html.events(list.head, list.tail, maybePlace))
      }
    result
  }

  /*def getEvent(id : Long) = Action.async { implicit request =>
    val dbResult: Future[(Seq[Event], Seq[Event])] = eventDao.sortedByEndTime.map {
      eventList =>
        val (foundEvent, remainingEvents) = eventList partition (_.id.get == id)
        Logger.info(s"foundEvents = $foundEvent remaining=$remainingEvents")

        (foundEvent, remainingEvents)

//        remainingEvents ++ remainingEvents
    }

    TODO()
  }*/

  //Authenticated extends ActionBuilder - here we're calling ACtionBuilder's apply method.
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
          val filledForm : Form[EventFormData] = eventForm.fill(eventFormData)
          println("filled form:")
          println(filledForm.data)

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
        val filledForm : Form[EventFormData] = eventForm.fill(createdEvent)
        println("filled form:")
        println(filledForm.data)

        Ok(views.html.createEvent(filledForm))
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
      "description" -> Forms.nonEmptyText.verifying("Description must not be empty", !_.isEmpty)
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

