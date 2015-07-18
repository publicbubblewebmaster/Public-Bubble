package models

import java.sql.Date
import scala.concurrent.Future

import dao.SlickBlogDao

case class Blog (
                   id : Option[Long],
                   title: String,
                   author: String,
                   intro: String,
                   content: String,
                   publishDate: Date,
                   image1Url: Option[String] = null)

/* Same as model class but missing image url because this is handled via ajax*/
case class BlogFormData(id : Option[Long],
                        title : String,
                        author : String,
                        intro : String,
                        content : String,
                        publishDate : Date)

object Blog {

  lazy val dao = new SlickBlogDao

  def createFrom(blogFormData: BlogFormData): Blog =
    Blog(blogFormData.id, blogFormData.title, blogFormData.author, blogFormData.intro, blogFormData.content, blogFormData.publishDate, None)

  def extract(blog: Blog) = {
    Option(Tuple6(blog.id, blog.title, blog.author, blog.intro, blog.content, blog.publishDate))
  }

  def update(blog: Blog): Unit = dao.update(blog)

  def addImage(id: Long, url: String): Future[Boolean] = dao.addImage(id, url)

  def delete(id: Int): Unit = dao.delete(id)

  def getById(blogId: Long): Future[Option[Blog]] = dao.findById(blogId)

  def create(blog: Blog) = dao.create(blog)

}