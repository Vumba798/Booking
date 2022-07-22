package server.Database

import org.mongodb.scala.{MongoClient, MongoDatabase}


object Database extends App {
  val client: MongoClient = MongoClient()
  val db: MongoDatabase = client.getDatabase("test")
}
