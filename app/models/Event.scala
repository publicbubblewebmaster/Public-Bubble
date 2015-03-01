package models

import anorm.SQL
import anorm.SqlQuery
import play.api.db.DB
import play.api.Play.current

/**
 * Created by ian on 24/02/15.
 */
case class Event (id : Long, title: String, location: String, description: String)

object Event {

  val GET_ALL_EVENTS_SQL : SqlQuery = SQL("select * from EVENTS order by id desc")
  val GET_EVENT_BY_ID_SQL : SqlQuery = SQL("select * from EVENTS where id = {id}")

  def getAll : List[Event] = DB.withConnection {
    implicit connection =>
      GET_ALL_EVENTS_SQL().map (row =>
            Event(
              row[Long] ("id"),
              row[String] ("title"),
              row[String] ("location"),
              row[String] ("description")
            )).toList
  }

  def getById(eventId : Long) : Event = DB.withConnection {
    implicit connection =>
      val row = GET_EVENT_BY_ID_SQL.on("id" -> eventId).apply().head;
      Event(
          row[Long] ("id"),
          row[String] ("title"),
          row[String] ("location"),
          row[String] ("description")
        )
  }

}





