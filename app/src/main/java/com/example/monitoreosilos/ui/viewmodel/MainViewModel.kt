package com.example.monitoreosilos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreosilos.data.model.EstadoSilo
import com.example.monitoreosilos.data.model.Silo
import com.example.monitoreosilos.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel : ViewModel() {

    // Datos ficticios / simulados (el caso indica que no se usan datos reales)
    private val _silos = MutableStateFlow(
        listOf(
            Silo(1, "Granja Los Aromos", "Silo A1", 20000, 16400, "09-10-2026 08:15"),
            Silo(2, "Granja Los Aromos", "Silo A2", 20000, 5200, "09-10-2026 08:15"),
            Silo(3, "Granja Los Aromos", "Silo A3", 15000, 1800, "09-10-2026 08:10"),
            Silo(4, "Granja El Sauce", "Silo B1", 25000, 21000, "09-10-2026 07:55"),
            Silo(5, "Granja El Sauce", "Silo B2", 25000, 6900, "09-10-2026 07:55"),
            Silo(6, "Granja Santa Rosa", "Silo C1", 18000, 9900, "09-10-2026 08:00")
        )
    )
    val silos: StateFlow<List<Silo>> = _silos.asStateFlow()

    // Silos que requieren atención (advertencia o crítico), del más bajo al más alto
    val alertas: StateFlow<List<Silo>> = _silos
        .map { lista ->
            lista
                .filter { it.estado != EstadoSilo.NORMAL }
                .sortedBy { it.porcentaje }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _usuario = MutableStateFlow(
        Usuario(
            nombre = "Camila Rojas",
            rol = "Supervisora",
            granja = "Granja Los Aromos"
        )
    )
    val usuario: StateFlow<Usuario> = _usuario.asStateFlow()
}