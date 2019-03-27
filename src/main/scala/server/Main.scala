package server

import scala.io.Source
//import scala.xml.XML

/**
  * Execute a function here by calling: `server.Main::{{handler}}`
  */
class Main {

  import java.io.{InputStream, OutputStream}

  val quandlBase = "https://www.quandl.com/api/v3/datasets/USTREASURY/YIELD/data.csv"

  def greeting(input: InputStream, output: OutputStream): Unit = {
    val result = s"Yo!"
    output.write(result.getBytes("UTF-8"))
  }

  def retrieveHistory(input: InputStream, output: OutputStream): Unit = {
    val in = Source.fromInputStream(input).mkString

    // output.write(in.getBytes("UTF-8"))
  }

}
