package server.database.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.github.nscala_time.time.Imports.DateTime
import org.mongodb.scala.bson.ObjectId

case class BookingRecord(
    @JsonSerialize(using = classOf[ToStringSerializer])
    _id: ObjectId,
    companyId: ObjectId,
    masterId: ObjectId,
    price: Double,
    info: String,
    startT: DateTime,
    finishT: DateTime,
    clientTel: String,
    status: String
) extends DbElement

object BookingRecord {

  // apply() used for constructing "id: ObjectId" field
  def apply(
      companyId: ObjectId,
      masterId: ObjectId,
      price: Double,
      info: String,
      startT: String,
      finishT: String,
      clientTel: String,
      status: String
  ): BookingRecord =
    BookingRecord(
      new ObjectId,
      companyId,
      masterId,
      price,
      info,
      new DateTime(startT),
      new DateTime(finishT),
      clientTel,
      status
    )
}
