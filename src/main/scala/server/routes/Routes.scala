package server.routes

import akka.http.scaladsl.server.Route

trait Routes extends AuthRoutes with AnalyticsRoutes {
  val routes: Route = pathPrefix("api") {
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
  }
}