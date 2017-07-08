package models

import java.sql.Date
import scala.concurrent.Future

import dao.SlickBlogDao

case class Member (name: String,
                   position: String)

object {

  def loadMembers() {
    java.nio.file.Files.readAllBytes()
  }

}
