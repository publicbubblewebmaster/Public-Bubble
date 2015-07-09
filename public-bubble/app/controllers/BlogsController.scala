package controllers

import com.cloudinary.{Transformation, Cloudinary}
import com.cloudinary.utils.ObjectUtils
import models.Blog
import play.api.Play
import play.api.data.{Form, Forms}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.mvc._


object BlogsController extends Controller {

  lazy val CLOUD_NAME : String = Play.current.configuration.getString("cloudinary.name").get
  lazy val CLOUD_KEY : String = Play.current.configuration.getString("cloudinary.key").get
  lazy val CLOUD_SECRET : String = Play.current.configuration.getString("cloudinary.secret").get

  def blogs = Action {
    val blogOption = Blog.getLatest

    blogOption match {
      case _ : Some[Blog] => Ok(views.html.blogs(blogOption.get))
      case _ => Ok(views.html.noContent("blogs"))
    }
  }

  def createBlog = Authenticated {
    Ok(views.html.createBlog(blogForm))
  }

  def updateBlog(id : Int) = Action {
    val blog = Blog.getById(id);
    print("blog.id: " + blog.id);
    Ok(views.html.createBlog(blogForm.fill(blog)))
  }

  def save = Action { implicit request =>
    blogForm.bindFromRequest.fold(
      formWithErrors =>     {
        print(formWithErrors.errors)
        Ok(views.html.createBlog(formWithErrors))},

      createdBlog => {
        if (createdBlog.id.isEmpty) {
          Blog.create(createdBlog)
        }
        else {
          Blog.update(createdBlog)
        }
        Ok(views.html.createBlog(blogForm.fill(createdBlog)))
      }
    )
  }

  def deleteBlog(id : Int)= Action { implicit request =>
    Blog.delete(id)
    Ok(views.html.createBlog(blogForm))
  }

  def blogsJson = Action {
    val jsonBlogs : List[JsValue] =
      Blog.getAll.map(
        blog =>
          Json.obj(
            "id" -> blog.id,
            "title" -> blog.title
          )
      )
    Ok(JsArray(jsonBlogs))
  }

  val blogForm = Form(
    Forms.mapping(
      "id" -> Forms.optional(Forms.longNumber()),
      "title" -> Forms.text,
      "author" -> Forms.text,
      "intro" -> Forms.text,
      "content" -> Forms.text,
      "displayFrom" -> Forms.date("yyyy-MM-dd"),
      "displayUntil" -> Forms.date("yyyy-MM-dd")
    )(Blog.apply)(Blog.extract)
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

      val blogWithImage = models.Blog.addImage(id.toLong, imageUrl);

      Ok("Retrieved file %s" format blogWithImage.image1Url)
    }.getOrElse(BadRequest("File missing!"))
  }
}