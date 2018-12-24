package controllers

import model.User
import model.UserJson._
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers._

class DbControllerTest extends PlaySpec with OneAppPerSuite with Results {

  "Db action" must {
    "insert a record" in {
      val newUser = User(None, "John", "Doe")
      val Some(insert) = route(app, FakeRequest(POST, "/set").withBody(Json.toJson(newUser)))

      status(insert) mustEqual CREATED

      val id = contentAsString(insert).toLong

      val Some(select) = route(app, FakeRequest(GET, s"/get/$id"))

      status(select) mustEqual OK

      val dbUser = contentAsJson(select).as[User]

      dbUser.first mustEqual newUser.first
      dbUser.last mustEqual newUser.last

    }

  }
}
