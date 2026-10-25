package com.example.monitoreosilos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreosilos.data.model.Estudiante
import com.example.monitoreosilos.data.model.EstudianteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EstudianteViewModel(
    private val repository: EstudianteRepository
) : ViewModel() {

    val estudiantes = repository.obtenerTodos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertar(
        nombre: String,
        correo: String,
        carrera: String
    ) {
        viewModelScope.launch {
            repository.insertar(
                Estudiante(
                    nombre = nombre.trim(),
                    correo = correo.trim(),
                    carrera = carrera.trim()
                )
            )
        }
    }

    fun actualizar(estudiante: Estudiante) {
        viewModelScope.launch {
            repository.actualizar(estudiante)
        }
    }

    fun eliminar(estudiante: Estudiante) {
        viewModelScope.launch {
            repository.eliminar(estudiante)
        }
    }
}
