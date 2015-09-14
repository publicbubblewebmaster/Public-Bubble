package util;

import com.cloudinary.Cloudinary
import play.api.Play

trait CloudinaryUploader {

  lazy val cloudinaryUrl = Play.current.configuration.getString("cloudinary.url").get

  lazy val cloudinary = new Cloudinary(cloudinaryUrl)

}