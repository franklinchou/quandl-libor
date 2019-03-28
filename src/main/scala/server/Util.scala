package server

import java.net.{HttpURLConnection, URL}

object Util {

  /**
    * Perform a GET request
    *
    * @param url
    * @param connectionTimeout
    * @param readTimeout
    * @param requestMethod
    * @return
    */
  def get(url: String,
          connectionTimeout: Int = 5000,
          readTimeout: Int = 5000,
          requestMethod: String = "GET"): String = {

    val connection = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectionTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)

    val inputStream = connection.getInputStream
    val content = io.Source.fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close()
    content
  }

}
