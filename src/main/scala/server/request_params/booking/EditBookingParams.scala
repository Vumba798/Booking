package server.request_params.booking

case class EditBookingParams(
    bookingId: String,
    status: String,
    message: Option[String]
)
