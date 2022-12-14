package server.actors.server_actor

import akka.actor.typed.scaladsl.{Behaviors, Routers}
import akka.actor.typed.{Behavior, PostStop}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import server.actors.database_actor.DatabaseActor
import server.routes.Routes

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Server {

  sealed trait Message
  private final case class StartFailed(e: Throwable) extends Message
  private final case class Started(binding: ServerBinding) extends Message
  case object Stop extends Message

  def apply(host: String, port: Int, poolSize: Int): Behavior[Message] =
    Behaviors.setup { ctx =>
      implicit val system = ctx.system

      // creating a pool of DatabaseActor
      val pool = Routers.pool(poolSize)(DatabaseActor())
      val dbActors = ctx.spawn(pool, "worker-pool")

      // initializes our routes with giving them ActorRef[DatabaseActor]
      val routes = new Routes(dbActors).routes
      val bindingFuture = Http().newServerAt(host, port).bind(routes)

      // uses callback
      ctx.pipeToSelf(bindingFuture) {
        case Success(binding) => Started(binding)
        case Failure(e)       => StartFailed(e)
      }

      def running(binding: ServerBinding): Behavior[Message] = {
        Behaviors
          .receiveMessagePartial[Message] { case Stop =>
            ctx.log.info(
              "Stopping server http://{}:{}/",
              binding.localAddress.getHostString,
              binding.localAddress.getPort
            )
            Behaviors.stopped
          }
          .receiveSignal {
            // we receive PostStop when the actor was stopped
            case (_, PostStop) =>
              binding.unbind()
              Behaviors.same
          }
      }

      def starting(wasStopped: Boolean): Behaviors.Receive[Message] =
        Behaviors.receiveMessage {
          case StartFailed(cause) =>
            throw new RuntimeException("Server failed to start", cause)
          case Started(binding) =>
            println("success")
            ctx.log.info(
              "Server online at http://{}:{}/",
              binding.localAddress.getHostString,
              binding.localAddress.getPort
            )
            if (wasStopped) ctx.self ! Stop
            running(binding)
          case Stop =>
            // we got a stop message but haven't completed starting yet,
            // we cannot stop until starting has completed
            starting(wasStopped = true)
        }

      starting(wasStopped = false)
    }

}
