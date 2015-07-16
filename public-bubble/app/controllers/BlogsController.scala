package controllers

import com.cloudinary.{Transformation, Cloudinary}
import com.cloudinary.utils.ObjectUtils
import dao.{SlickBlogDao}
import models.{BlogFormData, Blog}
import play.api.{Play, Logger}
import play.api.data.{Form, Forms}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.mvc._
import play.api.cache.{Cached, Cache}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Promise, Future}

object BlogsController extends Controller {

  lazy val blogDao = new SlickBlogDao

  val BLOGS_CACHE = "blogs"
  val BLOGS_JSON_CACHE = "blogsJson"
  lazy val CLOUD_NAME : String = Play.current.configuration.getString("cloudinary.name").get
  lazy val CLOUD_KEY : String = Play.current.configuration.getString("cloudinary.key").get
  lazy val CLOUD_SECRET : String = Play.current.configuration.getString("cloudinary.secret").get

  def blogs = Action.async { implicit request =>
    val futureBlogs : Future[Seq[Blog]] = blogDao.sortedByDate

    Logger.info(
      s"""
        |{"message" : "blogs retrieved"}
      """.stripMargin)

    futureBlogs.map {case (blogList) =>
        blogList match {
          case Nil => NotFound
          case _ => Ok(views.html.blogs(blogList))
        }
    }
  }

  def createBlog = Authenticated {
//    clearCache
    Ok(views.html.createBlog(blogForm))
  }

  def updateBlog(id : Long) = Action.async {
//    clearCache
    val futureBlogOption = Blog.getById(id)

    val result : Future[Result] = futureBlogOption.map(
      _ match {
          case blogOption : Some[Blog] => {val blogFormData = BlogFormData(blogOption.get.id, blogOption.get.title, blogOption.get.author, blogOption.get.intro, blogOption.get.content, blogOption.get.publishDate); Ok(views.html.createBlog(blogForm.fill(blogFormData)))}
          case _ => NotFound
        })

    result
  }

  def save = Action { implicit request =>
    blogForm.bindFromRequest.fold(
      formWithErrors =>     {

        Logger.warn("formErrors=" + formWithErrors.errorsAsJson);

        Ok(views.html.createBlog(formWithErrors))},

        createdBlog => {
        if (createdBlog.id.isEmpty) {
          Blog.create(Blog.createFrom(createdBlog))
        }
        else {
          Blog.update(Blog.createFrom(createdBlog))
        }
        Ok(views.html.createBlog(blogForm.fill(createdBlog)))
      }
    )
  }

  def deleteBlog(id : Int)= Action { implicit request =>
//    clearCache
    blogDao delete(id)
    Ok(views.html.createBlog(blogForm))
  }

  def blogsJson = Action.async { implicit request =>

    val futureBlogs : Future[Seq[Blog]] = blogDao.sortedById
    val futureJson : Future[Seq[JsValue]] = futureBlogs.map(_.map(blog => Json.obj("id" -> blog.id, "title" -> blog.title)))

    futureJson.map(jsList => Ok(JsArray(jsList)))
  }

  val blogForm = Form(
    Forms.mapping(
      "id" -> Forms.optional(Forms.longNumber()),
      "title" -> Forms.text,
      "author" -> Forms.text,
      "intro" -> Forms.text,
      "content" -> Forms.text,
      "publishDate" -> Forms.sqlDate("yyyy-MM-dd")
    )(BlogFormData.apply)(BlogFormData.unapply)
  )

  // here we are calling the ActionBuilder apply method
  // the apply method can accept a function
  def authenticatedBlogForm = Authenticated { request  =>
    Ok(views.html.createBlog(blogForm))
  }

  import java.nio.file.{Path, Paths, Files}
  def uploadImage = Action(parse.multipartFormData) { request =>

    val id : String = request.body.dataParts.get("id").get.head
    val domainObject : String = request.body.dataParts.get("domainObject").get.head

    request.body.file("image1").map { file =>

      val cloudinary : Cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", CLOUD_NAME,
        "api_key", CLOUD_KEY,
        "api_secret", CLOUD_SECRET));

      val uploadResult = cloudinary.uploader().upload(file.ref.file,
        ObjectUtils.asMap("transformation", new Transformation().width(800), "transformation", new Transformation().height(370))
      );

      val imageUrl = uploadResult.get("url").asInstanceOf[String]

      val blogWithImage = blogDao.addImage(id.toLong, imageUrl);

      Ok("Retrieved file %s" format blogWithImage.image1Url)
    }.getOrElse(BadRequest("File missing!"))
  }

  private def clearCache = {
    Cache.remove(BLOGS_CACHE)
    Cache.remove(BLOGS_JSON_CACHE)
  }
}

