package server.database.model

import org.mongodb.scala.bson.ObjectId


case class BookingRecord(id: ObjectId,
                         companyId: ObjectId,
                         price: Double,
                         info: String,
                         startT: String,
                         finishT: String,
                         clientTel: String,
                         masterId: ObjectId,
                         status: String) extends DbElement

object BookingRecord {

  // apply() used for constructing "id: ObjectId" field
  def apply(companyId: ObjectId,
            price: Double,
            info: String,
            startT: String,
            finishT: String,
            clientTel: String,
            masterId: ObjectId,
            status: String): BookingRecord =
    BookingRecord(new ObjectId, companyId, price, info, startT, finishT, clientTel, masterId, status)
}