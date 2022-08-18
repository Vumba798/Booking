package server.actors.database_actor.commands

import akka.actor.typed.ActorRef
import server.actors.database_actor.DatabaseActor._
import server.request_params.booking._

sealed trait BookingCommand
    extends Command // parameters for all Booking methods

final case class GetAvailableTimeCommand(
    replyTo: ActorRef[Response],
    params: GetAvailableTimeParams
) extends BookingCommand

final case class CreateBookingCommand(
    replyTo: ActorRef[Response],
    params: CreateBookingParams
) extends BookingCommand

final case class GetBookingsCommand(
    replyTo: ActorRef[Response],
    params: GetBookingsParams
) extends BookingCommand

final case class EditBookingCommand(
    replyTo: ActorRef[Response],
    params: EditBookingParams
) extends BookingCommand

final case class GetCompanyBookingsCommand(
    replyTo: ActorRef[Response],
    params: GetCompanyBookingsParams
) extends BookingCommand

final case class CreateWorkingScheduleCommand(
    replyTo: ActorRef[Response],
    params: CreateWorkingScheduleParams
) extends BookingCommand
