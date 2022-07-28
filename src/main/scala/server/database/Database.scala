package server.database

import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}


object Database {
  val client: MongoClient = MongoClient()
  val db: MongoDatabase = client.getDatabase("BookingPractice")

  val companies: MongoCollection[Document] = db.getCollection("Companies")
  val users: MongoCollection[Document] = db.getCollection("Users")
  val bookings: MongoCollection[Document] = db.getCollection("Bookings")
}
