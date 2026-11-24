package com.example.monitoreosilos.data

import com.example.monitoreosilos.data.remote.IncidenciaRemota
import com.example.monitoreosilos.data.remote.IncidenciasApi
import com.example.monitoreosilos.data.remote.SiloRemoto
import com.example.monitoreosilos.data.remote.SilosApi
import com.example.monitoreosilos.data.repository.GestionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GestionRepositoryTest {

    private val silosApi = mockk<SilosApi>(relaxed = true)
    private val incidenciasApi = mockk<IncidenciasApi>(relaxed = true)
    private val repo = GestionRepository(silosApi, incidenciasApi)

    private val nuevo = SiloRemoto(null, "G", "S", 1000, 500, "Engorda", 100)

    @Test
    fun guardarSilo_sinId_hacePost() = runTest {
        coEvery { silosApi.crear(nuevo) } returns nuevo.copy(id = 5)
        assertEquals(5L, repo.guardarSilo(nuevo).id)
        coVerify(exactly = 1) { silosApi.crear(nuevo) }
    }

    @Test
    fun guardarSilo_conId_hacePut() = runTest {
        val existente = nuevo.copy(id = 3)
        coEvery { silosApi.actualizar(3, existente) } returns existente
        repo.guardarSilo(existente)
        coVerify(exactly = 1) { silosApi.actualizar(3, existente) }
        coVerify(exactly = 0) { silosApi.crear(any()) }
    }

    @Test
    fun listarYEliminarSilos_delegaEnLaApi() = runTest {
        coEvery { silosApi.listar() } returns listOf(nuevo)
        assertEquals(listOf(nuevo), repo.listarSilos())
        repo.eliminarSilo(9)
        coVerify { silosApi.eliminar(9) }
    }

    @Test
    fun crearIncidencia_recortaLaDescripcion() = runTest {
        val esperado = IncidenciaRemota(siloId = 2, descripcion = "Sensor roto")
        coEvery { incidenciasApi.crear(esperado) } returns esperado.copy(id = 1)
        assertEquals(1L, repo.crearIncidencia(2, "  Sensor roto  ").id)
    }

    @Test
    fun resolverListarYEliminarIncidencias_delegaEnLaApi() = runTest {
        val inc = IncidenciaRemota(1, 2, "Falla", "RESUELTA")
        coEvery { incidenciasApi.resolver(1) } returns inc
        coEvery { incidenciasApi.listar() } returns listOf(inc)
        assertEquals("RESUELTA", repo.resolverIncidencia(1).estado)
        assertEquals(listOf(inc), repo.listarIncidencias())
        repo.eliminarIncidencia(1)
        coVerify { incidenciasApi.eliminar(1) }
    }
}