package dao

import play.api.Logger
import java.sql.Date

import models.Member
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.lifted.QueryBase

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait CommitteeComponent {
  self: HasDatabaseConfig[JdbcProfile] =>

  class Committee(tag: Tag) extends Table[Member](tag, Some("public_bubble"), "committee") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def description = column[String]("description")
    def image = column[Option[Array[Byte]]]("image")
    def position = column[Long]("position")

    // the ? method lifts the column into an option
    def * = (id.?, description, image, position) <> ((Member.apply _).tupled, Member.unapply)

    // the default projection is Member

  }

}

class SlickCommitteeDao extends HasDatabaseConfig[JdbcProfile] with CommitteeComponent {

  private val committee = TableQuery[Committee]

  override protected val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def delete(id: Long): Future[Int] = {
    val findById = committee.filter(_.id === id)
    dbConfig.db.run(findById.delete)
  }

  def create(member: Member) = {
    println(s"Creating $member")
    dbConfig.db.run(committee += member)
  }

  def imageById(id : Long): Future[Option[Array[Byte]]] = {
    val findById = committee.filter(_.id === id)
    return dbConfig.db.run(findById.map(_.image).result.head)
  }

  def listMembers: Future[Seq[Member]] = db.run(committee.result)

  def update(member : Member): Future[Int] = {
    println(s"Updating $member")


    val q = for { m <- committee if m.id === member.id.get } yield (m.description, m.image, m.position)

    println("Q is " + q)


    val findUrl: QueryBase[Seq[Option[Array[Byte]]]] = committee.filter(_.id === member.id).map(_.image)

    // messy code
    val image = if (member.image == null) {Await.result(dbConfig.db.run(findUrl.result), Duration(10, "seconds")).head} else {member.image}

    return dbConfig.db.run(q.update(member.description, image, member.position))
  }



}