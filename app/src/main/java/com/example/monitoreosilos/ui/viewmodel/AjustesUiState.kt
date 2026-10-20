package com.example.monitoreosilos.ui.viewmodel

data class AjustesUiState(
    val nombreGuardado: String = "",
    val nombreEditable: String = "",
    val errorNombre: String? = null,
    val alertasActivadas: Boolean = true,
    val umbralAlerta: Int = 30
)