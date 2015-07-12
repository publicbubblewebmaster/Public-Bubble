package dao

import java.util.Date

import anorm.Row
import models.Blog
import play.api.db.DB

/**
 * Created by ian on 11/07/15.
 */
trait BlogDao {

  def update(blog : Blog)

  def addImage(id : Long, url : String) : Blog

  def getById(blogId : Long) : Blog

  def delete(id : Int)

  def getAll : List[Blog]

  def getLatest : Option[Blog]

  def create(blog : Blog) : Option[Long]
  }
