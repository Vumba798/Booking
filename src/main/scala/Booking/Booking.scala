package Booking

import org.bson.types.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters.{gte, lte}
import org.mongodb.scala.result.InsertOneResult
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters.equal
import server.database.Dao
import server.database.model._

import scala.concurrent.{ExecutionContext, Future}

object Booking {

  // TODO check if it is better to move "new ObjectId(...)" in a separate val
  def getAvailableTime(
      startT: String,
      finishT: String,
      companyId: String,
      masterId: String
  )(implicit
      ec: ExecutionContext
  ): Future[Seq[BookingRecord]] = {
    Dao.bookings
      .find(
        Filters.and(
          equal("startT", startT),
          equal("finishT", finishT),
          equal("companyId", new ObjectId(companyId)),
          equal("masterId", new ObjectId(masterId))
        )
      )
      .toFuture()
      .recoverWith(e => Future.failed(e))
  }

  def createBooking(
      companyId: String,
      masterId: String,
      startT: String,
      finishT: String,
      clientTel: String
  )(implicit
      ec: ExecutionContext
  ): Future[InsertOneResult] = {
    Dao.bookings
      .insertOne(
        BookingRecord(
          new ObjectId(companyId),
          new ObjectId(masterId),
          price = 2000, // todo change
          info = "",
          startT,
          finishT,
          clientTel,
          status = "Created"
        )
      )
      .toFuture()
      .recoverWith(e => Future.failed(e))
  }

  def getBookings(
      companyId: String,
      clientTel: String,
      startT: String,
      finishT: String
  )(implicit
      ec: ExecutionContext
  ): Future[Seq[BookingRecord]] = {
    Dao.bookings
      .find(
        Filters.and(
          equal("companyId", new ObjectId(companyId)),
          equal("clientTel", clientTel),
          equal("startT", startT),
          equal("finishT", finishT)
        )
      )
      .toFuture()
      .recoverWith(e => Future.failed(e))
  }

  def editBooking(
      bookingId: String,
      status: String,
      message: String
  )(implicit
      ec: ExecutionContext
  ): Future[BookingRecord] = {
    Dao.bookings
      .findOneAndUpdate(
        Filters.equal("id", new ObjectId(bookingId)),
        Document("$set" -> Document("status" -> status, "message" -> message))
      )
      .toFuture()
      .recoverWith(e => Future.failed(e))
  }

  def getCompanyBookings(
      companyId: String,
      startT: String,
      finishT: String
  )(implicit
      ec: ExecutionContext
  ): Future[Seq[BookingRecord]] = {
    Dao.bookings
      .find(
        Filters.and(
          equal("companyId", new ObjectId(companyId)),
          gte("startT", startT),
          lte("finishT", finishT)
        )
      )
      .toFuture()
      .recoverWith(e => Future.failed(e))
  }

}
