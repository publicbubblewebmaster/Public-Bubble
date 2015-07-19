package dao

import models.Event
import scala.concurrent.Future

trait EventDao {

  def update(event : Event)

  def addImage(id : Long, url : String) : Future[Boolean]

  def findById(eventId : Long) : Future[Option[Event]]

  def delete(id : Int)

  def sortedByEndTime : Future[Seq[Event]]

  def sortedById : Future[Seq[Event]]

  def create(event : Event)

}