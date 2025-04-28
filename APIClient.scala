// APIClient is responsible for fetching energy production data from Fingrid API
import java.net.{HttpURLConnection, URL}
import scala.collection.mutable
import scala.io.Source

object APIClient {

  // API key for authentication
  val apiKey = "1c6e6ba95ef24557afcdc7c91d9effea"
  
  // Base URL for Fingrid API
  val baseUrl = "https://data.fingrid.fi/api/datasets"

  // Dataset IDs for different energy sources
  val energyTypes = Map(
    "Wind" -> 75,
    "Solar" -> 248,
    "Hydro" -> 191
  )

  // Fetch energy data with optional time range, prints fetching info
  def fetchEnergy(energyName: String, datasetId: Int, startTime: String = "", endTime: String = ""): Map[(String, Int), Double] = {
    try {
      val urlString = if (startTime != "" && endTime != "") {
        s"$baseUrl/$datasetId/data?startTime=$startTime&endTime=$endTime"
      } else {
        s"$baseUrl/$datasetId/data"
      }

      val url = new URL(urlString)
      val connection = url.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      connection.setRequestProperty("x-api-key", apiKey)
      connection.setConnectTimeout(5000)
      connection.setReadTimeout(5000)

      val inputStream = connection.getInputStream
      val response = Source.fromInputStream(inputStream).mkString
      inputStream.close()

      println(s"Successfully fetched $energyName data.")
      println("Fingrid raw response (first 500 characters): " + response.take(500))

      parseResponse(response)
    } catch {
      case e: Exception =>
        println(s"Error fetching $energyName data: " + e.getMessage)
        Map()
    }
  }

  // Fetch energy data quietly without printing debug output
  def fetchEnergyQuiet(energyName: String, datasetId: Int, startTime: String, endTime: String): Map[(String, Int), Double] = {
    try {
      val urlString = s"$baseUrl/$datasetId/data?startTime=$startTime&endTime=$endTime"
      val url = new URL(urlString)
      val connection = url.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      connection.setRequestProperty("x-api-key", apiKey)
      connection.setConnectTimeout(5000)
      connection.setReadTimeout(5000)

      val inputStream = connection.getInputStream
      val response = Source.fromInputStream(inputStream).mkString
      inputStream.close()

      parseResponse(response)
    } catch {
      case e: Exception =>
        println(s"Error fetching $energyName data: ${e.getMessage}")
        Map()
    }
  }

  // Fetch a range of energy data (simply calls fetchEnergyQuiet)
  def fetchEnergyRange(energyName: String, datasetId: Int, startTime: String, endTime: String): Map[(String, Int), Double] = {
    fetchEnergyQuiet(energyName, datasetId, startTime, endTime)
  }

  // Parse API JSON response and extract energy production values
  def parseResponse(response: String): Map[(String, Int), Double] = {
    val dataArrayPattern = """"data"\s*:\s*\[(.*)\]""".r
    val recordPattern = """\{[^}]*"startTime"\s*:\s*"([^"]+)",[^}]*"value"\s*:\s*([\d.]+)[^}]*\}""".r
    val tempMap = mutable.Map[(String, Int), List[Double]]()

    val dataContentOpt = dataArrayPattern.findFirstMatchIn(response).map(_.group(1))

    dataContentOpt match {
      case Some(dataContent) =>
        // Extract startTime and value from each record
        for (m <- recordPattern.findAllMatchIn(dataContent)) {
          val startTime = m.group(1)
          val value = m.group(2).toDouble

          val day = startTime.substring(8,10) + "/" + startTime.substring(5,7) + "/" + startTime.substring(0,4)
          val hour = startTime.substring(11,13).toInt

          val key = (day, hour)
          val existing = tempMap.getOrElse(key, List())
          tempMap(key) = existing :+ value
        }
      case None =>
        println("Error: Cannot find data array in response.")
    }

    // Calculate average if multiple values for the same (day, hour)
    tempMap.view.mapValues(values => values.sum / values.size).toMap
  }
}
