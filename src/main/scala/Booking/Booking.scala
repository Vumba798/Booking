package Booking

import org.bson.types.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters.{gt, gte, lt, lte}
import org.mongodb.scala.result.{InsertManyResult, InsertOneResult}
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters.equal
import server.database.Dao
import server.database.model._
import server.database.model.requests.CreateWorkingScheduleRequest

import scala.concurrent.{ExecutionContext, Future}

object Booking {

  // TODO check if it is better to move "new ObjectId(...)" in a separate val
  def getAvailableTime(
      startTString: String,
      finishTString: String,
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
      startTString: String,
      finishTString: String,
      clientPhone: String
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
    clientPhone: String,
    startT: String,
    finishT: String
  )(implicit
      ec: ExecutionContext
  ): Future[Seq[BookingRecord]] = {
    Dao.bookings
      .find(
        Filters.and(
          equal("companyId", new ObjectId(companyId)),
          equal("clientPhone", clientPhone),
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

  def createWorkingSchedule(
      params: CreateWorkingScheduleRequest
  )(implicit
      ec: ExecutionContext
  ): Future[InsertManyResult] = {

    val schedule = params.timeslots.map { slot =>
      BookingRecord.free(
        companyId = new ObjectId(params.companyId),
        masterId = new ObjectId(slot.masterId),
        price = slot.price,
        startT = slot.startT,
        finishT = slot.finishT
      )
    }

    def hasIntersections = {
      val listOfFutures = params.timeslots
        .map { slot =>
          Dao.bookings
            .find(
              Filters.and(
                equal("companyId", new ObjectId(params.companyId)),
                equal("masterId", new ObjectId(slot.masterId)),
                Filters.or(
                  Filters.and(
                    gte("startT", slot.startT),
                    lt("finishT", slot.startT)
                  ),
                  Filters.and(
                    gt("startT", slot.finishT),
                    lte("finishT", slot.finishT)
                  )
                )
              )
            )
            .headOption()
            .recoverWith(e => Future.failed(e))
            .map {
              case None    => false
              case Some(_) => true
            }
        }

      Future
        .sequence(listOfFutures)
        .map(_.contains(true))
    }

    hasIntersections.flatMap {
      case true =>
        Future.failed(
          new IllegalArgumentException("New schedule intersects with old one!")
        )
      case false =>
        Dao.bookings
          .insertMany(schedule)
          .toFuture()
          .recoverWith(e => Future.failed(e))
    }

  }

}
