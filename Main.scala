// Group 5
// Student Name: Kai Zhao, Yingjie Song, Zeyue Zhao
import java.io.{File, PrintWriter}
import java.time.{LocalDate, LocalDateTime}
import java.time.format.{DateTimeFormatter, DateTimeParseException}

// Initialize devices (solar panels, wind turbines, hydro plants)
object Main extends App {
  val solarPanels = List(
    SolarPanel("Solar Panel 1", 0.0, true),
    SolarPanel("Solar Panel 2", 0.0, true)
    )
  val windTurbines = List(
    WindTurbine("Wind Turbine 1", 0.0, true),
    WindTurbine("Wind Turbine 2", 0.0, true)
    )
  val hydroPlants = List(
    HydroPlant("Hydro Plant 1", 0.0, true)
    )

  // Save a list of energy production records to CSV file
  def saveEnergyData(energyList: List[EnergyData]): Unit = {
    val writer = new PrintWriter(new File("energy_data_completed.csv"))
    writer.println("Date,Hour,SolarKW,WindKW,HydroKW")
    for (data <- energyList) {
      writer.println(
        s"${data.date},${data.hour}," +
          s"${data.solarKW.map(_.toString).getOrElse("NoData")}," +
          s"${data.windKW.map(_.toString).getOrElse("NoData")}," +
          s"${data.hydroKW.map(_.toString).getOrElse("NoData")}"
      )
    }
    writer.close()
    println("Data saved to energy_data_completed.csv")
  }

  // Read a non-negative double value from user input, retry if invalid
  def readNonNegativeDouble(prompt: String): Option[Double] = {
    println(prompt)
    try {
      val value = scala.io.StdIn.readLine().toDouble
      if (value < 0) {
        println("Negative value is not allowed. Please enter again.")
        readNonNegativeDouble(prompt)
      } else Some(value)
    } catch {
      case _: Exception =>
        println("Invalid number input. Please enter again.")
        readNonNegativeDouble(prompt)
    }
  }

  // Fetch energy production data for a specific date from external API
  def fetchEnergyForDate(dateStr: String): List[EnergyData] = {
    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val inputDate = LocalDate.parse(dateStr, inputFormatter)

    val startTime = s"${inputDate.format(outputFormatter)}T00:00:00Z"
    val endTime = s"${inputDate.format(outputFormatter)}T23:59:59Z"

    println(s"Fetching data for ${inputDate.format(outputFormatter)} ...")
    println("Fetching Solar data...")
    val solarData = APIClient.fetchEnergyQuiet("Solar", APIClient.energyTypes("Solar"), startTime, endTime)
    Thread.sleep(3000)
    println("Fetching Wind data...")
    val windData = APIClient.fetchEnergyQuiet("Wind", APIClient.energyTypes("Wind"), startTime, endTime)
    Thread.sleep(3000)
    println("Fetching Hydro data...")
    val hydroData = APIClient.fetchEnergyQuiet("Hydro", APIClient.energyTypes("Hydro"), startTime, endTime)

    val rawEnergyList = (0 to 23).map { hour =>
      EnergyData(
        dateStr,
        hour,
        solarData.get((dateStr, hour)),
        windData.get((dateStr, hour)),
        hydroData.get((dateStr, hour))
      )
    }.toList

    if (rawEnergyList.forall(d => d.solarKW.isEmpty && d.windKW.isEmpty && d.hydroKW.isEmpty)) {
      println("No available data for the selected date. Please choose another date.")
      List()
    } else {
      DataManager.fillMissingData(rawEnergyList)
    }
  }

  def fetchEnergyForRange(startDate: LocalDate, endDate: LocalDate): List[EnergyData] = {
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startTime = s"${startDate.format(outputFormatter)}T00:00:00Z"
    val endTimeStr = s"${endDate.format(outputFormatter)}T23:59:59Z"

    println(s"Fetching data from ${startDate.format(outputFormatter)} to ${endDate.format(outputFormatter)} ...")
    println("Fetching Solar data...")
    val solarData = APIClient.fetchEnergyRange("Solar", APIClient.energyTypes("Solar"), startTime, endTimeStr)
    println("Fetching Wind data...")
    val windData = APIClient.fetchEnergyRange("Wind", APIClient.energyTypes("Wind"), startTime, endTimeStr)
    println("Fetching Hydro data...")
    val hydroData = APIClient.fetchEnergyRange("Hydro", APIClient.energyTypes("Hydro"), startTime, endTimeStr)

    val datesHours = for {
      date <- Iterator.iterate(startDate)(_ plusDays 1).takeWhile(!_.isAfter(endDate))
      dayStr = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
      hour <- 0 to 23
    } yield (dayStr, hour)

    val rawEnergyList = datesHours.map { case (dayStr, hour) =>
      EnergyData(
        dayStr,
        hour,
        solarData.get((dayStr, hour)),
        windData.get((dayStr, hour)),
        hydroData.get((dayStr, hour))
      )
    }.toList

    if (rawEnergyList.forall(d => d.solarKW.isEmpty && d.windKW.isEmpty && d.hydroKW.isEmpty)) {
      println("No available data for the selected date range.")
      List()
    } else {
      DataManager.fillMissingData(rawEnergyList)
    }
  }

