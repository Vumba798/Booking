import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import server.routes.Routes
//import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.server.{Directive, Route, RouteResult}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn


object Main extends App with Routes {
  // TODO replace hardcoded values
  val host = "localhost"
  val port = 8080

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "booking_system")
  implicit val ec: ExecutionContextExecutor = system.executionContext

  val bindingFuture = Http().newServerAt(host, port).bind(routes)

}
