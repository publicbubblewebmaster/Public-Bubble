package models

import java.sql.{Timestamp, Date}

import dao.SlickEventDao

import scala.concurrent.Future

case class Event (
                   id : Option[Long],
                   title: String,
                   location: String,
                   startTime: Timestamp,
                   endTime: Timestamp,
                   description: String,
                   image1Url: Option[String] = None)

/* Same as model class but missing image url because this is handled via ajax*/
case class EventFormData (
                   id : Option[Long],
                   title: String,
                   location: String,
                   startTime: Timestamp,
                   endTime: Timestamp,
                   description: String)

object Event {

  def createFrom(eventFormData : EventFormData) : Event =
    Event(
      eventFormData.id,
      eventFormData.title,
      eventFormData.location,
      eventFormData.startTime,
      eventFormData.endTime,
      eventFormData.description,
      None)

  lazy val dao = new SlickEventDao

  def extract(event: Event) = {
    Option(Tuple6(event.id, event.title, event.location, event.startTime, event.endTime, event.description))
  }

  def update(event: Event): Unit = dao.update(event)

  def addImage(id: Long, url: String): Future[Boolean] = dao.addImage(id, url)

  def delete(id: Int): Unit = dao.delete(id)

  def getById(eventId: Long): Future[Option[Event]] = dao.findById(eventId)

  def create(event: Event) = dao.create(event)
}
