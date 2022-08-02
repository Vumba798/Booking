package server.database.model

import org.bson.types.ObjectId

case class Company(id: ObjectId, name: String) extends DbElement

object Company {
  def apply(name: String) = new Company(id = new ObjectId, name)
}
