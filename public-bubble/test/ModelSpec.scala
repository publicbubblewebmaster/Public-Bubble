import dao.BlogDao
import models.Blog
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

class BlogSpec extends PlaySpec with MockitoSugar{

  "Blog#getById" should {
    "be true when the role is admin" in {
      val blogDaoMock = mock[BlogDao]
      when(blogDaoMock.findById(any[Long])) thenReturn Future(Blog())

      val userService = new Blog(userRepository)

      val actual = userService.isAdmin(User("11", "Steve", "user@example.org"))
      actual mustBe true
    }
  }

}
