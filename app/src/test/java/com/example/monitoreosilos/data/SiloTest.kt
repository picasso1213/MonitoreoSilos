package com.example.monitoreosilos.data

import com.example.monitoreosilos.data.model.EstadoSilo
import com.example.monitoreosilos.data.model.Silo
import org.junit.Assert.assertEquals
import org.junit.Test

class SiloTest {

    private fun silo(nivel: Int, capacidad: Int = 10000, consumo: Int = 1000) =
        Silo(1, "Granja", "Silo", capacidad, nivel, "hoy", "Engorda", consumo)

    @Test
    fun porcentaje_calculaElNivelSobreLaCapacidad() = assertEquals(45, silo(4500).porcentaje)

    @Test
    fun porcentaje_conCapacidadCeroEsCero() = assertEquals(0, silo(0, capacidad = 0).porcentaje)

    @Test
    fun estado_normalSobre30() = assertEquals(EstadoSilo.NORMAL, silo(3100).estado)

    @Test
    fun estado_advertenciaEntre20y30() {
        assertEquals(EstadoSilo.ADVERTENCIA, silo(2000).estado)
        assertEquals(EstadoSilo.ADVERTENCIA, silo(3000).estado)
    }

    @Test
    fun estado_criticoBajo20() = assertEquals(EstadoSilo.CRITICO, silo(1999).estado)

    @Test
    fun diasRestantes_dividePorElConsumoDiario() = assertEquals(4, silo(4500, consumo = 1000).diasRestantes)

    @Test
    fun diasRestantes_sinConsumoEsCero() = assertEquals(0, silo(4500, consumo = 0).diasRestantes)
}