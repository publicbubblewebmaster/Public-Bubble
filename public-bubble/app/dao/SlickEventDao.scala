package dao

import java.sql.{Timestamp, Date}

import models.Event
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future

trait EventsComponent {
  self: HasDatabaseConfig[JdbcProfile] =>

  class Events(tag: Tag) extends Table[Event](tag, Some("public_bubble"), "event") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def location = column[String]("location")
    def startTime = column[Timestamp]("start_time")
    def endTime = column[Timestamp]("end_time")
    def description = column[String]("description")
    def image1Url = column[Option[String]]("image_1_url")

    // the ? method lifts the column into an option
    def * = (id.?, title, location, startTime, endTime, description, image1Url) <> ((Event.apply _).tupled, Event.unapply)
    // the default projection is Event
  }
}

class SlickEventDao extends HasDatabaseConfig[JdbcProfile] with EventDao with EventsComponent  {

  private val events = TableQuery[Events]

  override protected val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  override def update(event: Event): Unit = ???

  override def addImage(id: Long, url: String): Future[Boolean] = {
    val q = for { b <- events if b.id === id } yield b.image1Url
    val updateImage = q.update(Some(url))
    db.run(updateImage).map(_ == 1)
  }

  override def delete(id: Int): Unit = ???

  override def findById(eventId: Long): Future[Option[Event]] = db.run(events.filter(_.id === eventId).result.headOption)

  override def create(event: Event) = {

    dbConfig.db.run(events += event)

  }

  override def sortedById : Future[Seq[Event]] = db.run(events.result)
  override def sortedByEndTime : Future[Seq[Event]] = {
    val query  =     events.sortBy(_.endTime.desc);

    dbConfig.db.run(query.result)
  }

}