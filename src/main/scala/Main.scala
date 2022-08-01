import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{Behaviors, Routers}
import akka.http.scaladsl.Http
import server.database.DatabaseActor
import server.routes.Routes

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main {

  def apply(): Behavior[Unit] = Behaviors.setup { ctx =>

    implicit val system: ActorSystem[Nothing] = ctx.system
    // TODO replace hardcoded values
    val host = "localhost"
    val port = 8080

    // creating pool of DatabaseActor
    val poolSize = 4
    val pool = Routers.pool(poolSize)(DatabaseActor())
    val dbActors = ctx.spawn(pool, "worker-pool")

    // initializes our routes with giving them ActorRef[DatabaseActor]
    val routes = new Routes(dbActors).routes
    val bindingFuture = Http().newServerAt(host, port).bind(routes)

    Behaviors.empty
  }

  def main(args: Array[String]): Unit = {
    ActorSystem(Main(), "booking-system")
    // TODO handle server stopping
    StdIn.readLine()
  }

}