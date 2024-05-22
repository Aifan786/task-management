package model

import java.sql.Timestamp

case class Task(id:Option[Long], userId:Long, title: String, description: String, dueDate: Timestamp, status: String, createdAt: Option[Timestamp], updatedAt: Option[Timestamp])

case class TaskRequest(title: String, description: String, dueDate: Timestamp, status: String)
