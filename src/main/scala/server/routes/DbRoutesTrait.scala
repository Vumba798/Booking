package server.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import server.database.DatabaseActor

trait DbRoutesTrait {
  implicit protected val system: ActorSystem[Nothing]
  protected val dbActors: ActorRef[DatabaseActor.Command]
}
