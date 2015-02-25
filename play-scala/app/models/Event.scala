package models

import anorm._
import play.api.db.DB

/**
 * Created by ian on 24/02/15.
 */
case class Event (title: String, location: String, description: String) {

  def getEvent(id : Long) = {
    DB.withConnection { implicit c =>
      val result: Boolean = SQL("select * from EVENT where id = {id}").
        on("id" -> id).execute()

    }

  }

}





