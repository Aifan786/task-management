package model

import java.sql.Timestamp

case class User(id:Option[Long], name:String, email: String, createdAt: Option[Timestamp])
