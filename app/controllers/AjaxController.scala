package controllers

import dao.{SlickCommiteeDao, SlickStaticPageDao}
import models.Member
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AjaxController extends Controller {

  lazy val HOME_404 = NotFound(views.html.noContent("Home"))

  lazy val committeDao = new SlickCommiteeDao()
  lazy val frontpageDao = new SlickStaticPageDao()

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

  def deleteCommiteeMember(id: Long) = Action {
    val commitee = Await.result(committeDao.delete(id), Duration(5, "seconds"))
    Redirect("/update/frontpage")
  }

}