package server.actors.database_actor

import server.actors.database_actor.DatabaseActor.Response


case class JsonResponse(json: String) extends Response
case class SuccessResponse(code: Int) extends Response
case class ExceptionResponse(e: Throwable) extends Response
