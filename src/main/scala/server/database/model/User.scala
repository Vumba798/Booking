package server.database.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId

case class User(
  @JsonSerialize(using = classOf[ToStringSerializer])
  _id: ObjectId,
  email: String,
  full_name: String,
  phone: String,
  companyId: ObjectId,
  password: String,
  role: String) extends DbElement

object User {

  // apply() used for constructing "id: ObjectId" field
  def apply(
    email: String,
    full_name: String,
    phone: String,
    companyId: ObjectId,
    password: String,
    role: String): User =
    User(new ObjectId, email, full_name, phone, companyId, password, role)

}
