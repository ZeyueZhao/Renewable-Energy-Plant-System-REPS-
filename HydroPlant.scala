// Group 5
// Student Name: Kai Zhao, Yingjie Song, Zeyue Zhao
case class HydroPlant(var id: String, var powerOutputKW: Double, var isOperational: Boolean) {
  
  def simulate(): Unit = {
    isOperational = scala.util.Random.nextDouble() > 0.1
    if (isOperational) {
      powerOutputKW = scala.util.Random.nextDouble() * 200
    } else {
      powerOutputKW = 0
    }
  }
}

object HydroPlant {
  val plants: List[HydroPlant] = List(
    HydroPlant("HydroPlant-1", 150.0, true),
    HydroPlant("HydroPlant-2", 180.0, true)
  )
}
