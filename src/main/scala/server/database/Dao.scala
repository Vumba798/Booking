package server.database

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.bson.{BsonReader, BsonWriter}
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.codecs.configuration.CodecRegistries.{fromCodecs, fromProviders, fromRegistries}
import org.joda.time.DateTime
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import server.database.model._


class DateTimeCodec extends Codec[DateTime] {
  override def encode(writer: BsonWriter, value: DateTime, ec: EncoderContext) =
    writer.writeDateTime(value.getMillis)

  override def decode(reader: BsonReader, dc: DecoderContext) =
    new DateTime(reader.readDateTime())

  override def getEncoderClass: Class[DateTime] = classOf[DateTime]
}

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
        fromCodecs(new DateTimeCodec),
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
