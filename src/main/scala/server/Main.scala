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

    s3.get(bucket, bucketKey) match {
      // if there is data, update (optional) & serve it up
      case Some(d) =>
        val cachedDataTimestamp = LocalDate.parse(d.metadata.getUserMetaDataOf("created-at"))
        // data is stale, fetch from quandl and store to s3
        if (today.isAfter(cachedDataTimestamp)) {
          updateThenOutput(s3, bucket, output)
        } else {
          output.write(IOUtils.toByteArray(d.content))
        }
      // if there is no data, update it
      case None => updateThenOutput(s3, bucket, output)
    }
  }

  private def updateThenOutput(s3: S3, bucket: Bucket, output: OutputStream): Unit = {
    lazy val meta = new ObjectMetadata()
    meta.addUserMetadata("created-by", "")
    meta.addUserMetadata("created-at", "")
    val stream = Util.get(treasuryYield).getBytes
    val r: PutObjectResult = s3.put(bucket, bucketKey, stream, meta)
    if (r.isRequesterCharged) {
      output.write(s"[warn] Account was charged for this transaction".getBytes("UTF-8"))
    }
    output.write(stream)
  }
}
