package router

import javax.inject.Inject
import controller.TaskController
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class TaskRouter @Inject()(controller: TaskController) extends SimpleRouter {
  override def routes: Routes = {
    case POST(p"") => controller.createUser()

    case POST(p"/$userId/tasks") => controller.createTaskByUser(userId.toLong)

    case GET(p"/$userId/tasks") => controller.getAllTask(userId.toLong)

    case GET(p"/$userId/tasks/$taskId") => controller.getTaskByUser(userId.toLong, taskId.toLong)

    case PUT(p"/$userId/tasks/$taskId") => controller.updateTask(userId.toLong, taskId.toLong)

    case DELETE(p"/$userId/tasks/$taskId") => controller.deleteTask(userId.toLong, taskId.toLong)
  }
}
