case class EnergyData(
    date: String,
    hour: Int,
    solarKW: Option[Double],
    windKW: Option[Double],
    hydroKW: Option[Double]
)