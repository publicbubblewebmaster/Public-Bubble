package models

import java.sql.Date

case class Blog (
                   id : Option[Long],
                   title: String,
                   author: String,
                   intro: String,
                   content: String,
                   publishDate: Date,
                   image1Url: Option[String] = null)
case class BlogFormData(id : Option[Long], title : String, author : String, intro : String, content : String, publishDate : Date)

object Blog {

  def createFrom(blogFormData : BlogFormData) : Blog =
    Blog(blogFormData.id, blogFormData.title, blogFormData.author, blogFormData.intro, blogFormData.content, blogFormData.publishDate, None)

  def extract(blog: Blog) = {
    Option(Tuple6(blog.id, blog.title, blog.author, blog.intro, blog.content, blog.publishDate))
  }
}

