package com.example.monitoreosilos.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.monitoreosilos.data.model.UsuarioErrores
import com.example.monitoreosilos.data.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel : ViewModel() {

    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado.asStateFlow()

    private val _errores = MutableStateFlow(UsuarioErrores())
    val errores: StateFlow<UsuarioErrores> = _errores.asStateFlow()

    private val regexCorreo =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    // Cada cambio actualiza el estado y limpia el error de ese campo
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor) }
        _errores.update { it.copy(nombre = null) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor) }
        _errores.update { it.copy(correo = null) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor) }
        _errores.update { it.copy(clave = null) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor) }
        _errores.update { it.copy(direccion = null) }
    }

    fun onRolChange(valor: String) {
        _estado.update { it.copy(rol = valor) }
        _errores.update { it.copy(rol = null) }
    }

    fun onTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
        _errores.update { it.copy(terminos = null) }
    }

    // Retorna true si el formulario es válido; si no, actualiza los errores
    fun validarFormulario(): Boolean {
        val e = _estado.value

        val errores = UsuarioErrores(
            nombre = if (e.nombre.isBlank()) "El nombre es obligatorio" else null,
            correo = when {
                e.correo.isBlank() -> "El correo es obligatorio"
                !regexCorreo.matches(e.correo) -> "Correo no válido"
                else -> null
            },
            clave = when {
                e.clave.length < 8 -> "Mínimo 8 caracteres"
                e.clave.none { it.isDigit() } -> "Debe incluir al menos un número"
                else -> null
            },
            direccion = if (e.direccion.isBlank()) "La dirección es obligatoria" else null,
            rol = if (e.rol.isBlank()) "Selecciona un rol" else null,
            terminos = if (!e.aceptaTerminos) "Debes aceptar los términos" else null
        )

        _errores.value = errores

        return listOf(
            errores.nombre, errores.correo, errores.clave,
            errores.direccion, errores.rol, errores.terminos
        ).all { it == null }
    }
}