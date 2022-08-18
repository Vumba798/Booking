package server.api

import org.bson.types.ObjectId
import org.joda.time.DateTime
import org.mongodb.scala.FindObservable
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.result.InsertManyResult
import server.database.Dao
import server.database.model.BookingRecord
import server.exceptions.{InsertionException, IntersectionException}
import server.request_params.booking._

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
  )(implicit
      ec: ExecutionContext
  ): Future[Seq[BookingRecord]] = {
    val ids = seq.map(_._id)
    Dao.bookings
      .deleteMany(Filters.in("id", ids))
      .toFutureOption()
      .map {
        case Some(res)
            if res.wasAcknowledged() &&
              res.getDeletedCount == ids.length =>
          seq
        case _ =>
          throw new NoSuchElementException("Failed to delete records by id")
      }
  }

  def getAvailableTime(
      params: GetAvailableTimeParams
  )(implicit
      ec: ExecutionContext
  ): Future[Seq[BookingRecord]] = {

    val startT = new DateTime(params.startT)
    val finishT = new DateTime(params.finishT)
    val companyId = new ObjectId(params.companyId)
    val masterId = new ObjectId(params.masterId)

    getMasterBookings(
      companyId,
      masterId,
      Filters.and(gte("startT", startT), lte("finishT", finishT))
    )
      .toFuture()
      .recoverWith(e => Future.failed(e))
  }

  def createBooking(
      params: CreateBookingParams
  )(implicit ec: ExecutionContext): Future[InsertManyResult] = {

    val startT = new DateTime(params.startT)
    val finishT = new DateTime(params.finishT)
    val companyId = new ObjectId(params.companyId)
    val masterId = new ObjectId(params.companyId)
    val clientPhone = params.clientPhone

    def modifyRecords(seq: Seq[BookingRecord]): Seq[BookingRecord] = {

      def modifyBordering(old: BookingRecord): Seq[BookingRecord] = {
        // modifies slot if we will book only part of it
        if (old.startT == startT && old.finishT == finishT) {
          Seq(old.modify(status = "considering", clientPhone = clientPhone))
        } else if ((old.startT isBefore startT) && (old.finishT == finishT)) {
          val freeBefore = old.modify(finishT = startT)
          val booked = old.modify(
            startT = startT,
            status = "considering",
            clientPhone = clientPhone
          )
          Seq(freeBefore, booked)
        } else if ((old.startT == startT) && (old.finishT isAfter finishT)) {
          val booked = old.modify(
            finishT = finishT,
            status = "considering",
            clientPhone = clientPhone
          )
          val after = old.modify(startT = finishT)
          Seq(booked, after)
        } else { // if matches
          val booked =
            old.modify(status = "considering", clientPhone = clientPhone)
          Seq(booked)
        }
      }

      seq.flatMap {
        case rec: BookingRecord
            if (rec.startT isAfter startT) && (rec.finishT isBefore finishT) =>
          // is inner slot
          Seq(rec.modify(status = "considering", clientPhone = clientPhone))
        case rec: BookingRecord => modifyBordering(rec)
      }
    }

    def checkTime() = {
      getMasterBookings(
        companyId,
        masterId,
        intersectsFilter(startT, finishT)
      )
        .toFuture()
        .map { records: Seq[BookingRecord] =>
          // checks if it covers non-free slots
          records.filter(_.status != "free") match {
            case Seq() => records
            case _ =>
              throw IntersectionException(
                "This request covers unavailable time"
              )
          }
        }
        .map { // checks if time is off schedule
          case Seq() => throw IntersectionException("Time is unavailable")
          case records
              if (records.head.startT isAfter startT) ||
                (records.last.finishT isBefore finishT) =>
            throw IntersectionException("This request covers unavailable time")
          case records =>
            // checks if there are any spaces in "free" timeslots
            val h = records.head
            val t = records.tail
            val endTime = t.foldLeft(h.finishT) { (z, rec) =>
              if (rec.startT == z) rec.finishT
              else
                throw IntersectionException(
                  "This request covers unavailable time"
                )
            }
            assert(endTime == records.last.finishT)
            records
        }
    }

    checkTime()
      .flatMap(deleteRecords)
      .map(modifyRecords)
      .flatMap(Dao.bookings.insertMany(_).headOption())
      .map {
        case None =>
          throw new RuntimeException("Couldn't create booking")
        case Some(res) if !res.wasAcknowledged() =>
          throw InsertionException("Request wasn't acknowledged")
        case Some(res) if res.getInsertedIds.isEmpty =>
          throw InsertionException("No elements was inserted")
        case Some(res) => res
      }
      .recoverWith(e => Future.failed(e))
  }

  def getBookings(
      params: GetBookingsParams
  )(implicit
      ec: ExecutionContext
  ): Future[Seq[BookingRecord]] = {

    val companyId = new ObjectId(params.companyId)
    val startT = new DateTime(params.startT)
    val finishT = new DateTime(params.finishT)
    val clientPhone = params.clientPhone

    Dao.bookings
      .find(
        Filters.and(
          equal("companyId", companyId),
          equal("clientPhone", clientPhone),
          equal("startT", startT),
          equal("finishT", finishT)
        )
      )
      .toFuture()
      .recoverWith(e => Future.failed(e))
  }

  def editBooking(
      params: EditBookingParams
  )(implicit
      ec: ExecutionContext
  ): Future[BookingRecord] = {

    val bookingId = new ObjectId(params.bookingId)
    val status = params.status
    val message = params.message

    Dao.bookings
      .findOneAndUpdate(
        Filters.equal("id", bookingId),
        Document("$set" -> Document("status" -> status, "message" -> message))
      )
      .toFuture()
      .recoverWith(e => Future.failed(e))
  }

  def getCompanyBookings(
      params: GetCompanyBookingsParams
  )(implicit
      ec: ExecutionContext
  ): Future[Seq[BookingRecord]] = {

    val companyId = new ObjectId(params.companyId)
    val startT = new DateTime(params.startT)
    val finishT = new DateTime(params.finishT)
    Dao.bookings
      .find(
        Filters.and(
          equal("companyId", companyId),
          gte("startT", startT),
          lte("finishT", finishT)
        )
      )
      .toFuture()
      .recoverWith(e => Future.failed(e))
  }

  def createWorkingSchedule(
      params: CreateWorkingScheduleParams
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
