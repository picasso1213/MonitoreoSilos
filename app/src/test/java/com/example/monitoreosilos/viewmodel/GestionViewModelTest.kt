package com.example.monitoreosilos.viewmodel

import com.example.monitoreosilos.MainDispatcherRule
import com.example.monitoreosilos.data.remote.IncidenciaRemota
import com.example.monitoreosilos.data.remote.SiloRemoto
import com.example.monitoreosilos.data.repository.GestionRepository
import com.example.monitoreosilos.ui.viewmodel.GestionViewModel
import com.example.monitoreosilos.ui.viewmodel.validarDescripcionIncidencia
import com.example.monitoreosilos.ui.viewmodel.validarSilo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class GestionViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val repo = mockk<GestionRepository>(relaxed = true)
    private val silo = SiloRemoto(1, "Granja", "Silo A", 10000, 4000, "Engorda", 500)
    private val incidencia = IncidenciaRemota(1, 1, "Sensor sin señal")

    private fun crearVm(): GestionViewModel {
        coEvery { repo.listarSilos() } returns listOf(silo)
        coEvery { repo.listarIncidencias() } returns listOf(incidencia)
        return GestionViewModel(repo)
    }

    @Test
    fun alIniciar_cargaSilosEIncidencias() {
        val vm = crearVm()
        assertEquals(listOf(silo), vm.uiState.value.silos)
        assertEquals(listOf(incidencia), vm.uiState.value.incidencias)
        assertFalse(vm.uiState.value.cargando)
    }

    @Test
    fun siElServidorFalla_muestraError() {
        coEvery { repo.listarSilos() } throws RuntimeException("sin servidor")
        val vm = GestionViewModel(repo)
        assertNotNull(vm.uiState.value.error)
        assertFalse(vm.uiState.value.cargando)
    }

    @Test
    fun guardarSiloValido_llamaAlRepositorioYAvisa() {
        val vm = crearVm()
        vm.guardarSilo(silo.copy(id = null))
        coVerify { repo.guardarSilo(silo.copy(id = null)) }
        assertEquals("Silo creado", vm.uiState.value.mensaje)
    }

    @Test
    fun guardarSiloExistente_avisaActualizado() {
        val vm = crearVm()
        vm.guardarSilo(silo)
        assertEquals("Silo actualizado", vm.uiState.value.mensaje)
    }

    @Test
    fun guardarSiloInvalido_noLlamaAlRepositorio() {
        val vm = crearVm()
        vm.guardarSilo(silo.copy(nombre = ""))
        coVerify(exactly = 0) { repo.guardarSilo(any()) }
        assertEquals("El nombre es obligatorio", vm.uiState.value.error)
    }

    @Test
    fun eliminarSilo_llamaAlRepositorio() {
        val vm = crearVm()
        vm.eliminarSilo(1)
        coVerify { repo.eliminarSilo(1) }
        assertEquals("Silo eliminado", vm.uiState.value.mensaje)
    }

    @Test
    fun crearIncidencia_validaLaDescripcion() {
        val vm = crearVm()
        vm.crearIncidencia(1, "ab")
        coVerify(exactly = 0) { repo.crearIncidencia(any(), any()) }
        assertNotNull(vm.uiState.value.error)

        vm.crearIncidencia(1, "Sensor sin señal")
        coVerify { repo.crearIncidencia(1, "Sensor sin señal") }
        assertEquals("Incidencia registrada", vm.uiState.value.mensaje)
    }

    @Test
    fun resolverYEliminarIncidencia_llamanAlRepositorio() {
        val vm = crearVm()
        vm.resolverIncidencia(1)
        coVerify { repo.resolverIncidencia(1) }
        vm.eliminarIncidencia(1)
        coVerify { repo.eliminarIncidencia(1) }
        assertEquals("Incidencia eliminada", vm.uiState.value.mensaje)
    }

    @Test
    fun limpiarMensajes_borraErrorYMensaje() {
        val vm = crearVm()
        vm.guardarSilo(silo.copy(nombre = ""))
        vm.limpiarMensajes()
        assertNull(vm.uiState.value.error)
        assertNull(vm.uiState.value.mensaje)
    }

    @Test
    fun validarSilo_cubreCadaRegla() {
        assertNull(validarSilo(silo))
        assertNotNull(validarSilo(silo.copy(granja = " ")))
        assertNotNull(validarSilo(silo.copy(tipoAlimento = "")))
        assertNotNull(validarSilo(silo.copy(capacidadKg = 0)))
        assertNotNull(validarSilo(silo.copy(nivelKg = -1)))
        assertNotNull(validarSilo(silo.copy(nivelKg = 20000)))
        assertNotNull(validarSilo(silo.copy(consumoDiarioKg = -5)))
    }

    @Test
    fun validarDescripcion_exigeCincoCaracteres() {
        assertNotNull(validarDescripcionIncidencia("  abc "))
        assertNull(validarDescripcionIncidencia("abcde"))
    }
}