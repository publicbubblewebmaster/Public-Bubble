package controllers

import play.api.mvc._
import dao.{SlickCommitteeDao, SlickStaticPageDao}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object Application extends Controller {

  lazy val HOME_404 = NotFound(views.html.noContent("Home"))
  lazy val committeDao = new SlickCommitteeDao()
  lazy val frontpageDao = new SlickStaticPageDao()

  def aboutUs = Action {
    val committee = Await.result(committeDao.listMembers, Duration(5, "seconds"))
    val frontpage = frontpageDao.getFrontPage()
    Ok(views.html.aboutUs(frontpage, committee))
  }

  def twitter = Action {
    Ok(views.html.twitter("Twitter"))
  }

  def facebook = Action {
    Ok(views.html.facebook())
  }
}