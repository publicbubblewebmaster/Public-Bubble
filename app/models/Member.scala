package models

case class Member(id : Option[Long],
                  title: String,
                  imageUrl : String)

case class StaticPage(id : Option[Long],
                     content: String,
                     imageUrl : String)