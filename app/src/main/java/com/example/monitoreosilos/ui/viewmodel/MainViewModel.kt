package com.example.monitoreosilos.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreosilos.data.SilosRepository
import com.example.monitoreosilos.data.local.AppDatabase
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
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Los silos viven en la base de datos local (Room); el repositorio los expone como Flow
    val repositorio = SilosRepository(AppDatabase.obtener(application))

    val silos: StateFlow<List<Silo>> = repositorio.silos
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Silos que requieren atención (advertencia o crítico), del más bajo al más alto
    val alertas: StateFlow<List<Silo>> = silos
        .map { lista ->
            lista
                .filter { it.estado != EstadoSilo.NORMAL }
                .sortedBy { it.porcentaje }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _usuario = MutableStateFlow(
        Usuario(
            nombre = "Camila Rojas",
            rol = "Supervisora",
            granja = "Granja Los Aromos"
        )
    )
    val usuario: StateFlow<Usuario> = _usuario.asStateFlow()

    // Evento: un silo acaba de pasar a nivel crítico (la interfaz decide cómo avisar)
    private val _eventosCriticos = MutableSharedFlow<Silo>(extraBufferCapacity = 16)
    val eventosCriticos: SharedFlow<Silo> = _eventosCriticos.asSharedFlow()

    init {
        viewModelScope.launch { repositorio.sembrarSiHaceFalta() }
    }

    fun simularConsumo() {
        viewModelScope.launch {
            val antes = repositorio.obtenerSilos()
            repositorio.aplicarConsumo()
            val despues = repositorio.obtenerSilos()
            despues.zip(antes).forEach { (nuevo, previo) ->
                if (nuevo.estado == EstadoSilo.CRITICO && previo.estado != EstadoSilo.CRITICO) {
                    _eventosCriticos.tryEmit(nuevo)
                }
            }
        }
    }

    fun restablecerNiveles() {
        viewModelScope.launch { repositorio.restablecer() }
    }
}