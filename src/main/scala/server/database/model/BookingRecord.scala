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
    clientPhone: String,
    status: String
) extends DbElement {
  // TODO check if overridden fields really work
  // added 'this' for clarity
  def modify(
//    _id: ObjectId = _id,
      companyId: ObjectId = this.companyId,
      masterId: ObjectId = this.masterId,
      price: Double = this.price,
      info: String = this.info,
      startT: DateTime = this.startT,
      finishT: DateTime = this.finishT,
      clientPhone: String = this.clientPhone,
      status: String = this.status
  ): BookingRecord =
    BookingRecord(
      new ObjectId(),
      companyId,
      masterId,
      price,
      info,
      startT,
      finishT,
      clientPhone,
      status)
}

object BookingRecord {

  // apply() used for constructing "id: ObjectId" field
  def apply(
      companyId: ObjectId,
      masterId: ObjectId,
      price: Double,
      info: String,
      startT: String,
      finishT: String,
      clientPhone: String,
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
      clientPhone,
      status
    )

  // creates free timeslot
  def free(
      companyId: ObjectId,
      masterId: ObjectId,
      price: Double,
      startT: DateTime,
      finishT: DateTime
  ): BookingRecord =
    BookingRecord(
      new ObjectId,
      companyId,
      masterId,
      price,
      "",
      startT,
      finishT,
      "",
      "free"
    )

}
