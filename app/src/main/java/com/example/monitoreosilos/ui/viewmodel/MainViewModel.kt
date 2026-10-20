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
        Silo(1, "Granja Los Aromos", "Silo A1", 20000, 16400, "09-10-2026 08:15"),
        Silo(2, "Granja Los Aromos", "Silo A2", 20000, 5200, "09-10-2026 08:15"),
        Silo(3, "Granja Los Aromos", "Silo A3", 15000, 1800, "09-10-2026 08:10"),
        Silo(4, "Granja El Sauce", "Silo B1", 25000, 21000, "09-10-2026 07:55"),
        Silo(5, "Granja El Sauce", "Silo B2", 25000, 6900, "09-10-2026 07:55"),
        Silo(6, "Granja Santa Rosa", "Silo C1", 18000, 9900, "09-10-2026 08:00")
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