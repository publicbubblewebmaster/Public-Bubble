package controllers

import com.cloudinary.{Transformation, Cloudinary}
import com.cloudinary.utils.ObjectUtils
import controllers.Events._
import models.Blog
import play.api.Play.current
import play.api.data.{Form, Forms}
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.mvc._

object Blogs extends Controller {

  def blogs = Action {
    val blog = Blog.getLatest

    Ok(views.html.blogs(blog))

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
        "cloud_name", "hhih43y5p",
        "api_key", "135878543169511",
        "api_secret", "aLT-f0E8uZ4WdPT20gY9eKoGeYc"));

      val uploadResult = cloudinary.uploader().upload(file.ref.file,
        ObjectUtils.asMap("transformation", new Transformation().width(400))
      );

      val imageUrl = uploadResult.get("url").asInstanceOf[String]

      val eventWithImage = models.Event.addImage(id.toLong, imageUrl);

      Ok("Retrieved file %s" format eventWithImage.image1Url)
    }.getOrElse(BadRequest("File missing!"))
  }


}