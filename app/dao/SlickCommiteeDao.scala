package dao

import play.api.{Logger}
import java.sql.Date
import models.Member
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future

trait CommiteeComponent {
  self: HasDatabaseConfig[JdbcProfile] =>

  class Commitee(tag: Tag) extends Table[Member](tag, Some("public_bubble"), "commitee") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def description = column[String]("description")

    def imageUrl = column[String]("image_url")

    // the ? method lifts the column into an option
    def * = (id.?, description, imageUrl) <> ((Member.apply _).tupled, Member.unapply)

    // the default projection is Member

  }

}

class SlickCommiteeDao extends HasDatabaseConfig[JdbcProfile] with CommiteeComponent {

  private val commitee = TableQuery[Commitee]

  override protected val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)

//  override def update(Member: Member): Unit = {
//    val query = for {b <- commitee if b.id === Member.id} yield
//      (b.title, b.intro, b.author, b.content, b.publishDate)
//
//    db.run(query.update(Member.title, Member.intro, Member.author, Member.content, Member.publishDate))
//  }

//  override def addImage(id: Long, url: String): Future[Boolean] = {
//    val q = for {b <- commitee if b.id === id} yield b.image1Url
//    val updateImage = q.update(Some(url))
//    db.run(updateImage).map(_ == 1)
//  }

  def delete(id: Long): Future[Int] = {
    val findById = commitee.filter(_.id === id)
    dbConfig.db.run(findById.delete)
  }

  def create(Member: Member) = {
    dbConfig.db.run(commitee += Member)
  }

  def listMembers: Future[Seq[Member]] = db.run(commitee.result)

  def update(member : Member): Future[Int] = {
    val q = for { m <- commitee if m.id === member.id.get } yield (m.description, m.imageUrl)

    println("Q is " + q)

//    val image = if (member.imageUrl == null) {
//      commitee.filter(_.id === id)    }

    return dbConfig.db.run(q.update(member.title, member.imageUrl))
  }

}