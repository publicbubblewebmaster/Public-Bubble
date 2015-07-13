package models

import java.sql.Date

case class Event (
                   id : Option[Long],
                   title: String,
                   location: String,
                   description: String,
                   displayFrom: Date,
                   displayUntil: Date,
                   image1Url: Option[String] = null)

/* Same as model class but missing image url because this is handled via ajax*/
case class EventFormData (
                   id : Option[Long],
                   title: String,
                   location: String,
                   description: String,
                   displayFrom: Date,
                   displayUntil: Date)

object Event {

  def createFrom(eventFormData : EventFormData) : Event =
    Event(eventFormData.id, eventFormData.title, eventFormData.location, eventFormData.description, eventFormData.displayFrom, eventFormData.displayUntil, None)

  lazy val dao = new SlickEventDao

   def getAll : List[Event] = dao.getAll

  def getLatest : Option[Event] = dao.getLatest

  def create(event : Event) : Option[Long] = dao.create(event)

  def update(event : Event) = dao.update(event)

  def addImage(id : Long, url : String) : Event = dao.addImage(id, url)

  def getById(eventId : Long) : Event = dao.getById(eventId)

  def delete(id : Int) :Unit = dao.delete(id)

  def extract(event: Event) = {
     Option(Tuple6(event.id, event.title, event.location, event.description, event.displayFrom, event.displayUntil))
  }

}
