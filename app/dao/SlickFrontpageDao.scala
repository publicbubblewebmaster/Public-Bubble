package dao

import play.api.Logger
import java.sql.{Blob, Date}

import models.{Blog, StaticPage}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

trait StaticPageComponent {
  self: HasDatabaseConfig[JdbcProfile] =>

  class StaticPages(tag: Tag) extends Table[StaticPage](tag, Some("public_bubble"), "static_content") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def content = column[String]("content")

    def image = column[Array[Byte]]("image")

    // the ? method lifts the column into an option
    def * = (id.?, content, image) <> ((StaticPage.apply _).tupled, StaticPage.unapply)

    // the default projection is StaticPage

  }

}

class SlickStaticPageDao extends HasDatabaseConfig[JdbcProfile] with StaticPageComponent {

  private val staticpages = TableQuery[StaticPages]

  override protected val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def updateFrontpage(content: String, image: Array[Byte]): Future[Boolean] = {
    val query = for {b <- staticpages if b.id === 1L} yield (b.id, b.content, b.image)
    var imageForUpdate = if (image == null) {getFrontPage().image} else image
    db.run(query.update(1L, content, imageForUpdate)).map(_ == 1)
  }

  def create(staticPage: StaticPage) = {
    dbConfig.db.run(staticpages += staticPage)
  }

  def getFrontPage(): StaticPage = Await.result(db.run(staticpages.filter(_.id === 1L).result.head), Duration(10, "seconds"))

}