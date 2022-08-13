package server.database.model.requests

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.joda.time.DateTime
import spray.json.{DefaultJsonProtocol, NullOptions, RootJsonFormat}

case class TimeSlot(
    masterId: String,
    startT: DateTime,
    finishT: DateTime,
    price: Double
)
// used to construct DateTime from String
object TimeSlotWithStr {
  def apply(masterId: String, startT: String, finishT: String, price: Double) =
    TimeSlot(masterId, new DateTime(startT).toDateTimeISO, new DateTime(finishT).toDateTimeISO, price)
}

case class CreateWorkingScheduleRequest(
    companyId: String,
    timeslots: List[TimeSlot]
)

case object CreateWorkingScheduleRequest
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with NullOptions {
  implicit val timeSlotFormat: RootJsonFormat[TimeSlot] = jsonFormat4(
    TimeSlotWithStr.apply
  )
  implicit val createWorkingScheduleRequestFormat
      : RootJsonFormat[CreateWorkingScheduleRequest] = jsonFormat2(
    CreateWorkingScheduleRequest.apply
  )
}
