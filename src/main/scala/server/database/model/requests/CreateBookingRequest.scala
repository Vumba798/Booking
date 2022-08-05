package server.database.model.requests

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, NullOptions, RootJsonFormat}

case class CreateBookingRequest(
  companyId: String,
  masterId: String,
  startT: String,
  finishT: String,
  clientTel: String)

case object CreateBookingRequest extends SprayJsonSupport with DefaultJsonProtocol with NullOptions {
  implicit val createBookingRequestFormat: RootJsonFormat[CreateBookingRequest] = jsonFormat5(CreateBookingRequest.apply)
}
