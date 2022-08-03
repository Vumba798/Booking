package Booking

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import org.bson.types.ObjectId
import org.mongodb.scala.result.InsertOneResult
//import org.bson.types.ObjectId
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters.equal
import server.database.DatabaseActor.GetAvailableTimeCommand
import server.database.Dao
import server.database.model._

import scala.concurrent.{ExecutionContext, Future}

object Booking {
  // TODO change
  implicit val ec: ExecutionContext = ???



  def getAvailableTime(
    startT: String,
    finishT: String,
    companyId: String,
    masterId: String): Future[Seq[BookingRecord]] = {
    Dao.bookings.find(Filters.and(
        equal("startT", startT),
        equal("finishT", finishT),
        equal("companyId", new ObjectId(companyId)),
        equal("masterId", new ObjectId(masterId))))
      .toFuture()
      .recoverWith(e => Future.failed(e))
  }

  def createBooking(
  price: Double,
  startT: String,
  finishT: String,
  companyId: Int,
  master: String) = ???
    /* todo implement
  Database.bookings
      .insertOne(BookingRecord(startT, finishT, companyId, master))

     */


}
