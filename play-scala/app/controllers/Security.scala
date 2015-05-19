package controllers

import com.cloudinary._
import com.cloudinary.utils.ObjectUtils
import models.Event
import play.api.Play
import play.api.data.{Form, Forms}
import play.api.libs.json._
import play.api.mvc._

object Security extends Controller {

  def login = Action {
    Ok(views.html.loginForm())
  }
}