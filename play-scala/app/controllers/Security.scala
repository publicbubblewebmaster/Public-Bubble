package controllers

import akka.actor.FSM.->
import com.cloudinary._
import com.cloudinary.utils.ObjectUtils
import models.Event
import play.api.Play
import play.api.data.{Form, Forms}
import play.api.libs.json._
import play.api.mvc._


case class UserData(id : String)

object Security extends Controller {

  val userFormTuple = Form(
    Forms.single("id" -> Forms.text) // tuples come with built-in apply/unapply
  )

  def showLoginForm = Action {
    Ok(views.html.loginForm())
  }

}

