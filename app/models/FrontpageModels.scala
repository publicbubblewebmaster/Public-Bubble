package models

import java.sql.Blob

case class Member(id : Option[Long],
                  description: String,
                  image : Option[Array[Byte]],
                  position: Long)

case class StaticPage(id : Option[Long],
                     content: String,
                     image : Option[Array[Byte]])