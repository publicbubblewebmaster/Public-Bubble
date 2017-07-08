package dao

import models.Blog

import scala.concurrent.Future

trait UserDao {

  def isAuthenticated

}