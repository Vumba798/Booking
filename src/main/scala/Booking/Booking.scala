package Booking

import org.bson.types.ObjectId
import org.joda.time.DateTime
import org.mongodb.scala.FindObservable
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions
import org.mongodb.scala.model.Filters.{gt, gte, lt, lte}
import org.mongodb.scala.result.{InsertManyResult, InsertOneResult}
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters.equal
import server.database.Dao
import server.database.model._
import server.database.model.requests.{CreateWorkingScheduleRequest, TimeSlot}

import java.util.NoSuchElementException
import scala.concurrent.{ExecutionContext, Future}

object Booking {
  // TODO refactor statuses, move them into enum

  private def intersectsFilter(startT: DateTime, finishT: DateTime) =
    Filters.or(
      Filters.and(gt("finishT", startT), lte("finishT", finishT)),
      Filters.and(gte("startT", startT), lt("startT", finishT))
    )

  private def getMasterBookings(
      companyId: ObjectId,
      masterId: ObjectId,
      timeFilter: conversions.Bson = Filters.empty()
  ): FindObservable[BookingRecord] = {
    Dao.bookings.find(
      Filters.and(
        equal("companyId", companyId),
        equal("masterId", masterId),
        timeFilter
      )
    )
  }

  private def deleteRecords(
      seq: Seq[BookingRecord]
  )(implicit ec: ExecutionContext): Future[Seq[BookingRecord]] = {
    val ids = seq.map(_._id)
    Dao.bookings
      .deleteMany(Filters.in("id", ids))
      .toFutureOption()
      .map {
        case Some(res)
            if res.wasAcknowledged() && res.getDeletedCount == ids.length =>
          seq
        case _ =>
          throw new NoSuchElementException("Failed to delete records by id")
      }
  }

  // TODO check if it is better to move "new ObjectId(...)" in a separate val
  def getAvailableTime(
      startTString: String,
      finishTString: String,
      companyId: String,
      masterId: String
  )(implicit
      ec: ExecutionContext
  ): Future[Seq[BookingRecord]] = {
    val startT = new DateTime(startTString)
    val finishT = new DateTime(finishTString)
    getMasterBookings(
      new ObjectId(companyId),
      new ObjectId(masterId),
      Filters.and(
        gte("startT", startT),
        lte("finishT", finishT)))
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
          val timeFilter = Filters.or(
            Filters.and(
              lt("startT", slot.finishT),
              gt("startT", slot.startT)
            ),
            Filters.and(
              lt("finishT", slot.finishT),
              gt("finishT", slot.startT)
            ),
            Filters.and(
              gte("finishT", slot.finishT),
              lte("startT", slot.startT)
            )
          )

          getMasterBookings(
            new ObjectId(params.companyId),
            new ObjectId(slot.masterId),
            timeFilter
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
