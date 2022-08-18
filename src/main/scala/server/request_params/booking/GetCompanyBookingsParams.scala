package server.request_params.booking

final case class GetCompanyBookingsParams(
    companyId: String,
    startT: String,
    finishT: String
)
