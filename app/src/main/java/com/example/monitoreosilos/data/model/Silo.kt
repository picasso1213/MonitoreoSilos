package com.example.monitoreosilos.data.model

enum class EstadoSilo { NORMAL, ADVERTENCIA, CRITICO }

data class Silo(
    val id: Int,
    val granja: String,
    val nombre: String,
    val capacidadKg: Int,
    val nivelKg: Int,
    val actualizado: String,
    val tipoAlimento: String = "Engorda",
    val consumoDiarioKg: Int = 1000
) {
    val porcentaje: Int
        get() = if (capacidadKg == 0) 0 else (nivelKg * 100) / capacidadKg

    // Niveles del caso: Normal > 30 %, Advertencia 20–30 %, Crítico < 20 %
    val estado: EstadoSilo
        get() = when {
            porcentaje < UMBRAL_CRITICO -> EstadoSilo.CRITICO
            porcentaje <= UMBRAL_ADVERTENCIA -> EstadoSilo.ADVERTENCIA
            else -> EstadoSilo.NORMAL
        }

    // Días que alcanza el alimento actual al consumo diario estimado
    val diasRestantes: Int
        get() = if (consumoDiarioKg <= 0) 0 else nivelKg / consumoDiarioKg

    companion object {
        const val UMBRAL_CRITICO = 20
        const val UMBRAL_ADVERTENCIA = 30
    }
}