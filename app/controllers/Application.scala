package controllers

import models.Member
import play.api.mvc._
import dao.{SlickCommiteeDao, SlickStaticPageDao}

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
    val frontpage = Await.result(frontpageDao.getFrontPage, Duration(5, "seconds"))
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

  def updateFrontpage = Action {
    val frontpage = Await.result(frontpageDao.getFrontPage, Duration(5, "seconds"))
    val commitee = Await.result(committeDao.listMembers, Duration(5, "seconds"))
    Ok(views.html.editFrontpage(frontpage, commitee))
  }

  def twitter = Action {
    Ok(views.html.twitter("Twitter"))
  }

  def facebook = Action {
    Ok(views.html.facebook())
  }
}