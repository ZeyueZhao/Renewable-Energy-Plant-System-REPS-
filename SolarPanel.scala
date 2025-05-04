// Group 5
// Student Name: Kai Zhao, Yingjie Song, Zeyue Zhao
// Represents a Solar Panel device in the system
case class SolarPanel(var id: String, var powerOutputKW: Double, var isOperational: Boolean) {

  // Simulate the solar panel's operational status and energy production
  def simulate(): Unit = {
    isOperational = scala.util.Random.nextDouble() > 0.1  // 90% chance to stay operational
    if (isOperational) {
      powerOutputKW = scala.util.Random.nextDouble() * 100 // Generate random power output (0-100 KW)
    } else {
      powerOutputKW = 0  // If not operational, output is 0
    }
  }
}

// Companion object for SolarPanel
object SolarPanel {
  // Initialize a predefined list of solar panels
  val panels: List[SolarPanel] = List(
    SolarPanel("SolarPanel-1", 50.0, true),
    SolarPanel("SolarPanel-2", 60.0, true),
    SolarPanel("SolarPanel-3", 70.0, true)
  )
}
