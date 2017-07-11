package dao

import play.api.{Logger}
import java.sql.Date
import models.StaticPage
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future

trait StaticPageComponent {
  self: HasDatabaseConfig[JdbcProfile] =>

  class StaticPages(tag: Tag) extends Table[StaticPage](tag, Some("public_bubble"), "static_content") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def content = column[String]("content")

    def imageUrl = column[String]("image_url")

    // the ? method lifts the column into an option
    def * = (id.?, content, imageUrl) <> ((StaticPage.apply _).tupled, StaticPage.unapply)

    // the default projection is StaticPage

  }

}

class SlickStaticPageDao extends HasDatabaseConfig[JdbcProfile] with StaticPageComponent {

  private val staticpages = TableQuery[StaticPages]

  override protected val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def delete(id: Long): Future[Int] = {
    val findById = staticpages.filter(_.id === id)
    dbConfig.db.run(findById.delete)
  }

  def create(staticPage: StaticPage) = {
    dbConfig.db.run(staticpages += staticPage)
  }

 def getFrontPage(): Future[StaticPage] = db.run(staticpages.filter(_.id === 1L).result.head)

}