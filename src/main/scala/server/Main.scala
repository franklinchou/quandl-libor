package server

import java.time.{LocalDate, ZoneId}

import awscala._
import awscala.s3._
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.util.IOUtils


/**
  * Execute a function here by calling: `server.Main::{{handler}}`
  */
class Main {

  import java.io.{InputStream, OutputStream}

  final val treasuryYield = "https://www.quandl.com/api/v3/datasets/USTREASURY/YIELD/data.json"
  final val bucketName = "peeriq-dev"
  final val bucketKey = "us-treasury-yield"

  implicit val s3: S3 = S3.at(Region.NorthernVirginia)

  def greeting(input: InputStream, output: OutputStream): Unit = {
    val result = s"Yo!"
    output.write(result.getBytes("UTF-8"))
  }

  /**
    *
    * @param input
    * @param output
    */
  def retrieveHistory(input: InputStream, output: OutputStream): Unit = {
    val bucket = Bucket.apply(bucketName)
    val today = LocalDate.now(ZoneId.of("America/New_York"))

    // store data
    s3
      .get(bucket, bucketKey)
      .foreach { b =>
        val cachedDataTimestamp = LocalDate.parse(b.metadata.getUserMetaDataOf("created-at"))
        if (today.isAfter(cachedDataTimestamp)) {
          // data is stale, fetch from quandl and store to s3
          val meta = new ObjectMetadata()
          meta.addUserMetadata("created-by", "")
          meta.addUserMetadata("created-at", "")
          s3.put(bucket, bucketKey, Util.get(treasuryYield).getBytes, meta)
        }
      }

    s3.get(bucket, bucketKey) match {
      case Some(c) => output.write(IOUtils.toByteArray(c.content))
      case None =>
        val error = s"[error] Failed to fetch data!"
        output.write(error.getBytes("UTF-8"))
    }
  }

}
