// Represents a Wind Turbine device in the system
case class WindTurbine(var id: String, var powerOutputKW: Double, var isOperational: Boolean) {

  // Simulate the wind turbine's operational status and energy production
  def simulate(): Unit = {
    isOperational = scala.util.Random.nextDouble() > 0.1  // 90% chance to be operational
    if (isOperational) {
      powerOutputKW = scala.util.Random.nextDouble() * 150 // Generate random power output (0-150 KW)
    } else {
      powerOutputKW = 0  // If not operational, output is 0
    }
  }
}

// Companion object for WindTurbine
object WindTurbine {
  // Initialize a predefined list of wind turbines
  val turbines: List[WindTurbine] = List(
    WindTurbine("WindTurbine-1", 100.0, true),
    WindTurbine("WindTurbine-2", 120.0, true),
    WindTurbine("WindTurbine-3", 130.0, true)
  )
}
