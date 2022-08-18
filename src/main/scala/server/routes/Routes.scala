package server.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives.{complete, concat, pathPrefix}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import server.actors.database_actor.DatabaseActor
import server.exceptions.{InsertionException, IntersectionException}
import server.routes.DbRoutesTrait

final class Routes(
    override protected val dbActors: ActorRef[DatabaseActor.Command]
)(
    override protected implicit val system: ActorSystem[Nothing]
) extends DbRoutesTrait {

  /* TODO implement ExceptionHandler
  implicit def exceptionHandler =
    ExceptionHandler {
      case e: InsertionException =>
        complete(???)
      case e: IntersectionException =>
        complete(???)
      case e: RuntimeException =>
        complete(???)
      case _ =>
        complete(???)
    }

   */

  private val bookingRoutes = new BookingRoutes(dbActors).routes
  /*
  private val authRoutes = ??? // TODO provide implementation
  private val analyticsRoutes = ??? // TODO provide implementation

   */

  val routes: Route = pathPrefix("api") {
    concat(
      //    authRoutes,
      bookingRoutes
      //    analyticsRoutes,
    )
  }
}
