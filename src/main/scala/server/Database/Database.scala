package server.Database

import org.mongodb.scala.{MongoClient, MongoDatabase}


object Database {
  val client: MongoClient = MongoClient()
  val db: MongoDatabase = client.getDatabase("BookingPractice")
}
