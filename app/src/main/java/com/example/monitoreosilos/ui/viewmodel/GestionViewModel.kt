package com.example.monitoreosilos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreosilos.data.remote.BackendClient
import com.example.monitoreosilos.data.remote.IncidenciaRemota
import com.example.monitoreosilos.data.remote.SiloRemoto
import com.example.monitoreosilos.data.repository.GestionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.util.Log
import kotlinx.coroutines.launch

data class GestionUiState(
    val silos: List<SiloRemoto> = emptyList(),
    val incidencias: List<IncidenciaRemota> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)

fun validarSilo(silo: SiloRemoto): String? = when {
    silo.granja.isBlank() -> "La granja es obligatoria"
    silo.nombre.isBlank() -> "El nombre es obligatorio"
    silo.tipoAlimento.isBlank() -> "El tipo de alimento es obligatorio"
    silo.capacidadKg <= 0 -> "La capacidad debe ser mayor a 0"
    silo.nivelKg < 0 || silo.nivelKg > silo.capacidadKg ->
        "El nivel debe estar entre 0 y la capacidad"
    silo.consumoDiarioKg < 0 -> "El consumo diario no puede ser negativo"
    else -> null
}

fun validarDescripcionIncidencia(descripcion: String): String? =
    if (descripcion.trim().length < 5) "La descripción debe tener al menos 5 caracteres" else null

class GestionViewModel(
    private val repositorio: GestionRepository = GestionRepository(
        BackendClient.silosApi,
        BackendClient.incidenciasApi
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(GestionUiState())
    val uiState: StateFlow<GestionUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        ejecutar(mensajeOk = null) { }
    }

    fun guardarSilo(silo: SiloRemoto) {
        val error = validarSilo(silo)
        if (error != null) {
            _uiState.update { it.copy(error = error, mensaje = null) }
            return
        }
        val mensaje = if (silo.id == null) "Silo creado" else "Silo actualizado"
        ejecutar(mensaje) { repositorio.guardarSilo(silo) }
    }

    fun eliminarSilo(id: Long) = ejecutar("Silo eliminado") { repositorio.eliminarSilo(id) }

    fun crearIncidencia(siloId: Long, descripcion: String) {
        val error = validarDescripcionIncidencia(descripcion)
        if (error != null) {
            _uiState.update { it.copy(error = error, mensaje = null) }
            return
        }
        ejecutar("Incidencia registrada") { repositorio.crearIncidencia(siloId, descripcion) }
    }

    fun resolverIncidencia(id: Long) =
        ejecutar("Incidencia resuelta") { repositorio.resolverIncidencia(id) }

    fun eliminarIncidencia(id: Long) =
        ejecutar("Incidencia eliminada") { repositorio.eliminarIncidencia(id) }

    fun limpiarMensajes() {
        _uiState.update { it.copy(error = null, mensaje = null) }
    }

    private fun ejecutar(mensajeOk: String?, operacion: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null, mensaje = null) }
            try {
                operacion()
                val silos = repositorio.listarSilos()
                val incidencias = repositorio.listarIncidencias()
                _uiState.value = GestionUiState(
                    silos = silos,
                    incidencias = incidencias,
                    mensaje = mensajeOk
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Gestion", "Fallo al llamar al servidor", e)
                _uiState.update {
                    it.copy(cargando = false, error = "No fue posible comunicarse con el servidor")
                }
            }
        }
    }
}