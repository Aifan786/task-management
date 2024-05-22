import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.test.Helpers.{DELETE, GET, POST, PUT, contentAsJson, contentType, defaultAwaitTimeout, route, status, writeableOf_AnyContentAsEmpty, writeableOf_AnyContentAsJson}
import play.api.test.{FakeRequest, Injecting}

class TaskTestController extends PlaySpec with GuiceOneAppPerTest with Injecting with ScalaFutures{

  "TaskManagement" should {
    "create a new user" in {
      val requestJson = Json.obj("name" -> "John Doe", "email" -> "john230@gmail.com")
      val request = FakeRequest(POST, "/users").withJsonBody(requestJson)

      val response = route(app, request).get

      status(response) mustBe OK
      (contentAsJson(response) \ "message").as[String] must include("user created")
    }

    "create a new task for the specified user" in {
      val userId = 1L
      val requestJson = Json.obj(
        "title" -> "Task1",
        "description" -> "Task description1",
        "dueDate" -> "2024-12-31",
        "status" -> "To Do"
      )
      val request = FakeRequest(POST, s"/users/$userId/tasks").withJsonBody(requestJson)

      val response = route(app, request).get

      status(response) mustBe OK
      (contentAsJson(response) \ "message").as[String] must include("task created by userId")
    }

    "retrieve all tasks for the specified user" in {
      val userId = 1L
      val request = FakeRequest(GET, s"/users/$userId/tasks")

      val response = route(app, request).get

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
    }

    "retrieve a specific task for the specified user" in {
      val userId = 1
      val taskId = 1L
      val request = FakeRequest(GET, s"/users/$userId/tasks/$taskId")

      val response = route(app, request).get

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
    }

    "update a specific task for the specified user" in {
      val userId = 1
      val taskId = 1L
      val requestJson = Json.obj(
        "title" -> "Updated Task1",
        "description" -> "Updated description1",
        "dueDate" -> "2024-12-31",
        "status" -> "In Progress"
      )
      val request = FakeRequest(PUT, s"/users/$userId/tasks/$taskId").withJsonBody(requestJson)

      val response = route(app, request).get

      status(response) mustBe OK
      (contentAsJson(response) \ "message").as[String] must include("task updated")
    }

    "delete a specific task for the specified user" in {
      val userId = 1L
      val taskId = 1L
      val request = FakeRequest(DELETE, s"/users/$userId/tasks/$taskId")

      val response = route(app, request).get

      status(response) mustBe OK
      (contentAsJson(response) \ "message").as[String] must include("task deleted")
    }
  }

}
