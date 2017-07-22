package models

import scala.concurrent.Future
import com.google.api.client.googleapis.auth.oauth2.{GoogleIdToken, GoogleIdTokenVerifier}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import dao.SlickUserDao
import play.api.{Play, Logger}

import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.JavaConversions;

case class User(email: String, role : String)

object User {

  lazy val userDao = new SlickUserDao

//  val FIND_ROLE_BY_EMAIL : SqlQuery = SQL()

  val HTTP_TRANSPORT = new NetHttpTransport();
  val JSON_FACTORY = new JacksonFactory();

  val CLIENT_ID = Play.current.configuration.getString("google.oauth.clientId").get
  val APPS_DOMAIN_NAME = "localhost";

  val verifier : GoogleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
    .setAudience(JavaConversions.asJavaCollection(List(CLIENT_ID)))
    .build();

  def eventuallyAuthenticateUser(idTokenString: String): Future[Option[User]] = {

    val idToken: GoogleIdToken = verifier.verify(idTokenString);
    if (idToken != null) {
      val payload = idToken.getPayload();

      val email = payload.getEmail();
      Logger.info(s"{event : 'user with the email address '$email' has requested authentication'}")

      userDao.hasRole(email, "ADMIN").map(
        isAdmin => {
          if (isAdmin) {
            Some(User(email, "ADMIN"))
          } else {
            None
          }
        }
      )
    } else {
      Future {
        None
      }
    }
  }
}