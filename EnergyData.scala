// Group 5
// Student Name: Kai Zhao, Yingjie Song, Zeyue Zhao
case class EnergyData(
    date: String,
    hour: Int,
    solarKW: Option[Double],
    windKW: Option[Double],
    hydroKW: Option[Double]
)
