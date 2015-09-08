package controllers

import models.User
import play.api.Play
import play.api.data.{Form, Forms}
import play.api.Logger
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class UserData(id: String)

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

  def handleLogin = Action.async {
    implicit request =>
      userFormSingle.bindFromRequest().fold(
        errorForm => {
          Future {
            Unauthorized(views.html.unauthorized())
          }
        },
        boundId => {

          User.eventuallyAuthenticateUser(boundId).map {
            authenticatedUserOption =>
              if (!authenticatedUserOption.isEmpty) {
                Ok(views.html.portal("You are logged in as " + authenticatedUserOption.get.email + "!"))
                  //the session is signed by the framework so the client cannot tamper with it
                  .withSession("email" -> authenticatedUserOption.get.email)
              } else {
                Logger.error("{error : 'User could not be authenticated'} ")
                Unauthorized(views.html.unauthorized())
              }
          }
        })
  }
}