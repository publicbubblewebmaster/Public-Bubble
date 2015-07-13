package dao

import models.Blog
import scala.concurrent.Future

/**
 * Created by ian on 11/07/15.
 */
trait BlogDao {

  def update(blog : Blog)

  def addImage(id : Long, url : String) : Blog

  def findById(blogId : Long) : Future[Option[Blog]]

  def delete(id : Int)

  def getAll : List[Blog]

  def getLatest : Option[Blog]

  def create(blog : Blog) : Option[Long]
  }
