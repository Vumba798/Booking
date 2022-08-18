package server.request_params.booking

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, NullOptions, RootJsonFormat}

case class CreateBookingParams(
    companyId: String,
    masterId: String,
    startT: String,
    finishT: String,
    clientPhone: String
)

case object CreateBookingParams
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with NullOptions {
  implicit val createBookingRequestFormat: RootJsonFormat[CreateBookingParams] =
    jsonFormat5(
      CreateBookingParams.apply
    )
}
