package dao

import java.sql.{Timestamp, Date}
import java.util.Calendar

import models.Event
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future
import java.sql.Timestamp

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

  val currentTimestamp = new Timestamp(Calendar.getInstance.getTime.getTime)

  private val events = TableQuery[Events]

  override protected val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  override def update(event: Event): Unit = {
    val query = for {e <- events if e.id === event.id} yield
    (e.title, e.location, e.description, e.startTime, e.endTime)

    db.run(query.update(event.title, event.location, event.description, event.startTime, event.endTime))
  }

  override def addImage(id: Long, url: String): Future[Boolean] = {
    val q = for { b <- events if b.id === id } yield b.image1Url
    val updateImage = q.update(Some(url))
    db.run(updateImage).map(_ == 1)
  }

  override def delete(id: Long):  Future[Int] = {
    val findById = events.filter(_.id === id)
    dbConfig.db.run(findById.delete)
  }

  override def findById(eventId: Long): Future[Option[Event]] = db.run(events.filter(_.id === eventId).result.headOption)

  override def create(event: Event) = {
    dbConfig.db.run(events += event)
  }

  override def sortedById : Future[Seq[Event]] = db.run(events.result)

  override def futureEvents : Future[Seq[Event]] = {
    val query  =     events.filter(_.endTime >= currentTimestamp)sortBy(_.endTime.desc);
    dbConfig.db.run(query.result)
  }

  override def allEvents: Future[Seq[Event]] = {
    val eventSortedByEndTime = events.sortBy(_.endTime.desc)
    dbConfig.db.run(eventSortedByEndTime.result)
  }
}