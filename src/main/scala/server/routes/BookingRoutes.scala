package server.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import server.database.DatabaseActor
import server.database.DatabaseActor.{CreateBookingCommand, EditBookingCommand, GetAvailableTimeCommand, GetBookingsCommand, GetCompanyBookingsCommands, JsonResponse, StatusCodeResponse}
import server.database.model.requests.CreateBookingRequest

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
        "companyId".as[String],
        "master".as[String]
      ) { (startT, finishT, companyId, master) =>

        val dbResponse = dbActors.ask(ref =>
          GetAvailableTimeCommand(ref, startT, finishT, companyId, master))

        // TODO add exception handler (onFailure) or replace "onSuccess" with onComplete to handle failures manually
        onSuccess(dbResponse) {
          case JsonResponse(json) => complete(HttpEntity(ContentTypes.`application/json`, json))
          case _ => complete("undefined response")
        }
      }
    }
  }

  val createBooking: Route = path("createBooking") {
    /* todo where its better to hold params? in query or in body?
    post {
      parameters(
        "companyId".as[String],
        "masterId".as[String],
        "startT".as[String],
        "finishT".as[String],
        "clientTel".as[String]
      ) { (companyId, masterId, startT, finishT, clientTel) =>
        val dbResponse = dbActors.ask(ref =>
          CreateBookingCommand(ref, companyId, masterId, startT, finishT, clientTel))

        onSuccess(dbResponse) {
          case StatusCodeResponse(code: Int) => complete(StatusCode.int2StatusCode(code))
          case _ => complete("undefined response") // TODO
        }
      }
    }
     */
    post {
      entity(as[CreateBookingRequest]) { request =>
        val dbResponse = dbActors.ask(ref =>
          CreateBookingCommand(ref, request.companyId, request.masterId, request.startT, request.finishT, request.clientTel))
        onSuccess(dbResponse) {
          case StatusCodeResponse(code: Int) => complete(StatusCode.int2StatusCode(code))
          case _ => complete("undefined response") // TODO

        }
      }
    }
  }

  val getBookings: Route = path("getBookings") {
    get {
      parameters(
        "companyId".as[String],
        "clientTel".as[String],
        "startT".as[String],
        "finishT".as[String]
      ) { (companyId, clientTel, startT, finishT) =>
        val dbResponse = dbActors.ask(ref =>
          GetBookingsCommand(ref, companyId, clientTel, startT, finishT))
        onSuccess(dbResponse) {
          case JsonResponse(json) => complete(HttpEntity(ContentTypes.`application/json`, json))
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
      ) { (bookingId, status, message) =>
        val msg = message match {
          case Some(str) => str
          case None => ""
        }
        val dbResponse = dbActors.ask(ref =>
          EditBookingCommand(ref, bookingId, status, msg))
        onSuccess(dbResponse) {
          case StatusCodeResponse(code: Int) => complete(StatusCode.int2StatusCode(code))
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
      ) { (companyId, startT, finishT) =>
        val dbResponse = dbActors.ask(ref =>
          GetCompanyBookingsCommands(ref, companyId, startT, finishT))
        onSuccess(dbResponse) {
          case JsonResponse(json) => complete(HttpEntity(ContentTypes.`application/json`, json))
          case _ => complete("undefined response") // TODO
        }
      }
    }
  }

}