package models

import java.util.Date

import anorm.{Row, SQL, SqlQuery}
import play.api.Play.current
import play.api.db.DB

case class Event (
                   val id : Option[Long],
                   title: String,
                   location: String,
                   description: String,
                   displayFrom: Date,
                   displayUntil: Date)

object Event {

  val GET_ALL_EVENTS_SQL : SqlQuery = SQL("select * from EVENTS order by id desc")
  val DELETE_EVENT_SQL : SqlQuery = SQL("delete from EVENTS where id = {id}")
  val GET_EVENT_BY_ID_SQL : SqlQuery = SQL("select * from EVENTS where id = {id}")

  // TODO incorporate publish date
  val GET_LATEST_EVENT : SqlQuery = SQL("select * from EVENTS order by id desc LIMIT 1")

  val SAVE_EVENT : SqlQuery = SQL("""
    insert into EVENTS(title, location, description, display_from, display_until)
                values
                      ({title}, {location}, {description}, {display_from}, {display_until})
    """)

  def getAll : List[Event] = DB.withConnection {
    implicit connection =>
    GET_ALL_EVENTS_SQL().map(row =>
        createFrom(row)
    ).toList
  }

  def getLatest : Event = DB.withConnection{
        implicit connection =>
          val row : Row = GET_LATEST_EVENT.apply().head;
          Event.createFrom(row);

  }

  def insert(event : Event) : Option[Long] = {

    val id: Option[Long] = DB.withConnection {
      implicit connection =>
        SAVE_EVENT.on(
          "title" -> event.title,
          "location" -> event.location,
          "description" -> event.description,
          "display_from" -> event.displayFrom,
          "display_until" -> event.displayUntil
        ).executeInsert();
    }
    id
  }

  def getById(eventId : Long) : Event = DB.withConnection {
    implicit connection =>
      val row = GET_EVENT_BY_ID_SQL.on("id" -> eventId).apply().head;
      Event.createFrom(row)
  }

  def delete(id : Int) :Unit = DB.withConnection {
    implicit connection =>
      val result : Boolean = DELETE_EVENT_SQL.on("id" -> id).execute()
  }

  def createFrom(row : Row) : Event = {
    Event(
      row[Option[Long]] ("id"),
      row[String] ("title"),
      row[String] ("location"),
      row[String] ("description"),
      row[Date] ("display_from"),
      row[Date] ("display_until")
    )
  }

}