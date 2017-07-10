package dao

import java.sql.Date

import models.Member
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future

class CommitteeDao {

  protected val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def listMembers(): Seq[Member] = {
    val db = dbConfig.db
    try {

      val rslt = sql"""select title, author
      from blog""".as[(String, String)]
      println(rslt)
    } finally db.close
    return Seq(Member(Option(1), "title", "imageUrl"))
  }
}