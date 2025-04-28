// DataManager handles reading, filling, and saving energy production data
import java.io.{File, PrintWriter}

object DataManager {

    // Load energy data from a CSV file into a List of EnergyData
    def loadEnergyData(filePath: String = "energy_data_completed.csv"): List[EnergyData] = {
        try {
            val source = scala.io.Source.fromFile(filePath)
            val lines = source.getLines().drop(1).toList // Skip header line
            val data = lines.map { line =>
                val cols = line.split(",")
                EnergyData(
                    date = cols(0),
                    hour = cols(1).toInt,
                    solarKW = if (cols(2) == "NoData") None else Some(cols(2).toDouble),
                    windKW = if (cols(3) == "NoData") None else Some(cols(3).toDouble),
                    hydroKW = if (cols(4) == "NoData") None else Some(cols(4).toDouble)
                )
            }
            source.close()
            data
        } catch {
            case _: Exception =>
                println("Error reading data file. The file may not exist or is corrupted.")
                List()
        }
    }

    // Fill missing energy values randomly to complete the dataset
    def fillMissingData(data: List[EnergyData]): List[EnergyData] = {
        val filled = data.map { record =>
            EnergyData(
                record.date,
                record.hour,
                Some(record.solarKW.getOrElse(scala.util.Random.nextDouble() * 5000)), // Random solar production if missing
                Some(record.windKW.getOrElse(scala.util.Random.nextDouble() * 7000)),  // Random wind production if missing
                Some(record.hydroKW.getOrElse(scala.util.Random.nextDouble() * 3000))  // Random hydro production if missing
            )
        }
        filled
    }

    // Save a list of EnergyData records to a CSV file
    def saveEnergyData(data: List[EnergyData], filePath: String = "energy_data_completed.csv"): Unit = {
        val writer = new PrintWriter(new File(filePath))
        writer.println("Date,Hour,SolarKW,WindKW,HydroKW") // Write header

        for (entry <- data) {
            writer.println(
                s"${entry.date},${entry.hour}," +
                s"${entry.solarKW.map(_.toString).getOrElse("NoData")}," +
                s"${entry.windKW.map(_.toString).getOrElse("NoData")}," +
                s"${entry.hydroKW.map(_.toString).getOrElse("NoData")}"
            )
        }
        writer.close()
        println("Data saved to " + filePath)
    }
}
