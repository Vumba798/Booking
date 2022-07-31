package server.database

import Booking.Booking
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import server.database.DAO.{bookings, toJson}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object DatabaseActor {

  // TODO
  implicit val ec: ExecutionContext = ???

  // mapper is used in marshalling objects into json strings



  sealed trait Command // Messages that HttpActor can receive
  sealed trait BookingCommands extends Command // parameters for all Booking methods
  final case class GetAvailableTimeCommand(
    replyTo: ActorRef[Response],
    startT: String,
    finishT: String,
    companyId: Int,
    master: String) extends BookingCommands
  // TODO add more case classes

  sealed trait AuthCommands extends Command
  // TODO add more case classes which extend AuthCommands trait

  sealed trait AnalyticsCommands extends Command
  // TODO add more case classes which extend AnalyticsCommands trait


  sealed trait Response
  case class JsonResponse(json: String) extends Response
  case class InvalidRequest(e: Throwable) extends Response // TODO maybe change it

  def apply(): Behavior[Command] = Behaviors.receiveMessage {
    case _: BookingCommands => bookingCommandsHandler _
      Behaviors.same

    // TODO add more cases (one for each Command)
    case _ => ???
  }

  private def bookingCommandsHandler(c: BookingCommands): Unit = c match {
    case p: GetAvailableTimeCommand =>
      val dataFuture = Booking.getAvailableTime(
        p.startT, p.finishT, p.companyId, p.master)

      dataFuture.onComplete {
        case Success(x) => p.replyTo ! JsonResponse(toJson(x))
        // TODO maybe change InvalidRequest
        case Failure(e) => p.replyTo ! InvalidRequest(e)
      }
  }
}
