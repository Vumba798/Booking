package server.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import server.database.DatabaseActor
import server.database.DatabaseActor.{GetAvailableTimeCommand, JsonResponse}

import scala.concurrent.duration.DurationInt

class BookingRoutes(override protected val dbActors: ActorRef[DatabaseActor.Command])
                   (override protected implicit val system: ActorSystem[Nothing]) extends DbRoutesTrait {

  implicit val timeout: Timeout = 5.seconds

  lazy val routes: Route = pathPrefix("booking") {
    concat(
      getAvailableTime,
      createBooking,
      getBookings,
      editBooking,
      getCompanyBookings,
    )
  }


  val getAvailableTime: Route = path("getAvailableTime") {
    get {
      parameters(
        "startT".as[String],
        "finishT".as[String],
        "companyId".as[Int],
        "master".as[String]
      ) { (startT, finishT, companyId, master) =>

        val dbResponse = dbActors.ask(ref =>
          GetAvailableTimeCommand(ref, startT, finishT, companyId, master))

        // TODO add exception handler (onFailure) or replace "onSuccess" with onComplete to handle failures manually
        onSuccess(dbResponse) {
          case JsonResponse(json) => complete(HttpEntity(ContentTypes.`application/json`, json))
          case _ => complete("")
        }
      }
    }
  }

  val createBooking: Route = path("createBooking") {
    post {
      parameters(
        "fullName".as[String],
        "email".as[String],
        "master".as[String]
      ) { (fullName, email, master) =>
        complete(
          ???
        )
      }
    }
  }

  val getBookings: Route = path("getBookings") {
    get {
      parameters(
        "companyId".as[Int],
        "email".as[String],
        "startT".as[String],
        "finishT".as[String]
      ) { (companyId, email, startT, finishT) =>
        complete(
            ???
        )
      }
    }
  }

  val editBooking: Route = path("editBooking") {
    patch {
      parameters(
        "id".as[Int],
        "status".as[String],
        "message".as[String].optional
      ) { (id, status, message) =>
        val msg = message match {
          case Some(str) => str
          case None => "None"
        }
        complete(
          ???
        )
      }
    }
  }

  val getCompanyBookings: Route = path("getCompanyBookings") {
    get {
      parameters(
        "companyId".as[Int],
        "startT".as[String],
        "finishT".as[String]
      ) { (companyId, startT, finishT) =>
        complete(
          ???
        )
      }
    }
  }

}