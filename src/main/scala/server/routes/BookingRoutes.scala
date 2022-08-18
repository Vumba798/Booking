package server.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import server.actors.database_actor._
import server.actors.database_actor.commands._
import server.request_params.booking._

import scala.concurrent.duration.DurationInt

class BookingRoutes(
    override protected val dbActors: ActorRef[DatabaseActor.Command]
)(
    override protected implicit val system: ActorSystem[Nothing]
) extends DbRoutesTrait {

  private implicit val timeout: Timeout = 5.seconds

  lazy val routes: Route = pathPrefix("booking") {
    concat(
      getAvailableTime,
      createBooking,
      getBookings,
      editBooking,
      getCompanyBookings,
      createWorkingSchedule
    )
  }

  val getAvailableTime: Route = path("getAvailableTime") {
    get {
      parameters(
        "startT".as[String],
        "finishT".as[String],
        "companyId".as[String],
        "masterId".as[String]
      )
        .as(GetAvailableTimeParams) { params =>
          val dbResponse =
            dbActors.ask(ref => GetAvailableTimeCommand(ref, params))

          // TODO add exception handler (onFailure) or replace "onSuccess" with onComplete to handle failures manually
          onSuccess(dbResponse) {
            case JsonResponse(json) =>
              complete(HttpEntity(ContentTypes.`application/json`, json))
            case _ => complete("undefined response")
          }
        }
    }
  }

  val createBooking: Route = path("createBooking") {
    post {
      entity(as[CreateBookingParams]) { params =>
        val dbResponse = dbActors.ask(ref => CreateBookingCommand(ref, params))

        onSuccess(dbResponse) {
          case SuccessResponse(code: Int) =>
            complete(StatusCode.int2StatusCode(code))
          case _ => complete("undefined response") // TODO

        }
      }
    }
  }

  val getBookings: Route = path("getBookings") {
    get {
      parameters(
        "companyId".as[String],
        "startT".as[String],
        "finishT".as[String],
        "clientPhone".as[String]
      )
        .as(GetBookingsParams) { params =>
          val dbResponse = dbActors.ask(ref => GetBookingsCommand(ref, params))
          onSuccess(dbResponse) {
            case JsonResponse(json) =>
              complete(HttpEntity(ContentTypes.`application/json`, json))
            case _ => complete("undefined response") // TODO
          }
        }
    }
  }

  val editBooking: Route = path("editBooking") {
    patch {
      parameters(
        "bookingId".as[String],
        "status".as[String],
        "message".as[String].optional
      )
        .as(EditBookingParams) { params =>
          val dbResponse =
            dbActors.ask(ref => EditBookingCommand(ref, params))
          onSuccess(dbResponse) {
            case SuccessResponse(code: Int) =>
              complete(StatusCode.int2StatusCode(code))
            case _ => complete("undefined response") // TODO
          }
        }
    }
  }

  val getCompanyBookings: Route = path("getCompanyBookings") {
    get {
      parameters(
        "companyId".as[String],
        "startT".as[String],
        "finishT".as[String]
      )
        .as(GetCompanyBookingsParams) { params =>
          val dbResponse =
            dbActors.ask(ref => GetCompanyBookingsCommand(ref, params))
          onSuccess(dbResponse) {
            case JsonResponse(json) =>
              complete(HttpEntity(ContentTypes.`application/json`, json))
            case _ => complete("undefined response") // TODO
          }
        }
    }
  }

  val createWorkingSchedule: Route = path("createWorkingSchedule") {
    post {
      entity(as[CreateWorkingScheduleParams]) { params =>
        val dbResponse =
          dbActors.ask(ref => CreateWorkingScheduleCommand(ref, params))

        onSuccess(dbResponse) {
          case SuccessResponse(code) =>
            complete(StatusCode.int2StatusCode(code))
          case ExceptionResponse(e) => throw e
        }
      }
    }
  }

}
