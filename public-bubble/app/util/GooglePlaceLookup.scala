package util

import javax.xml.ws.WebServiceClient

import play.api.Play
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.functional.syntax._
import play.api.libs.ws.{WSResponse, WS, WSRequest}
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

case class GooglePlace(id : String, name : String, formattedAddress : String, coords : (Double, Double))

trait GooglePlaceJsonParser {

  private val readId : Reads[String] = {(__ \ "place_id").read[String]}
  private val readName : Reads[String] = {(__ \ "name").read[String]}
  private val readFormattedAddress : Reads[String] = {(__ \ "formatted_address").read[String]}
  private val readLocation : Reads[JsObject] = {(__ \ "geometry" \ "location").read[JsObject]}
  private val readCoordinates : Reads[(Double, Double)] = {
    ((__ \ "lat").read[Double]
      and
      (__ \ "lng").read[Double]).
      apply((_, _))}

  private val readResults : Reads[Seq[JsObject]] = {
    (__ \ "results").read[Seq[JsObject]]
  }

  private val readGooglePlace : Reads[GooglePlace] = {
    (readId and
      readName and
      readFormattedAddress and
      readLocation.andThen(readCoordinates))(GooglePlace.apply _)
  }

  private val maybeReadFirstResult : Reads[Option[JsObject]] = {
    readResults.map(_.headOption)
  }

  val readPlace : Reads[Option[GooglePlace]] = {
    maybeReadFirstResult.map(_.map(_.as[GooglePlace](readGooglePlace)))
  }

}

import play.api.Play.current
import play.api.Logger

class GooglePlaceFinder extends GooglePlaceJsonParser {

  lazy val googleApiKey = Play.current.configuration.getString("google.api.serverKey").get

  def findPlace(query:String): Future[Option[GooglePlace]] = {
      val url = s"https://maps.googleapis.com/maps/api/place/textsearch/json?key=$googleApiKey&query=$query"
    println(url)
      val request : WSRequest = WS.url(url)
      val futureResponse : Future[WSResponse] = request.get()

    futureResponse.onSuccess{case response : WSResponse => {

      Logger.debug(s"request=$url\n response=${response.body}")

      println(s"response = $response" )}}


      futureResponse.map(_.json.as[Option[GooglePlace]](readPlace))
  }
}