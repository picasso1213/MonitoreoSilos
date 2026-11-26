package com.example.monitoreosilos.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.monitoreosilos.data.PreferenciasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AjustesViewModel(
    private val repositorio: PreferenciasRepository
) : ViewModel() {

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer { AjustesViewModel(PreferenciasRepository(context.applicationContext)) }
        }
    }

    // Texto que se está escribiendo (todavía no guardado)
    private val nombreEditable = MutableStateFlow("")
    private val errorNombre = MutableStateFlow<String?>(null)

    init {
        // Carga el nombre guardado una sola vez para mostrarlo en el campo
        viewModelScope.launch {
            nombreEditable.value = repositorio.nombre.first()
        }
    }

    val uiState: StateFlow<AjustesUiState> =
        combine(
            repositorio.nombre,
            repositorio.alertasActivadas,
            repositorio.umbralAlerta,
            nombreEditable,
            errorNombre
        ) { guardado, alertas, umbral, editable, error ->
            AjustesUiState(
                nombreGuardado = guardado,
                nombreEditable = editable,
                errorNombre = error,
                alertasActivadas = alertas,
                umbralAlerta = umbral
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AjustesUiState()
        )

    fun onNombreChange(valor: String) {
        nombreEditable.value = valor
        errorNombre.value = null
    }

    fun guardarNombre() {
        val nombre = nombreEditable.value.trim()
        when {
            nombre.isBlank() -> errorNombre.value = "El nombre no puede estar vacío"
            nombre.length > 30 -> errorNombre.value = "Máximo 30 caracteres"
            else -> {
                errorNombre.value = null
                viewModelScope.launch {
                    repositorio.guardarNombre(nombre)
                }
            }
        }
    }

    fun cambiarAlertasActivadas() {
        val nuevoValor = !uiState.value.alertasActivadas
        viewModelScope.launch {
            repositorio.guardarAlertasActivadas(nuevoValor)
        }
    }

    fun subirUmbral() {
        val nuevo = (uiState.value.umbralAlerta + 5).coerceAtMost(50)
        viewModelScope.launch {
            repositorio.guardarUmbralAlerta(nuevo)
        }
    }

    fun bajarUmbral() {
        val nuevo = (uiState.value.umbralAlerta - 5).coerceAtLeast(5)
        viewModelScope.launch {
            repositorio.guardarUmbralAlerta(nuevo)
        }
    }
}