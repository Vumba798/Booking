package server.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives.{concat, pathPrefix}
import akka.http.scaladsl.server.Route
import server.database.DatabaseActor
import server.routes.DbRoutesTrait

final class Routes
  (override protected val dbActors: ActorRef[DatabaseActor.Command])
  (override protected implicit val system: ActorSystem[Nothing]) extends DbRoutesTrait {

  private val bookingRoutes = new BookingRoutes(dbActors).routes
  private val authRoutes = ??? // TODO provide implementation
  private val analyticsRoutes = ??? // TODO provide implementation


  lazy val routes: Route = pathPrefix("api") {
    concat(
      authRoutes,
      bookingRoutes,
      analyticsRoutes,
    )
  }
}