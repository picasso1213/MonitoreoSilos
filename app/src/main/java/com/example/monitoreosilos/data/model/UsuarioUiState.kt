package com.example.monitoreosilos.data.model

data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val rol: String = "",
    val aceptaTerminos: Boolean = false
)

data class UsuarioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val direccion: String? = null,
    val rol: String? = null,
    val terminos: String? = null
)