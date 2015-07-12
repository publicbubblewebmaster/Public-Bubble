package controllers

import java.sql.Date

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

object BlogsController extends Controller {

  lazy val blogDao = new SlickBlogDao

  val BLOGS_CACHE = "blogs"
  val BLOGS_JSON_CACHE = "blogsJson"
  lazy val CLOUD_NAME : String = Play.current.configuration.getString("cloudinary.name").get
  lazy val CLOUD_KEY : String = Play.current.configuration.getString("cloudinary.key").get
  lazy val CLOUD_SECRET : String = Play.current.configuration.getString("cloudinary.secret").get

  def blogs = Action {
    val blogOption = blogDao.getLatest

    blogOption match {
      case _ : Some[Blog] => Ok(views.html.blogs(blogOption.get))
      case _ => Ok(views.html.noContent("blogs"))
    }
  }


  def createBlog = Authenticated {
//    clearCache
    Ok(views.html.createBlog(blogForm))
  }

  def updateBlog(id : Long) = Action {
//    clearCache
    val blog = blogDao.getById(id);

//    val (id, title, author, intro, content, publishDate, image1Url) = Blog.unapply(blog).get
    val blogFormData = BlogFormData(blog.id, blog.title, blog.author, blog.intro, blog.content, blog.publishDate)

    Ok(views.html.createBlog(blogForm.fill(blogFormData)))
  }

  def save = Action { implicit request =>
    blogForm.bindFromRequest.fold(
      formWithErrors =>     {

        Logger.warn("formErrors=" + formWithErrors.errorsAsJson);

        Ok(views.html.createBlog(formWithErrors))},

        createdBlog => {
        if (createdBlog.id.isEmpty) {
          blogDao create(Blog.createFrom(createdBlog))
        }
        else {
          blogDao update(Blog.createFrom(createdBlog))
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

  def blogsJson = Cached(BLOGS_JSON_CACHE) {
    Action {
      val jsonBlogs: List[JsValue] =
        blogDao.getAll.map(
          blog =>
            Json.obj(
              "id" -> blog.id,
              "title" -> blog.title
            )
        )
      Ok(JsArray(jsonBlogs))
    }
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

