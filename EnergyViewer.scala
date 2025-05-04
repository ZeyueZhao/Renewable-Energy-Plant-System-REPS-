// EnergyViewer is responsible for displaying energy production and storage data
object EnergyViewer {

  // Displays a detailed table of all energy records, sorted by date and hour
  def showEnergyData(energyList: List[EnergyData]): Unit = {
    println("\n=== Renewable Energy Production and Storage Overview ===")
    println(f"|    Date    | Hour | Solar Output (KW) | Wind Output (KW) | Hydro Output (KW) |")
    println("|------------|------|-------------------|------------------|-------------------|")

    var totalSolar = 0.0
    var totalWind = 0.0
    var totalHydro = 0.0

    // Sort data chronologically by date and hour before displaying
    val sortedList = energyList.sortBy(data => (data.date, data.hour))

    // Display each energy record
    for (data <- sortedList) {
      val solarStr = data.solarKW.map(v => f"$v%.2f").getOrElse("NoData")
      val windStr = data.windKW.map(v => f"$v%.2f").getOrElse("NoData")
      val hydroStr = data.hydroKW.map(v => f"$v%.2f").getOrElse("NoData")

      println(f"| ${data.date}%-10s | ${data.hour}%4d | ${solarStr}%17s | ${windStr}%16s | ${hydroStr}%17s |")

      // Accumulate totals for final summary
      totalSolar += data.solarKW.getOrElse(0.0)
      totalWind += data.windKW.getOrElse(0.0)
      totalHydro += data.hydroKW.getOrElse(0.0)
    }

    println("-" * 79)
    println("\n Summary Statistics:")
    println(f"- Total Solar Energy Produced: $totalSolar%.2f kWh")
    println(f"- Total Wind Energy Produced:  $totalWind%.2f kWh")
    println(f"- Total Hydro Energy Produced: $totalHydro%.2f kWh")
  }

}
