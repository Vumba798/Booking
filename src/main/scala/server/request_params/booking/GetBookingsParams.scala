package server.request_params.booking

final case class GetBookingsParams (
  companyId: String,
  startT: String,
  finishT: String,
  clientPhone: String
)
