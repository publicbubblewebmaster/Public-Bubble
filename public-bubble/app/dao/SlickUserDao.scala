package dao

import play.api.Play
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.{JdbcProfile, PostgresDriver}
import slick.driver.PostgresDriver.api._
import scala.concurrent.Future

class SlickUserDao {

  import scala.concurrent.ExecutionContext.Implicits.global

  val db = DatabaseConfigProvider.get[JdbcProfile](Play.current).db

  def hasRole(email: String, role : String) : Future[Boolean] = {
    val query = sql"SELECT 1 FROM PUBLIC_BUBBLE.USER WHERE email = $email AND lower(role) = ${role.toLowerCase}"
    Logger.debug(s"event : 'Running query: $query with paremeters: $email and $role")
    db.run(query.as[Int]).map(
      !_.isEmpty
    )
  }
}