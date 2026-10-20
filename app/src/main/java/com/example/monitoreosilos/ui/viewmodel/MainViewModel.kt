package com.example.monitoreosilos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreosilos.data.model.EstadoSilo
import com.example.monitoreosilos.data.model.Silo
import com.example.monitoreosilos.data.model.Usuario
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {

    // Datos ficticios / simulados (el caso indica que no se usan datos reales)
    private val silosIniciales = listOf(
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

    private val _silos = MutableStateFlow(silosIniciales)
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

    // Evento: un silo acaba de pasar a nivel crítico (la interfaz decide cómo avisar)
    private val _eventosCriticos = MutableSharedFlow<Silo>(extraBufferCapacity = 8)
    val eventosCriticos: SharedFlow<Silo> = _eventosCriticos.asSharedFlow()

    // Simula el consumo de alimento: cada silo baja un 10% de su capacidad
    fun simularConsumo() {
        val anteriores = _silos.value
        val ahora = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

        val nuevos = anteriores.map { silo ->
            val reduccion = (silo.capacidadKg * 0.10).toInt()
            silo.copy(
                nivelKg = (silo.nivelKg - reduccion).coerceAtLeast(0),
                actualizado = ahora
            )
        }
        _silos.value = nuevos

        nuevos.zip(anteriores).forEach { (nuevo, antes) ->
            if (nuevo.estado == EstadoSilo.CRITICO && antes.estado != EstadoSilo.CRITICO) {
                _eventosCriticos.tryEmit(nuevo)
            }
        }
    }

    fun restablecerNiveles() {
        _silos.value = silosIniciales
    }
}