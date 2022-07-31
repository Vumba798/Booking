package server.database

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import server.database.model._


object DAO {
  private val mapper = JsonMapper.builder()
    .addModule(DefaultScalaModule)
    .build()

  private val client: MongoClient = MongoClient()

  // todo add provider classOf[Company]
  private val db: MongoDatabase =
  client.getDatabase("BookingPractice").withCodecRegistry(
    fromRegistries(fromProviders(classOf[User], classOf[BookingRecord]),
    DEFAULT_CODEC_REGISTRY))

  // todo change companies generic type from "Document" to "Company"
  val companies: MongoCollection[Document] = db.getCollection("Companies")
  val users: MongoCollection[User] = db.getCollection("Users")
  val bookings: MongoCollection[BookingRecord] = db.getCollection("Bookings")

  def toJson(value: Any): String = mapper.writeValueAsString(value)
}
