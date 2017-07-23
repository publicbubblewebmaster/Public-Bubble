package controllers

import dao.{SlickCommitteeDao, SlickStaticPageDao}
import models.Member
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AjaxController extends Controller {

  lazy val HOME_404 = NotFound(views.html.noContent("Home"))

  lazy val committeDao = new SlickCommitteeDao()
  lazy val frontpageDao = new SlickStaticPageDao()

  implicit val memberWrites = new Writes[Member] {
    def writes(member: Member) = Json.obj(
      "description" -> member.description,
      "technicalId" -> member.id,
      "position" -> member.position
    )
  }

  def jsonCommittee = Action {
    val committee = Await.result(committeDao.listMembers, Duration(5, "seconds"))
    Ok(Json.toJson(committee))
  }

  def deleteCommitteeMember(id: Long) = Action {
    val committee = Await.result(committeDao.delete(id), Duration(5, "seconds"))
    Redirect("/update/frontpage")
  }

}