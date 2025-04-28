import java.time.LocalDate
import java.time.format.DateTimeFormatter

// EnergyAnalyzer provides functionalities to analyze and filter renewable energy data
object EnergyAnalyzer {

  // Analyze all energy data by calculating basic statistics for each energy type
  def analyzeAll(data: List[EnergyData]): Unit = {
    if (data.isEmpty) {
      println("No data available to analyze.")
      return
    }

    println("\n=== Energy Data Analysis ===")

    analyzeSingle("Solar", data.flatMap(_.solarKW))
    analyzeSingle("Wind", data.flatMap(_.windKW))
    analyzeSingle("Hydro", data.flatMap(_.hydroKW))
  }

  // Analyze a single type of energy data: Solar, Wind, or Hydro
  private def analyzeSingle(name: String, values: List[Double]): Unit = {
    if (values.isEmpty) {
      println(s"Statistics for $name:")
      println(s"No data available for $name.")
      println("--------------------------------------\n")
      return
    }

    val sorted = values.sorted
    val mean = values.sum / values.length
    val median = if (sorted.length % 2 == 0)
      (sorted(sorted.length / 2 - 1) + sorted(sorted.length / 2)) / 2
    else
      sorted(sorted.length / 2)

    val mode = values.groupBy(identity).view.mapValues(_.size).toMap.maxBy(_._2)._1
    val range = sorted.last - sorted.head
    val midrange = (sorted.last + sorted.head) / 2

    println(s"Statistics for $name:")
    println(f"- Mean: $mean%.2f KW")
    println(f"- Median: $median%.2f KW")
    println(f"- Mode: $mode%.2f KW")
    println(f"- Range: $range%.2f KW")
    println(f"- Midrange: $midrange%.2f KW")
    println("-" * 40)
    println(s"🔎 Sorted $name Output Values:")
    println(sorted.map(v => f"$v%.2f").mkString(", "))
    println("-" * 40 + "\n")
  }

  // Filter energy data by a specific day (dd/MM/yyyy)
  def filterByDay(data: List[EnergyData], day: String): List[EnergyData] = {
    data.filter(_.date == day)
  }

  // Filter energy data for the week containing the given day
  def filterByWeek(data: List[EnergyData], day: String): List[EnergyData] = {
    try {
      val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
      val inputDay = LocalDate.parse(day, formatter)
      val weekOfYear = inputDay.getDayOfYear / 7

      data.filter { record =>
        val recordDate = LocalDate.parse(record.date, formatter)
        recordDate.getDayOfYear / 7 == weekOfYear
      }
    } catch {
      case _: Exception =>
        println("Invalid day format for weekly filtering.")
        List()
    }
  }

  // Filter energy data by a specific month and year
  def filterByMonth(data: List[EnergyData], month: Int, year: Int): List[EnergyData] = {
    try {
      val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
      data.filter { record =>
        val recordDate = LocalDate.parse(record.date, formatter)
        recordDate.getMonthValue == month && recordDate.getYear == year
      }
    } catch {
      case _: Exception =>
        println("Invalid month/year filtering attempt.")
        List()
    }
  }

  // Filter energy data by a specific hour of the day (0–23)
  def filterByHour(data: List[EnergyData], hour: Int): List[EnergyData] = {
    if (hour < 0 || hour > 23) {
      println("Invalid hour. Must be between 0 and 23.")
      List()
    } else {
      data.filter(_.hour == hour)
    }
  }
}
