package models

import java.util.Date

import anorm.{Row, SQL, SqlQuery}
import play.api.Play.current
import play.api.db.DB

case class Blog (
                   id : Option[Long],
                   title: String,
                   author: String,
                   intro: String,
                   content: String,
                   displayFrom: Date,
                   displayUntil: Date,
                   image1Url: Option[String] = null)

object Blog {

  val GET_ALL_BLOGS_SQL : SqlQuery = SQL("select * from PUBLIC_BUBBLE.BLOG order by id desc")
  val DELETE_BLOG_SQL : SqlQuery = SQL("delete from PUBLIC_BUBBLE.BLOG where id = {id}")
  val GET_BLOG_BY_ID_SQL : SqlQuery = SQL("select * from PUBLIC_BUBBLE.BLOG where id = {id}")

  // TODO incorporate publish date
  val GET_LATEST_BLOG : SqlQuery = SQL("select * from PUBLIC_BUBBLE.BLOG " +
    "where display_from <= current_date and display_until > current_date order by display_until asc LIMIT 1")

  val CREATE_BLOG : SqlQuery = SQL("""
    insert into PUBLIC_BUBBLE.BLOG(title, author, content, display_from, display_until)
                values
                      ({title}, {author}, {intro}, {content}, {display_from}, {display_until})
    """)

  val ADD_IMAGE : SqlQuery = SQL("""UPDATE public_bubble.blog SET image_1_url = {image1Url} where ID = {id}""")

  val UPDATE_BLOG : SqlQuery = SQL("""
    UPDATE public_bubble.blog SET title = {title},
                   author = {author},
                   intro = {intro},
                   content = {content},
                   display_from = {display_from},
                   display_until = {display_until}
                   where id = {id}
                                    """)

  def getAll : List[Blog] = DB.withConnection {
    implicit connection =>
    GET_ALL_BLOGS_SQL().map(row =>
        createFrom(row)
    ).toList
  }

  def getLatest : Blog = DB.withConnection{
        implicit connection =>
          val row : Row = GET_LATEST_BLOG.apply().head;
          Blog.createFrom(row);
  }

  def create(blog : Blog) : Option[Long] = {

    val id: Option[Long] = DB.withConnection {
      implicit connection =>
        CREATE_BLOG.on(
          "title" -> blog.title,
          "author" -> blog.author,
          "intro" -> blog.intro,
          "content" -> blog.content,
          "display_from" -> blog.displayFrom,
          "display_until" -> blog.displayUntil
        ).executeInsert();
    }
    id
  }
  
  def update(blog : Blog) = {
    DB.withConnection {
      implicit connection =>
      UPDATE_BLOG.on(
        "title" -> blog.title,
        "author" -> blog.author,
        "intro" -> blog.intro,
        "content" -> blog.content,
        "display_from" -> blog.displayFrom,
        "display_until" -> blog.displayUntil,
        "id" -> blog.id
      ).executeUpdate()
    }
  }

  def addImage(id : Long, url : String) : Blog = {
    DB.withConnection {
      implicit connection =>
        ADD_IMAGE.on(
          "id" -> id,
          "image1Url" -> url
        ).executeUpdate()
        Blog.getById(id)
    }
  }

  def getById(blogId : Long) : Blog = DB.withConnection {
    implicit connection =>
      val row = GET_BLOG_BY_ID_SQL.on("id" -> blogId).apply().head;
      Blog.createFrom(row)
  }

  def delete(id : Int) :Unit = DB.withConnection {
    implicit connection =>
      val result : Boolean = DELETE_BLOG_SQL.on("id" -> id).execute()
  }

  def createFrom(row : Row) : Blog = {
    Blog(
      row[Option[Long]] ("id"),
      row[String] ("title"),
      row[String] ("author"),
      row[String] ("intro"),
      row[String] ("content"),
      row[Date] ("display_from"),
      row[Date] ("display_until"),
      row[Option[String]] ("IMAGE_1_URL")
    )
  }

  def apply(id : Option[Long], title: String, author: String, intro: String, content: String, displayFrom: Date, displayUntil: Date) = {
    new Blog(id, title, author, intro, content, displayFrom, displayUntil)
  }

  def extract(blog: Blog) = {
     Option(Tuple7(blog.id, blog.title, blog.author, blog.intro, blog.content, blog.displayFrom, blog.displayUntil))
  }
}