package server.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives.{complete, concat, pathPrefix}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import server.actors.database_actor.DatabaseActor
import server.exceptions.{InsertionException, IntersectionException}

final class Routes(
    override protected val dbActors: ActorRef[DatabaseActor.Command]
)(
    override protected implicit val system: ActorSystem[Nothing]
) extends DbRoutesTrait {

  implicit def exceptionHandler =
    ExceptionHandler {
      case e: InsertionException =>
        complete(s"An insertion exception has occurred while querying database:\n${e.getMessage}")
      case e: IntersectionException =>
        complete(s"An intersection exception has occurred while querying database:\n${e.getMessage}")
      case e: RuntimeException =>
        complete(s"A runtime exception has occurred while proceeding your request:\n${e.getMessage}")
      case e: Throwable =>
        complete(s"An unknown exception has occurred:\n${e.getMessage}")
    }

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
