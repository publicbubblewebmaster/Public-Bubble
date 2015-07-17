/*
package dao

class AnormEventDao extends EventDao {

  def getAll: List[Event] = DB.withConnection {
    implicit connection =>
      GET_ALL_EVENTS_SQL().map(row =>
        createFrom(row)
      ).toList
  }

  def getLatest: Option[Event] = DB.withConnection {
    implicit connection =>
      val optionRow = GET_LATEST_EVENT.apply().headOption;

      optionRow match {
        case _: Row => Some(Event.createFrom(optionRow.get))
        case _ => None
      }
  }

  def create(event: Event): Option[Long] = {

    val id: Option[Long] = DB.withConnection {
      implicit connection =>
        CREATE_EVENT.on(
          "title" -> event.title,
          "location" -> event.location,
          "description" -> event.description,
          "display_from" -> event.displayFrom,
          "display_until" -> event.displayUntil
        ).executeInsert();
    }
    id
  }

  def update(event: Event) = {
    DB.withConnection {
      implicit connection =>
        UPDATE_EVENT.on(
          "title" -> event.title,
          "location" -> event.location,
          "description" -> event.description,
          "display_from" -> event.displayFrom,
          "display_until" -> event.displayUntil,
          "id" -> event.id
        ).executeUpdate()
    }
  }

  def addImage(id: Long, url: String): Event = {
    DB.withConnection {
      implicit connection =>
        ADD_IMAGE.on(
          "id" -> id,
          "image1Url" -> url
        ).executeUpdate()
        Event.getById(id)
    }
  }

  def getById(eventId: Long): Event = DB.withConnection {
    implicit connection =>
      val row = GET_EVENT_BY_ID_SQL.on("id" -> eventId).apply().head;
      Event.createFrom(row)
  }

  def delete(id: Int): Unit = DB.withConnection {
    implicit connection =>
      val result: Boolean = DELETE_EVENT_SQL.on("id" -> id).execute()
  }

  def createFrom(row: Row): Event = {
    Event(
      row[Option[Long]]("id"),
      row[String]("title"),
      row[String]("location"),
      row[String]("description"),
      row[Date]("display_from"),
      row[Date]("display_until"),
      row[Option[String]]("IMAGE_1_URL")
    )
  }

  def apply(id: Option[Long], title: String, location: String, description: String, displayFrom: Date, displayUntil: Date) = {
    new Event(id, title, location, description, displayFrom, displayUntil)
  }

  def extract(event: Event) = {
    Option(Tuple6(event.id, event.title, event.location, event.description, event.displayFrom, event.displayUntil))
  }

}*/
