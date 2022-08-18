import akka.actor.typed.ActorSystem
import server.actors.server_actor.Server

object Main extends App {
  ActorSystem(Server(), "booking-system")
}
