package server.routes

import akka.http.scaladsl.server.Directives.{concat, pathPrefix}
import akka.http.scaladsl.server.Route
import server.routes.BookingRoutes.{createBooking, editBooking, getAvailableTime, getBookings, getCompanyBookings}


object Routes {
  val routes: Route = pathPrefix("api") {
    pathPrefix("booking") {
      concat(
        getAvailableTime,
        createBooking,
        getBookings,
        editBooking,
        getCompanyBookings
      )
    }
    // todo provide implementation
    /*
    pathPrefix("auth") {
      registration ~ // api/auth/registration
      login ~ // api/auth/login
      invite // api/auth/invite)
    } ~
    pathPrefix("analytics") {
      getAllClients ~ // api/analytics/get_all
      getLeaderBoard ~ // api/analytics/get_leader
      getBusiestTimes // api/analytics/get_best_time
    }
     */
  }
}