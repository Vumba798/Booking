package server.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}

object BookingRoutes {

  // TODO remove printParams (helps in debugging)
  private def formatParams(params: (String, String)*): HttpEntity.Strict = {
    /* formats params into html (using notation "paramName: paramValue\n") e.g.
     companyId: 1234
     startT: 24-06-2022 (another format may be used)
     */
    //
    val s = params.foldLeft("<h1>")((z, p) => z + p._1 + ": " + p._2 + "<br>") + "</h1>"
    HttpEntity(ContentTypes.`text/html(UTF-8)`, s)
  }

  val getAvailableTime: Route = path("getAvailableTime") {
    get {
      parameters(
        "startT".as[String],
        "finishT".as[String],
        "companyId".as[Int],
        "master".as[String]
      ) { (startT, finishT, companyId, master) =>
        complete(
          // TODO change contents of complete function (replace formatParams)
          formatParams(
            ("startT", startT),
            ("finishT", finishT),
            ("companyId", companyId.toString),
            ("master", master))
        )
      }
    }
  }

  val createBooking: Route = path("createBooking") {
    post {
      parameters(
        "fullName".as[String],
        "email".as[String],
        "master".as[String]
      ) { (fullName, email, master) =>
        complete(
          formatParams(
            ("fullName", fullName),
            ("email", email),
            ("master", master)
          )
        )
      }
    }
  }

  val getBookings: Route = path("getBookings") {
    get {
      parameters(
        "companyId".as[Int],
        "email".as[String],
        "startT".as[String],
        "finishT".as[String]
      ) { (companyId, email, startT, finishT) =>
        complete(
          // TODO change contents of complete function (replace formatParams)
          formatParams(
            ("companyId", companyId.toString),
            ("email", email),
            ("startT", startT),
            ("finishT", finishT))
        )
      }
    }
  }

  val editBooking: Route = path("editBooking") {
    patch {
      parameters(
        "id".as[Int],
        "status".as[String],
        "message".as[String].optional
      ) { (id, status, message) =>
        val msg = message match {
          case Some(str) => str
          case None => "None"
        }
        complete(
          // TODO change contents of complete function (replace formatParams)
          formatParams(
            ("id", id.toString),
            ("status", status),
            ("message", msg)
          )
        )
      }
    }
  }

  val getCompanyBookings: Route = path("getCompanyBookings") {
    get {
      parameters(
        "companyId".as[Int],
        "startT".as[String],
        "finishT".as[String]
      ) { (companyId, startT, finishT) =>
        complete(
          formatParams(
            ("companyId", companyId.toString),
            ("startT", startT),
            ("finishT", finishT)
          )
        )
      }
    }
  }

}