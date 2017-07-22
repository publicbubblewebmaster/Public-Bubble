package models

case class Member(id : Option[Long],
                  description: String,
                  imageUrl : String,
                  position: Long)

case class StaticPage(id : Option[Long],
                     content: String,
                     imageUrl : String)