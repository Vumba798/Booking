package server.database.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId

case class Company(
    @JsonSerialize(using = classOf[ToStringSerializer])
    _id: ObjectId,
    name: String
) extends DbElement

object Company {
  def apply(name: String) = new Company(new ObjectId, name)
}
