package controllers

import com.cloudinary.utils.ObjectUtils
import models.Event
import play.api.Play
import play.api.data.{Form, Forms}
import play.api.libs.json._
import play.api.mvc._
import com.cloudinary._

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


  import java.nio.file.{Path, Paths, Files}
  def uploadImage = Action(parse.multipartFormData) { request =>

    val id : String = request.body.dataParts.get("id").get.head
    val domainObject : String = request.body.dataParts.get("domainObject").get.head

    val imageFolder: String = Play.current.configuration.getString("image.folder").get

    request.body.file("image1").map { file =>

      val cloudinary : Cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", "eventWithImage",
        "api_key", "135878543169511",
        "api_secret", "aLT-f0E8uZ4WdPT20gY9eKoGeYc"));

      val uploadResult = cloudinary.uploader().upload(file.ref.file, null);

      val imageUrl = uploadResult.get("url").asInstanceOf[String]

      val eventWithImage = Event.addImage(id.asInstanceOf[Long], imageUrl);

      Ok("Retrieved file %s" format eventWithImage.image1Url)
    }.getOrElse(BadRequest("File missing!"))
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
}