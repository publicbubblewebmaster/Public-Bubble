package models

import anorm._
import com.google.api.client.googleapis.auth.oauth2.{GoogleIdToken, GoogleIdTokenVerifier}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import play.api.Play

import scala.collection.JavaConversions;

case class User(email: String, role : String) {

}

object User {


  val FIND_ROLE_BY_EMAIL : SqlQuery = SQL("SELECT role FROM public_bubble.user WHERE email = {email}")

  val HTTP_TRANSPORT = new NetHttpTransport();
  val JSON_FACTORY = new JacksonFactory();

  val CLIENT_ID = Play.current.configuration.getString("google.clientId").get
  val APPS_DOMAIN_NAME = "localhost";

  val verifier : GoogleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
    .setAudience(JavaConversions.asJavaCollection(List(CLIENT_ID)))
    .build();

  def authenticatedUser(idTokenString: String): Option[User] = {

    val idToken: GoogleIdToken = verifier.verify(idTokenString);
    if (idToken != null) {
      val payload = idToken.getPayload();
      if (true) {

        val email = payload.getEmail();
        val roleOption: Option[String] = Some("Admin")// TODO decide on proper implementation findRoleByEmail(payload.getEmail);

        if (!roleOption.isEmpty) {
          roleOption.get.equalsIgnoreCase("ADMIN")
          Option(User(email, "ADMIN"))
        } else {
          None
        }
      } else {
        println("Invalid ID token.");
        None
      }
    } else {
      println("Invalid ID token.");
      None
    }
  }

//  def findRoleByEmail(email : String) : Option[String] = {
//    DB.withConnection {
//      implicit connection =>
//        val role: Option[String] = FIND_ROLE_BY_EMAIL.on("email" -> email).as(SqlParser.scalar[String].singleOpt)
//        role
//    }
//  }
}