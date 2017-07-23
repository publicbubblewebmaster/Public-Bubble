package models

import java.sql.Timestamp
import java.util.Date

import dao.SlickEventDao

import scala.concurrent.Future

case class Event (
                   id : Option[Long],
                   title: String,
                   location: String,
                   startTime: Timestamp,
                   endTime: Timestamp,
                   description: String,
                   image1: Option[Array[Byte]] = None)

/* Same as model class but missing image url because this is handled via ajax*/
case class EventFormData (
                   id : Option[Long],
                   title: String,
                   location: String,
                   startTime: Date,
                   endTime: Date,
                   description: String)

object Event {

  def createFrom(eventFormData : EventFormData) : Event =
    Event(
      eventFormData.id,
      eventFormData.title,
      eventFormData.location,
      new Timestamp(eventFormData.startTime.toInstant.toEpochMilli),
      new Timestamp(eventFormData.endTime.toInstant.toEpochMilli),
      eventFormData.description,
      None)

  lazy val dao = new SlickEventDao

  def extract(event: Event) = {
    Option(Tuple6(event.id, event.title, event.location, event.startTime, event.endTime, event.description))
  }

  def update(event: Event): Unit = dao.update(event)

  def addImage(id: Long, url: Array[Byte]): Future[Boolean] = dao.addImage(id, url)

  def delete(id: Int):  Future[Int] = dao.delete(id)

  def getById(eventId: Long): Future[Option[Event]] = dao.findById(eventId)

  def create(event: Event) = dao.create(event)
}
