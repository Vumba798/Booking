package server.database

import Booking.Booking
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import server.database.Dao.{bookings, toJson}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object DatabaseActor {

  // TODO
  implicit val ec: ExecutionContext = ExecutionContext.global


  sealed trait Command // Messages that HttpActor can receive
  sealed trait BookingCommands extends Command // parameters for all Booking methods
  final case class GetAvailableTimeCommand(
    replyTo: ActorRef[Response],
    startT: String,
    finishT: String,
    companyId: String,
    masterId: String) extends BookingCommands

  final case class CreateBookingCommand(
    replyTo: ActorRef[Response],
    companyId: String,
    masterId: String,
    startT: String,
    finishT: String,
    clientTel: String) extends BookingCommands
  // TODO add more case classes

  sealed trait AuthCommands extends Command
  // TODO add more case classes which extend AuthCommands trait

  sealed trait AnalyticsCommands extends Command
  // TODO add more case classes which extend AnalyticsCommands trait


  sealed trait Response
  case class JsonResponse(json: String) extends Response
  case class StatusCodeResponse(code: Int) extends Response
  case class InvalidRequest(e: Throwable) extends Response // TODO maybe change it

  def apply(): Behavior[Command] = Behaviors.receiveMessage {
    case m: BookingCommands => bookingCommandsHandler(m)
    case m: AuthCommands => authCommandsHandler(m)
    case m: AnalyticsCommands => analyticsCommandsHandler(m)
  }

  private def bookingCommandsHandler(c: BookingCommands): Behavior[Command] = c match {
    // each case must return Behaviors.same
    case p: GetAvailableTimeCommand =>
      val dataFuture = Booking.getAvailableTime(
        p.startT, p.finishT, p.companyId, p.masterId)

      dataFuture.onComplete {
        case Success(x) => p.replyTo ! JsonResponse(toJson(x))
        case Failure(e) => p.replyTo ! InvalidRequest(e)
      }
      Behaviors.same

    case p: CreateBookingCommand =>
      val dataFuture = Booking.createBooking(
        p.companyId, p.masterId, p.startT, p.finishT, p.clientTel
      )

      dataFuture.onComplete {
        case Success(x) => p.replyTo ! StatusCodeResponse(201)
        case Failure(e) => p.replyTo ! InvalidRequest(e)
      }
      Behaviors.same

    case _ => ???; Behaviors.same // TODO
  }

  private def authCommandsHandler(c: AuthCommands): Behavior[Command] = c match {
    // TODO
    case _ => Behaviors.same
  }

  private def analyticsCommandsHandler(c: AnalyticsCommands): Behavior[Command] = c match {
    // TODO
    case _ => Behaviors.same
  }
}

