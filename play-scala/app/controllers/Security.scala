package controllers

import models.User
import play.api.Play
import play.api.data.{Form, Forms}
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.Future

case class UserData(id : String)

object Security extends Controller {

  val userFormSingle = Form(
    Forms.single("id" -> Forms.text)
  )

  def showLoginForm = Action {
    Ok(views.html.loginForm())
  }

  def logout = Action {
    Ok(views.html.logout())
  }

  def handleLogin = Action {
    implicit request =>
      userFormSingle.bindFromRequest().fold(
        errorForm => {Unauthorized(views.html.unauthorized())},
        boundId => {
          val authenticatedUserOption: Option[User] = User.authenticatedUser(boundId);

          if (!authenticatedUserOption.isEmpty) {
            Ok(views.html.portal("You are logged in as "+ authenticatedUserOption.get.email +"!"))
              //the session is signed by the framework so the client cannot tamper with it
              .withSession("email" -> authenticatedUserOption.get.email)
          } else {
            Unauthorized(views.html.unauthorized())
          }
        }
      )
  }
}