package database.mysql

import com.google.inject.Inject
import model.Task
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.{JdbcBackend, JdbcProfile}
import slick.lifted.{ProvenShape, Rep}

import java.sql.Timestamp
import scala.concurrent.{ExecutionContext, Future}

class TaskTable(tag: Tag) extends Table[Task](tag,"tasks") {

  def id:Rep[Option[Long]] = column[Option[Long]]("id",O.PrimaryKey,O.AutoInc,O.Unique)
  def userId:Rep[Long] = column[Long]("user_id")
  def title:Rep[String] = column[String]("title")
  def description:Rep[String] = column[String]("description")
  def dueDate:Rep[Timestamp] = column[Timestamp]("due_date")
  def status:Rep[String] = column[String]("status")
  def createdAt:Rep[Option[Timestamp]] = column[Option[Timestamp]]("created_at")
  def updatedAt:Rep[Option[Timestamp]] = column[Option[Timestamp]]("updated_at")

  type Data = (Option[Long], Long, String, String, Timestamp, String, Option[Timestamp], Option[Timestamp])

  def constructRole : Data=>Task= {
    case (id:Option[Long], userId:Long, title: String, description: String, dueDate: Timestamp, status: String, createdAt: Option[Timestamp], updatedAt: Option[Timestamp]) => Task(id, userId, title, description, dueDate, status, createdAt, updatedAt)
  }

  def extractRole:PartialFunction[Task,Data] = {
    case Task(id, userId, title, description, dueDate, status, createdAt, updatedAt) => (id, userId, title, description, dueDate, status, createdAt, updatedAt)
  }

  override def * : ProvenShape[Task] = (id, userId, title, description, dueDate, status, createdAt, updatedAt) <> (constructRole,extractRole.lift)
}

class Tasks @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  val tasks :TableQuery[TaskTable] = TableQuery[TaskTable]
  val database: JdbcBackend#DatabaseDef = dbConfig.db

  def create(task: Task): Future[Option[Long]] = database.run(tasks returning tasks.map(_.id) += task)

  def getAllTaskByUserId(userId: Long): Future[Seq[Task]] = database.run(tasks.filter(_.userId === userId).result)

  def getTaskByUserId(userId: Long, taskId: Long): Future[Option[TaskTable#TableElementType]] = database.run(tasks.filter(s => s.userId === userId && s.id === taskId).result.headOption)

  def updateTaskByUserId(userId: Long, taskId: Long, task: Task): Future[Int] = database.run(tasks.filter(s => s.userId === userId && s.id === taskId).update(task))

  def deleteTask(userId: Long, taskId: Long): Future[Int] = database.run(tasks.filter(s => s.userId === userId && s.id === taskId).delete)
}