package controllers

import java.nio.file.Paths

import controllers.FrontpageController.upload
import dao.{SlickCommitteeDao, SlickStaticPageDao}
import models.Member
import play.api.mvc._
import util.CloudinaryUploader

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object CommitteeController extends Controller with CloudinaryUploader {

  lazy val HOME_404 = NotFound(views.html.noContent("Home"))

  lazy val committeDao = new SlickCommitteeDao()

  def committeeEditor = Authenticated {
    val committee = Await.result(committeDao.listMembers, Duration(5, "seconds"))
    Ok(views.html.editCommittee(committee))
  }

  def updateCommittee = Action.async(parse.multipartFormData) { request =>
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

    val pattern = """.+\[([0-9]+)\].+""".r


    val indices = request.body.dataParts.keys.map(pattern.findFirstMatchIn(_).get.group(1)).toSet

    indices.foreach(memberIdx => {
      val photo: Option[MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile]] = request.body.files.filter(_.key == s"member[$memberIdx]image").headOption
      val description: Option[String] = request.body.dataParts.get(s"member[$memberIdx]description").headOption.flatMap(_.headOption)
      val technicalId: Option[String] = request.body.dataParts.get(s"member[$memberIdx]id").headOption.flatMap(_.headOption)
      val position: Long = request.body.dataParts.get(s"member[$memberIdx]position").head.head.toLong


      val filename = s"${java.time.LocalDateTime.now().getNano}.png"
      val fileforUpload = photo.filter(f => !f.ref.file.getName.isEmpty).map(f => upload(f.ref.file, 800, 370))


      if (technicalId.isDefined && !technicalId.get.isEmpty) {

        committeDao.update(Member(technicalId.map(_.toLong), description.get, fileforUpload.orNull, position)).foreach(_ => println("UPDATED"))
      } else {
        committeDao.create(Member(None, description.getOrElse("EMPTY"), s"/assets/images/$filename", position)).foreach(_ => println("CREATED"))
      }
    })
    Future(Redirect("/update/committee"))
  }

}