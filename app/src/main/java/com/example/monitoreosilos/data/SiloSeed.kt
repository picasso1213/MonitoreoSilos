package com.example.monitoreosilos.data

import com.example.monitoreosilos.data.model.Silo

// Datos ficticios iniciales: 3 granjas con 4 silos cada una
object SiloSeed {
    val silosIniciales = listOf(
        Silo(1, "Granja Los Aromos", "Silo A1", 20000, 16400, "19-10-2026 08:15", "Engorda", 1500),
        Silo(2, "Granja Los Aromos", "Silo A2", 20000, 5200, "19-10-2026 08:15", "Crecimiento", 1400),
        Silo(3, "Granja Los Aromos", "Silo A3", 15000, 1800, "19-10-2026 08:10", "Inicio", 900),
        Silo(4, "Granja Los Aromos", "Silo A4", 20000, 12000, "19-10-2026 08:10", "Postura", 1300),
        Silo(5, "Granja El Sauce", "Silo B1", 25000, 21000, "19-10-2026 07:55", "Engorda", 1800),
        Silo(6, "Granja El Sauce", "Silo B2", 25000, 6900, "19-10-2026 07:55", "Crecimiento", 1700),
        Silo(7, "Granja El Sauce", "Silo B3", 25000, 14000, "19-10-2026 07:50", "Postura", 1600),
        Silo(8, "Granja El Sauce", "Silo B4", 15000, 4200, "19-10-2026 07:50", "Inicio", 1000),
        Silo(9, "Granja Santa Rosa", "Silo C1", 18000, 9900, "19-10-2026 08:00", "Engorda", 1300),
        Silo(10, "Granja Santa Rosa", "Silo C2", 18000, 15300, "19-10-2026 08:00", "Crecimiento", 1200),
        Silo(11, "Granja Santa Rosa", "Silo C3", 18000, 2700, "19-10-2026 07:45", "Postura", 1250),
        Silo(12, "Granja Santa Rosa", "Silo C4", 20000, 9000, "19-10-2026 07:45", "Inicio", 1100)
    )
}