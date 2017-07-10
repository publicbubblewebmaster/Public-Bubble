package controllers

import play.api.mvc._
import dao.CommitteeDao

object Application extends Controller {

  def aboutUs = Action {
    val dao = new CommitteeDao()
    Ok(views.html.aboutUs(dao.listMembers()))
  }

  def twitter = Action {
    Ok(views.html.twitter("Twitter"))
  }

  def facebook = Action {
    Ok(views.html.facebook())
  }
}