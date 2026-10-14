package com.example.monitoreosilos.data.model

enum class EstadoSilo { NORMAL, ADVERTENCIA, CRITICO }

data class Silo(
    val id: Int,
    val granja: String,
    val nombre: String,
    val capacidadKg: Int,
    val nivelKg: Int,
    val actualizado: String
) {
    // Porcentaje de capacidad disponible
    val porcentaje: Int
        get() = (nivelKg * 100) / capacidadKg

    // Estado calculado según el porcentaje (umbrales supuestos, ver UMBRAL_*)
    val estado: EstadoSilo
        get() = when {
            porcentaje <= UMBRAL_CRITICO -> EstadoSilo.CRITICO
            porcentaje <= UMBRAL_ADVERTENCIA -> EstadoSilo.ADVERTENCIA
            else -> EstadoSilo.NORMAL
        }

    companion object {
        const val UMBRAL_CRITICO = 15
        const val UMBRAL_ADVERTENCIA = 30
    }
}