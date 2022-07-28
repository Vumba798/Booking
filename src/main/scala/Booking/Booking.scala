package Booking

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import org.mongodb.scala.model.Filters.equal
import server.Database.Database
import server.database.model._

import scala.concurrent.{ExecutionContext, Future}

// Actor that handles Booking API
object Booking {
  // TODO change
  implicit val ec: ExecutionContext = ???



  def getAvailableTime(startT: String,
    finishT: String,
    companyId: Int,
    master: String): Future[Seq[BookingRecord]] = Database.bookings
      .find(equal("startT", startT))
      .toFuture()
      .recoverWith(e => Future.failed(e))

  def createBooking(price: Double,
  startT: String,
  finishT: String,
  companyId: Int,
  master: String) = ???
    /* todo implement
  Database.bookings
      .insertOne(BookingRecord(startT, finishT, companyId, master))

     */


}
