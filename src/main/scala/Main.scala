import akka.actor.typed.ActorSystem
import server.actors.server_actor.Server

object Main extends App {
  val host = "localhost"
  val port = 8080
  val poolSize = 4
  ActorSystem(Server(host, port, poolSize), "booking-system")
}
