package server.actors.database_actor.handlers

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import server.actors.database_actor.DatabaseActor.Command
import server.actors.database_actor.{ExceptionResponse, JsonResponse, SuccessResponse}
import server.actors.database_actor.commands.{BookingCommand, CreateBookingCommand, CreateWorkingScheduleCommand, EditBookingCommand, GetAvailableTimeCommand, GetBookingsCommand, GetCompanyBookingsCommand}
import server.api.Booking
import server.database.Dao.toJson

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object BookingCommandHandler {
  def apply(c: BookingCommand)
           (implicit ec: ExecutionContext)
  : Behavior[Command] = c match {
    // each case must return Behaviors.same
    case c: GetAvailableTimeCommand =>
      Booking
        .getAvailableTime(c.params)
        .onComplete {
          case Success(x) => c.replyTo ! JsonResponse(toJson(x))
          case Failure(e) => c.replyTo ! ExceptionResponse(e)
        }
      Behaviors.same

    case c: CreateBookingCommand =>
      Booking
        .createBooking(c.params)
        .onComplete {
          case Success(x) => c.replyTo ! SuccessResponse(201)
          case Failure(e) => c.replyTo ! ExceptionResponse(e)
        }
      Behaviors.same

    case c: GetBookingsCommand =>
      Booking
        .getBookings(c.params)
        .onComplete {
          case Success(records) => c.replyTo ! JsonResponse(toJson(records))
          case Failure(e)       => c.replyTo ! ExceptionResponse(e)
        }
      Behaviors.same

    case c: EditBookingCommand =>
      Booking
        .editBooking(c.params)
        .onComplete {
          case Success(x) => c.replyTo ! SuccessResponse(200)
          case Failure(e) => c.replyTo ! ExceptionResponse(e)
        }

      Behaviors.same

    case c: GetCompanyBookingsCommand =>
      Booking
        .getCompanyBookings(c.params)
        .onComplete { case Success(records) =>
          c.replyTo ! JsonResponse(toJson(records))
        }

      Behaviors.same

    case c: CreateWorkingScheduleCommand =>
      Booking
        .createWorkingSchedule(c.params)
        .onComplete {
          case Success(_) => c.replyTo ! SuccessResponse(200)
          case Failure(e) => c.replyTo ! ExceptionResponse(e)
        }
      Behaviors.same
  }

}
