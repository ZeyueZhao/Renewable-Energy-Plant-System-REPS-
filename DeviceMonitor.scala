// DeviceMonitor is responsible for monitoring real-time status and production of all renewable devices
object DeviceMonitor {

  // Simulate and display the current status of all solar panels, wind turbines, and hydro plants
  def monitorDevices(): Unit = {
    println("\n=== Monitoring Renewable Energy Production ===")
    
    // Simulate and display solar panel status
    SolarPanel.panels.foreach { panel =>
      panel.simulate()
      if (panel.isOperational)
        println(f"${panel.id}%-18s ${panel.powerOutputKW}%.2f KW (Status: OK)")
      else
        println(f"${panel.id}%-18s Fault")
    }

    // Simulate and display wind turbine status
    WindTurbine.turbines.foreach { turbine =>
      turbine.simulate()
      if (turbine.isOperational)
        println(f"${turbine.id}%-18s ${turbine.powerOutputKW}%.2f KW (Status: OK)")
      else
        println(f"${turbine.id}%-18s Fault")
    }

    // Simulate and display hydro plant status
    HydroPlant.plants.foreach { plant =>
      plant.simulate()
      if (plant.isOperational)
        println(f"${plant.id}%-18s ${plant.powerOutputKW}%.2f KW (Status: OK)")
      else
        println(f"${plant.id}%-18s Fault")
    }
  }
}
