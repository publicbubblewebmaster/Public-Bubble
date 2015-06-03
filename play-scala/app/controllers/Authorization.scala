package controllers

import play.api.mvc.ActionBuilder
import play.api.mvc.Request
import play.api.mvc.Results.Ok
import play.api.mvc.Result
import play.api.mvc.WrappedRequest

import scala.concurrent.Future

class AuthenticatedRequest[A](val username: String, request: Request[A]) extends WrappedRequest[A](request)

object Authenticated extends ActionBuilder[AuthenticatedRequest] {
  def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]) = {
    request.session.get("email").map { email =>
      block(new AuthenticatedRequest(email, request))
    } getOrElse {
      Future.successful(Ok(views.html.loginForm()))
    }
  }
}
