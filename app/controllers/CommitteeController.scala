package controllers

import java.nio.file.Files

import dao.{SlickCommitteeDao, SlickStaticPageDao}
import models.Member
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object CommitteeController extends Controller {

  lazy val HOME_404 = NotFound(views.html.noContent("Home"))

  lazy val committeDao = new SlickCommitteeDao()

  def committeeEditor = Authenticated {
    val committee = Await.result(committeDao.listMembers, Duration(5, "seconds"))
    Ok(views.html.editCommittee(committee))
  }

  def updateCommittee = Action.async(parse.multipartFormData) { request =>
    var members = scala.collection.mutable.Map[String, Member]()
    val pattern = """.+\[([0-9]+)\].+""".r
    val indices = request.body.dataParts.keys.map(pattern.findFirstMatchIn(_).get.group(1)).toSet

    indices.foreach(memberIdx => {
      val photo: Option[MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile]] = request.body.files.filter(_.key == s"member[$memberIdx]image").headOption
      val description: Option[String] = request.body.dataParts.get(s"member[$memberIdx]description").headOption.flatMap(_.headOption)
      val technicalId: Option[String] = request.body.dataParts.get(s"member[$memberIdx]id").headOption.flatMap(_.headOption)
      val position: Long = request.body.dataParts.get(s"member[$memberIdx]position").head.head.toLong
      val fileforUpload = photo.filter(f => !f.ref.file.getName.isEmpty).map(f => Files.readAllBytes(f.ref.file.toPath))

      if (technicalId.isDefined && !technicalId.get.isEmpty) {
        committeDao.update(Member(technicalId.map(_.toLong), description.get, fileforUpload, position)).foreach(_ => println("UPDATED"))
      } else {
        committeDao.create(Member(None, description.getOrElse("EMPTY"), fileforUpload, position)).foreach(_ => println("CREATED"))
      }
    })
    Future(Redirect("/update/committee"))
  }

  def image(id : Long) = Action.async {
   committeDao.imageById(id).map(
     maybeImage => {
       if (maybeImage.isDefined) {
         Ok(maybeImage.get)
       } else {
         NotFound("Committee member image missing")
       }
     }

   )
  }

}