package controllers

import play.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import tables.BlogTable

class BlgController extends Controller with BlogTable {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  //create an instance of the table
  val Blogs = TableQuery[Blog] //see a way to architect your app in the computers-database sample

  def index = Action.async {
    dbConfig.db.run(Blogs.result).map(res => Ok(views.html.index(res.toList)))
  }

  val catForm = Form(
    mapping(
      "name" -> text(),
      "color" -> text()
    )(Cat.apply)(Cat.unapply)
  )

  def insert = Action.async { implicit rs =>
    val cat = catForm.bindFromRequest.get
    db.run(Cats += cat).map(_ => Redirect(routes.Application.index))
  }
}
