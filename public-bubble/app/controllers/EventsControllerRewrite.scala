package controllers

import _root_.util.{GooglePlace, GooglePlaceFinder}
import com.cloudinary.{Cloudinary, Transformation}
import dao.SlickEventDao
import models.{Event, EventFormData}
import play.api.data.{Form, Forms}
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

import scala.concurrent.Future

object EventsControllerRewrite extends Controller {

  lazy val eventDao = new SlickEventDao
  lazy val placeFinder = new GooglePlaceFinder

  def events = Action.async { implicit request =>

    val eventsList = eventDao.sortedByEndTime

   val result : Future[Result] = for {
      list     <- eventsList
      maybePlace <- placeFinder.findPlace(list.head.location)
    } yield {
        println(maybePlace)
        Ok(views.html.events(list.head, list.tail, maybePlace))
    }
  result
  }
}

