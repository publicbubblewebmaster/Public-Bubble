/*
package tables

import models.Blog
import slick.lifted.TableQuery
import slick.driver.PostgresDriver.api._

trait BlogTable {

  // Definition of the BLOG table
  class Blog(tag: Tag) extends Table[Blog](tag, "SUPPLIERS") {
    def id = column[Int]("ID", O.PrimaryKey) // This is the primary key column
    def title = column[String]("TITLE")
    def author = column[String]("AUTHOR")
    def intro = column[String]("INTRO")
    def content = column[String]("CONTENT")
    def publishDate = column[String]("PUBLISH_DATE")
    def image1url = column[String]("IMAGE_1_URL")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, title, author, intro, content, image1url) <> (Blog.apply, Blog.extract)
  }
  val blogs = TableQuery[Blog]

}*/
