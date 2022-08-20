package server.actors.database_actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import server.actors.database_actor.commands._
import server.actors.database_actor.handlers._


object DatabaseActor {

  trait Command // Messages that HttpActor can receive
  trait Response // Messages that will be transmitted to Routes

  def apply(): Behavior[Command] = Behaviors.setup { ctx =>
    implicit val ec = ctx.executionContext

    Behaviors.receiveMessage {
      case m: BookingCommand => BookingCommandHandler(m)
      case m: AuthCommand => AuthCommandHandler(m)
      case m: AnalyticsCommand => AnalyticsCommandHandler(m)
    }
  }
}
