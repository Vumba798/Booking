import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import server.routes.Routes.routes
import scala.concurrent.ExecutionContextExecutor

object Main extends App {
  // TODO replace hardcoded values
  val host = "localhost"
  val port = 8080

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "booking_system")
  implicit val ec: ExecutionContextExecutor = system.executionContext

  val bindingFuture = Http().newServerAt(host, port).bind(routes)

}
