package util;

import com.cloudinary.utils.ObjectUtils
import com.cloudinary.{Cloudinary, Transformation}
import controllers.BlogsController.cloudinary
import play.api.Play

trait CloudinaryUploader {

  lazy val cloudinaryUrl = Play.current.configuration.getString("cloudinary.url").get

  lazy val cloudinary = new Cloudinary(cloudinaryUrl)

  def upload(file : java.io.File, width : Int, height : Int): String = {
    val transformation = ObjectUtils.asMap("transformation", new Transformation().width(width), "transformation", new Transformation().height(height))
    val uploadResult = cloudinary.uploader().upload(file, transformation);
    uploadResult.get("url").asInstanceOf[String]
  }

}