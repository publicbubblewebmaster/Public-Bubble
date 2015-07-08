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

object Events extends Controller {

  def events = Action {
    val event = Event.getLatest
    Ok(views.html.events(event))

  }


  def createEvent = Authenticated {
    Ok(views.html.createEvent(eventForm))
  }


  def updateEvent(id : Int) = Action {

    val event = Event.getById(id);
    Ok(views.html.createEvent(eventForm.fill(event)))
  }

  def save = Action { implicit request =>
    eventForm.bindFromRequest.fold(
      formWithErrors =>     {
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
      "displayFrom" -> Forms.date("yyyy-MM-dd"),
      "displayUntil" -> Forms.date("yyyy-MM-dd")
    )(Event.apply)(Event.extract)
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


}