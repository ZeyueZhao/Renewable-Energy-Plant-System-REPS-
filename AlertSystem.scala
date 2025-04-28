// System to check and alert for low renewable energy production
object AlertSystem {

  // Thresholds for different energy sources (in KW)
  val solarThreshold = 50.0
  val windThreshold = 1000.0
  val hydroThreshold = 1500.0

  // Check the list of energy data and print alerts if production falls below thresholds
  def checkAlerts(energyList: List[EnergyData]): Unit = {
    println("\n=== Energy Alerts ===")

    for (data <- energyList) {
      // Check solar output
      data.solarKW match {
        case Some(value) if value < solarThreshold =>
          println(s"Low Solar Power Alert: ${data.date} Hour ${data.hour} -> ${value} KW")
        case _ =>
      }

      // Check wind output
      data.windKW match {
        case Some(value) if value < windThreshold =>
          println(s"Low Wind Power Alert: ${data.date} Hour ${data.hour} -> ${value} KW")
        case _ =>
      }

      // Check hydro output
      data.hydroKW match {
        case Some(value) if value < hydroThreshold =>
          println(s"Low Hydro Power Alert: ${data.date} Hour ${data.hour} -> ${value} KW")
        case _ =>
      }
    }

    println("=== End of Alerts ===")
  }
}
