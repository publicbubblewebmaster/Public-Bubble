package controllers

import models.User
import org.apache.commons.mail.{DefaultAuthenticator, SimpleEmail}
import play.api.Play
import play.api.data.{Form, Forms}
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.Future

object Email extends Controller {

  val emailForm = Form(
    Forms.tuple(
      "subject" -> Forms.text,
      "from" -> Forms.text,
      "body" -> Forms.text
    )
  )

  def emailUs = Action {
    Ok(views.html.emailUs())
  }

  def sendEmail = Action {
    implicit request =>
      val emailTuple3: (String, String, String) = emailForm.bindFromRequest().get

//      val emailUser = Play.current.configuration.getString("email.username").get
//      val emailPassword = Play.current.configuration.getString("email.password").get

      var email = new SimpleEmail;
      email.setHostName("smtp.googlemail.com");
      email.setSmtpPort(465);
      email.setAuthenticator(new DefaultAuthenticator("ian.wowcher@gmail.com", "alp1nemarm0t"));
      email.setSSLOnConnect(true);
      email.setSubject(emailTuple3._1);
      email.setMsg(emailTuple3._3);
      email.send;

      Ok(views.html.emailUs())

  }
}
