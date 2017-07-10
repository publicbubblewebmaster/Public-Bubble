package controllers
import models.Member
import play.api.mvc._
import dao.SlickCommiteeDao

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global



object Application extends Controller {

  lazy val HOME_404 = NotFound(views.html.noContent("Home"))


  lazy val committeDao = new SlickCommiteeDao()

  def aboutUs = Action.async { implicit request =>

    committeDao.listMembers.map {commitee =>
      Ok(views.html.aboutUs(commitee))

    }.fallbackTo(Future{HOME_404})
  }



  def twitter = Action {
    Ok(views.html.twitter("Twitter"))
  }

  def facebook = Action {
    Ok(views.html.facebook())
  }
}