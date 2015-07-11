package tables

import slick.driver.PostgresDriver.api._

trait BlogTable {

  // Definition of the SUPPLIERS table
  class Blog(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {
    def id = column[Int]("ID", O.PrimaryKey) // This is the primary key column
    def title = column[String]("TITLE")
    def author = column[String]("AUTHOR")
    def intro = column[String]("INTRO")
    def content = column[String]("CONTENT")
    def publishDate = column[String]("PUBLISH_DATE")
    def image1url = column[String]("IMAGE_1_URL")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, title, author, intro, content, image1url)
  }
  val blogs = TableQuery[Blog]
  
}
