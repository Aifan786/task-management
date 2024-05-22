package controller

import javax.inject.Inject
import database.mysql.{Tasks, Users}
import model.{Task, TaskRequest, User}
import play.api.libs.json.{Format, JsError, JsResult, JsSuccess, JsValue, Json, OFormat, Reads}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import java.sql.Timestamp
import scala.concurrent.{Await, ExecutionContext, Future}

class TaskController @Inject()(users: Users, tasks: Tasks, val controllerComponents: ControllerComponents, implicit val ec: ExecutionContext) extends BaseController{

  def timestampToLong(t: Timestamp): Long = t.getTime

  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(t: Timestamp): JsValue = Json.toJson(timestampToLong(t))

    def reads(json: JsValue): JsResult[Timestamp] = Json.fromJson[Long](json).map(longToTimestamp)
  }

  def longToTimestamp(dt: Long): Timestamp = new Timestamp(dt)

  implicit private val userReads: Reads[User] = Json.reads[User]
  implicit private val taskRequestReads: Reads[TaskRequest] = Json.reads[TaskRequest]
  implicit val taskFormat: OFormat[Task] = Json.format[Task]

  def createUser(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[User] match {
      case JsSuccess(user, _) =>
        users.create(User(user.id, user.name, user.email, user.createdAt)).map{
          count => Ok(Json.obj("message" -> "user created", "id" -> count))
        }.recover {
          case ex: Exception =>
            InternalServerError(Json.obj("error" -> ex.getMessage))
        }
      case JsError(errors) =>
        Future(BadRequest(Json.obj("message" -> errors.toString())))
    }
  }

  def createTaskByUser(userId: Long): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[TaskRequest] match {
      case JsSuccess(task, _) =>
        users.getUserById(userId).flatMap {
          case Some(_) =>
            tasks.create(Task(None, userId, task.title, task.description, task.dueDate, task.status, Some(new Timestamp(System.currentTimeMillis())), Some(new Timestamp(System.currentTimeMillis())))).map { _ =>
              Ok(Json.obj("message" -> s"Task created for userId $userId"))
            }
          case None =>
            Future.successful(BadRequest(Json.obj("message" -> s"User not found with id $userId")))
        }.recover {
          case ex: Exception =>
            InternalServerError(Json.obj("error" -> ex.getMessage))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("message" -> errors.toString())))
    }
  }

  def getAllTask(userId: Long): Action[AnyContent] = Action.async {
    tasks.getAllTaskByUserId(userId).map{ task =>
      Ok(Json.toJson(task))
    }.recover {
      case ex: Exception =>
        InternalServerError(Json.obj("error" -> ex.getMessage))
    }
  }

  def getTaskByUser(userId: Long, taskId: Long): Action[AnyContent] = Action.async {
    users.getUserById(userId).flatMap {
      case Some(user) =>
        tasks.getTaskByUserId(userId, taskId).map {
          case Some(task) => Ok(Json.toJson(task))
          case None => NotFound(Json.obj("message" -> s"Task not found with id $taskId for user $userId"))
        }.recover {
          case ex: Exception =>
            InternalServerError(Json.obj("error" -> ex.getMessage))
        }
      case None => Future.successful(BadRequest(Json.obj("message" -> s"User not found with id $userId")))
    }
  }

  def updateTask(userId: Long, taskId: Long): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[TaskRequest] match {
      case JsSuccess(task, _) =>
        users.getUserById(userId).flatMap {
          case Some(user) =>
            val timestamp = Some(new Timestamp(System.currentTimeMillis()))
            tasks.updateTaskByUserId(userId, taskId, Task(Some(taskId), userId, task.title, task.description, task.dueDate, task.description, None, timestamp)).map(c => Ok(Json.obj("message" -> "task updated!!")))
          case None => Future.successful(BadRequest(Json.obj("message" -> s"User not found with id $userId")))
        }.recover {
          case ex: Exception =>
            InternalServerError(Json.obj("error" -> ex.getMessage))
        }
      case JsError(errors) =>
        Future(BadRequest(Json.obj("message" -> errors.toString())))
    }
  }

  def deleteTask(userId: Long, taskId: Long): Action[AnyContent] = Action.async{
    users.getUserById(userId).flatMap {
      case Some(_) =>
        tasks.deleteTask(userId, taskId).map {
          case 1 => Ok(Json.obj("message" -> "task deleted"))
          case 0 => NotFound(Json.obj("message" -> s"Task not found with id $taskId for user $userId"))
        }
      case None => Future.successful(BadRequest(Json.obj("message" -> s"User not found with id $userId")))
    }.recover {
      case ex: Exception =>
        InternalServerError(Json.obj("error" -> ex.getMessage))
    }
  }

}
