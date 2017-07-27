package controllers

import java.nio.file.Paths
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob

import dao.{SlickCommitteeDao, SlickStaticPageDao}
import models.Member
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object FrontpageController extends Controller {

  lazy val HOME_404 = NotFound(views.html.noContent("Home"))

  lazy val frontpageDao = new SlickStaticPageDao()

  def frontpageEditor = Authenticated {
    val frontpage = frontpageDao.getFrontPage()
    Ok(views.html.editFrontpage(frontpage))
  }

  def image = Action {
    val imageMaybe = frontpageDao.getFrontPage().image
    imageMaybe match {
      case Some(image) =>
        Ok(image)
      case None =>
        NotFound("Frontpage image not found")
    }
  }

  def updateFrontpage = Authenticated(parse.multipartFormData) {
    request =>
      val content: String = request.body.dataParts.get("intro").get.head
      val photo: Option[MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile]] = request.body.files.filter(_.key == "image1").headOption

      val fileforUpload : Option[Array[Byte]] = photo.filter(f => !f.ref.file.getName.isEmpty).map(f => java.nio.file.Files.readAllBytes(f.ref.file.toPath))




      val updatedFrontpage = frontpageDao.updateFrontpage(content, fileforUpload.orNull);

      Await.result(updatedFrontpage.map(
        b => if (b) {
          Redirect("/update/introduction")
        } else {
          InternalServerError("Error updating frontpage")
        }
      ), Duration(10, "seconds"))
  }

  def twitter = Action {
    Ok(views.html.twitter("Twitter"))
  }

  def facebook = Action {
    Ok(views.html.facebook())
  }
}