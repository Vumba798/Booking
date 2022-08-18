package server.actors.database_actor.handlers

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import server.actors.database_actor.DatabaseActor.Command
import server.actors.database_actor.commands.AuthCommand

import scala.concurrent.ExecutionContext

object AuthCommandHandler {
  def apply(c: AuthCommand)
           (implicit ec: ExecutionContext)
  : Behavior[Command] = c match {
      // TODO
      case _ => Behaviors.same
    }
}