  def askForDate(): String = {
    println("Enter date (dd/MM/yyyy): ")
    val input = scala.io.StdIn.readLine().trim
    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    try {
      LocalDate.parse(input, inputFormatter)
      input
    } catch {
      case _: Exception =>
        println("Invalid date format. Please use dd/MM/yyyy. For example, enter '12/04/2024' for April 12, 2024.")
        askForDate()
    }
  }

  // Display menu options and handle user interactions
  while (true) {
    println(
      """
=== Renewable Energy Plant Monitoring and Management System (REPS) ===
1. Monitor Renewable Energy Production (Solar, Wind, Hydro)
2. Manually Input New Energy Production Records
3. View All Energy Data
4. Filter and Analyze Energy Production (Day/Week/Month/Year)
5. Detect Low Output and Generate System Alerts
6. Exit System
"""
    )

    val choiceInput = scala.io.StdIn.readLine("Select an option: ").trim
    val choice = try {
      choiceInput.toInt
    } catch {
      case _: Exception =>
        println("Invalid input. Please enter a number between 1 and 6.")
        -1
    }

    choice match {
      case 1 =>
  println("\n=== Real-time Device Monitoring ===")

  solarPanels.foreach { p =>
    p.simulate()
    val status = if (p.isOperational) "OK" else "FAULT"
    println(s"${p.id}: ${p.powerOutputKW} kWh (Status: $status)")
  }

  windTurbines.foreach { w =>
    w.simulate()
    val status = if (w.isOperational) "OK" else "FAULT"
    println(s"${w.id}: ${w.powerOutputKW} kWh (Status: $status)")
  }

  hydroPlants.foreach { h =>
    h.simulate()
    val status = if (h.isOperational) "OK" else "FAULT"
    println(s"${h.id}: ${h.powerOutputKW} kWh (Status: $status)")
  }

  println("====================================")

      case 2 =>
        val date = askForDate()
        println("Enter hour (0-23):")
        val hour = try {
          val h = scala.io.StdIn.readLine().toInt
          if (h >= 0 && h <= 23) h else throw new Exception()
        } catch {
          case _: Exception =>
            println("Invalid hour input.")
            -1
        }
        if (hour != -1) {
          val solar = readNonNegativeDouble("Enter Solar KW:")
          val wind = readNonNegativeDouble("Enter Wind KW:")
          val hydro = readNonNegativeDouble("Enter Hydro KW:")
          val newData = EnergyData(date, hour, solar, wind, hydro)
          saveEnergyData(List(newData))
        }

      case 3 =>
        val date = askForDate()
        val energyList = fetchEnergyForDate(date)
        if (energyList.nonEmpty) {
          saveEnergyData(energyList)
          EnergyViewer.showEnergyData(energyList)
        }

      case 4 =>
        println("Filter and Analyze by: 1) Day 2) Week 3) Month 4) Year")
        val filterChoice = scala.io.StdIn.readLine().trim.toInt

        if (filterChoice == 1) {
          val date = askForDate()
          val energyList = fetchEnergyForDate(date)
          if (energyList.nonEmpty) {
            saveEnergyData(energyList)
            EnergyAnalyzer.analyzeAll(energyList)
          }
        } else {
          println("Enter a date within the desired range (dd/MM/yyyy):")
          val inputDateStr = scala.io.StdIn.readLine().trim
          val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
          try {
            val inputDate = LocalDate.parse(inputDateStr, formatter)
            val (start, end) = filterChoice match {
              case 2 => (inputDate.minusDays(inputDate.getDayOfWeek.getValue % 7), inputDate.plusDays(6 - inputDate.getDayOfWeek.getValue % 7))
              case 3 => (inputDate.withDayOfMonth(1), inputDate.withDayOfMonth(inputDate.lengthOfMonth()))
              case 4 => (inputDate.withDayOfYear(1), inputDate.withDayOfYear(inputDate.lengthOfYear()))
            }
            val energyList = fetchEnergyForRange(start, end)
            if (energyList.nonEmpty) {
              saveEnergyData(energyList)
              EnergyAnalyzer.analyzeAll(energyList)
            }
          } catch {
            case _: Exception =>
              println("Invalid date input.")
          }
        }

      case 5 =>
        val date = askForDate()
        val energyList = fetchEnergyForDate(date)
        if (energyList.nonEmpty) {
          saveEnergyData(energyList)
          AlertSystem.checkAlerts(energyList)
        }

      case 6 =>
        println("Exiting the Renewable Energy Plant System... Goodbye!")
        System.exit(0)

      case _ =>
        println("Invalid choice. Please select a valid option.")
    }
  }
}
