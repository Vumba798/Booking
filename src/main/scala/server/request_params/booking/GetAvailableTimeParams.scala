package server.request_params.booking

final case class GetAvailableTimeParams(
    startT: String,
    finishT: String,
    companyId: String,
    masterId: String
)
