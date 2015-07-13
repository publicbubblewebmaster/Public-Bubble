package dao

import java.sql.Date

import models.Blog
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future

trait BlogsComponent {
  self: HasDatabaseConfig[JdbcProfile] =>

  class Blogs(tag: Tag) extends Table[Blog](tag, Some("PUBLIC_BUBBLE"), "BLOG") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("TITLE")
    def author = column[String]("AUTHOR")
    def intro = column[String]("INTRO")
    def content = column[String]("CONTENT")
    def publishDate = column[Date]("PUBLISH_DATE")
    def image1Url = column[Option[String]]("IMAGE_1_URL")

    /* It is possible to define a mapped table
    that uses a custom type for its * projection
    by adding a bi-directional mapping with the <> operator:

    The <> operator is optimized for case classes*/


    // the ? method lifts the column into an option
    def * = (id.?, title, author, intro, content, publishDate, image1Url) <> ((Blog.apply _).tupled, Blog.unapply)
    // the default projection is Blog

  }

}

class SlickBlogDao extends HasDatabaseConfig[JdbcProfile] with BlogDao with BlogsComponent  {

  private val blogs = TableQuery[Blogs]

  override protected val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  override def update(blog: Blog): Unit = ???

  override def addImage(id: Long, url: String): Blog = ???

  override def delete(id: Int): Unit = ???

  override def findById(blogId: Long): Future[Option[Blog]] = db.run(blogs.filter(_.id === blogId).result.headOption)

  override def create(blog: Blog): Option[Long] = ???

  override def getLatest: Option[Blog] = ???

  override def getAll: List[Blog] = ???

}