package server.database

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.bson.codecs.configuration.CodecRegistries.{
  fromProviders,
  fromRegistries
}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import server.database.model._

object Dao {
  private val mapper = JsonMapper
    .builder()
    .addModule(DefaultScalaModule)
    .build()

  private val client: MongoClient = MongoClient()

  private val db: MongoDatabase = client
    .getDatabase("BookingPractice")
    .withCodecRegistry(
      fromRegistries(
        fromProviders(
          classOf[User],
          classOf[BookingRecord],
          classOf[Company]
        ),
        DEFAULT_CODEC_REGISTRY
      )
    )

  val companies: MongoCollection[Company] = db.getCollection("Companies")
  val users: MongoCollection[User] = db.getCollection("Users")
  val bookings: MongoCollection[BookingRecord] = db.getCollection("Bookings")

  def toJson(value: Any): String = mapper.writeValueAsString(value)
}
