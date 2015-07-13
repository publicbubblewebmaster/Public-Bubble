package dao

import java.sql.Date

import models.Event

trait EventDao {

  def getAll : List[Event]

  def getLatest : Option[Event]

  def create(event : Event) : Option[Long]

  def update(event : Event)

  def addImage(id : Long, url : String) : Event

  def getById(eventId : Long) : Event

  def delete(id : Int) :Unit

  def apply(id : Option[Long], title: String, location: String, description: String, displayFrom: Date, displayUntil: Date) = {
    new Event(id, title, location, description, displayFrom, displayUntil)
  }

  def extract(event: Event)

}
