package controllers

import com.cloudinary.utils.ObjectUtils
import models.Event
import play.api.Play
import play.api.data.{Form, Forms}
import play.api.libs.Files.TemporaryFile
import play.api.libs.json._
import play.api.mvc._
import com.cloudinary._
import play.api.i18n.Messages.Implicits._
import play.api.i18n._
import play.api.Play.current

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def twitter = Action {
    Ok(views.html.twitter("Twitter"))
  }

  def facebook = Action {
    Ok(views.html.facebook())
  }


 }