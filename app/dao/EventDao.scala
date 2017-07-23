package dao

import models.Event
import scala.concurrent.Future

trait EventDao {

  def update(event : Event)

  def addImage(id : Long, url : Array[Byte]) : Future[Boolean]

  def findById(eventId : Long) : Future[Option[Event]]

  def delete(id : Long) : Future[Int]

  def allEvents : Future[Seq[Event]]

  def futureEvents : Future[Seq[Event]]

  def sortedById : Future[Seq[Event]]

  def create(event : Event)

}