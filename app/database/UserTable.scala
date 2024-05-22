package database.mysql

import com.google.inject.Inject
import model.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.{JdbcBackend, JdbcProfile}
import slick.lifted.{ProvenShape, Rep}

import java.sql.Timestamp
import scala.concurrent.{ExecutionContext, Future}

class UserTable(tag: Tag) extends Table[User](tag,"users") {

  def id:Rep[Option[Long]] = column[Option[Long]]("id",O.PrimaryKey,O.AutoInc,O.Unique)
  def name:Rep[String] = column[String]("name")
  def email:Rep[String] = column[String]("email")
  def createdAt:Rep[Option[Timestamp]] = column[Option[Timestamp]]("created_at")

  type Data = (Option[Long], String, String, Option[Timestamp])

  def constructRole : Data=>User= {
    case (id:Option[Long], name:String, email: String, createdAt: Option[Timestamp]) => User(id, name, email, createdAt)
  }

  def extractRole:PartialFunction[User,Data] = {
    case User(id, name, email, createdAt) => (id, name, email, createdAt)
  }

  override def * : ProvenShape[User] = (id, name, email, createdAt) <> (constructRole,extractRole.lift)
}

class Users @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  val users :TableQuery[UserTable] = TableQuery[UserTable]
  val database: JdbcBackend#DatabaseDef = dbConfig.db

  def create(user: User): Future[Option[Long]] = database.run(users returning users.map(_.id) += user)

  def getUserById(id: Long): Future[Option[User]] = database.run(users.filter(_.id === id).result.headOption)

}
