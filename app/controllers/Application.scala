package controllers

import com.cloudinary.Transformation
import com.cloudinary.utils.ObjectUtils
import controllers.BlogsController.{BadRequest, InternalServerError, Ok, cloudinary}
import models.{Member, StaticPage}
import play.api.mvc._
import dao.{SlickCommiteeDao, SlickStaticPageDao}
import play.api.data._
import play.api.data.Forms._
import play.api.libs.Files

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._

object Application extends Controller {

  lazy val HOME_404 = NotFound(views.html.noContent("Home"))


  lazy val committeDao = new SlickCommiteeDao()
  lazy val frontpageDao = new SlickStaticPageDao()

  def aboutUs = Action {
    val commitee = Await.result(committeDao.listMembers, Duration(5, "seconds"))
    val frontpage = frontpageDao.getFrontPage()
    Ok(views.html.aboutUs(frontpage, commitee))
  }

  implicit val locationWrites = new Writes[Member] {
    def writes(location: Member) = Json.obj(
      "title" -> location.title,
      "imageUrl" -> location.imageUrl
    )
  }

  def jsonCommitee = Action {
    val commitee = Await.result(committeDao.listMembers, Duration(5, "seconds"))
    Ok(Json.toJson(commitee))
  }

  def frontpageEditor = Action {
    val frontpage = frontpageDao.getFrontPage()
    val commitee = Await.result(committeDao.listMembers, Duration(5, "seconds"))
    Ok(views.html.editFrontpage(frontpage, commitee))
  }

  def updateCommitee = Action.async(parse.multipartFormData) { request =>
    /* Deal with
    *
    * List(FilePart(member[0]image,nasa.jpg,Some(image/jpeg),TemporaryFile(/tmp/multipartBody9152114886734519325asTemporaryFile)),
    *      FilePart(member[1]image,vs300_boarding_pass.jpeg,Some(image/jpeg),TemporaryFile(/tmp/multipartBody4550472096845962971asTemporaryFile)),
    *      FilePart(member[2]image,download.jpg,Some(image/jpeg),TemporaryFile(/tmp/multipartBody1112734292670041851asTemporaryFile)))
    *      Map(
    *       member[2]position -> List(asdf3232323),
    *       member[1]position -> List(asdf333),
    *       member[0]position -> List(asdf))
    *
    * */

    var members = scala.collection.mutable.Map[String, Member]()
    for ((k, v) <- request.body.dataParts) {
      val pattern = """member\[([0-9]+)\]position""".r
      val pattern(memberIdx) = k
      val photo: Option[MultipartFormData.FilePart[Files.TemporaryFile]] = request.body.files.filter(_.key == s"member[$memberIdx]image").headOption
      if (photo.nonEmpty) {
        println("TODO: upload file")
      }
      members.put(k, Member(Some(1L), "title", "imageUrl"))
    }

    println(request.body.files)
    println(request.body.dataParts)
    Future(Ok("TODO Commitee Upload and Commitee rename"))
  }

  def updateFrontpage = Action.async(parse.multipartFormData) { request =>

    val content: String = request.body.dataParts.get("intro").get.head
    val file = request.body.file("image1").orNull

    if (file != null) {
      /*      val uploadResult = cloudinary.uploader().upload(file.ref.file,
        ObjectUtils.asMap("transformation", new Transformation().width(120), "transformation", new Transformation().height(120))
      );

      val imageUrl = uploadResult.get("url").asInstanceOf[String]*/

    }


    val updatedFrontpage = frontpageDao.updateFrontpage(content, "imageUrl");

    updatedFrontpage.map(
      b => if (b) {
        Redirect("/update/frontpage")
      } else {
        InternalServerError("Error updating frontpage")
      }
    )
  }


  def twitter = Action {
    Ok(views.html.twitter("Twitter"))
  }

  def facebook = Action {
    Ok(views.html.facebook())
  }
}