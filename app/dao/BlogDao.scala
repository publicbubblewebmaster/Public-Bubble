package dao

import models.Blog
import scala.concurrent.Future

trait BlogDao {

  def update(blog : Blog)

  def addImage(id : Long, url : Array[Byte]) : Future[Boolean]

  def findById(blogId : Long) : Future[Option[Blog]]

  def delete(id : Long) : Future[Int]

  def sortedByDate : Future[Seq[Blog]]

  def sortedById : Future[Seq[Blog]]

  def create(blog : Blog)
  }
