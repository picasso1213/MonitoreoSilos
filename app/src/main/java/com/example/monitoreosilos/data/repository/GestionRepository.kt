package com.example.monitoreosilos.data.repository

import com.example.monitoreosilos.data.remote.IncidenciaRemota
import com.example.monitoreosilos.data.remote.IncidenciasApi
import com.example.monitoreosilos.data.remote.SiloRemoto
import com.example.monitoreosilos.data.remote.SilosApi

class GestionRepository(
    private val silosApi: SilosApi,
    private val incidenciasApi: IncidenciasApi
) {
    suspend fun listarSilos(): List<SiloRemoto> = silosApi.listar()

    suspend fun guardarSilo(silo: SiloRemoto): SiloRemoto {
        val id = silo.id
        return if (id == null) silosApi.crear(silo) else silosApi.actualizar(id, silo)
    }

    suspend fun eliminarSilo(id: Long) = silosApi.eliminar(id)

    suspend fun listarIncidencias(): List<IncidenciaRemota> = incidenciasApi.listar()

    suspend fun crearIncidencia(siloId: Long, descripcion: String): IncidenciaRemota =
        incidenciasApi.crear(IncidenciaRemota(siloId = siloId, descripcion = descripcion.trim()))

    suspend fun resolverIncidencia(id: Long): IncidenciaRemota = incidenciasApi.resolver(id)

    suspend fun eliminarIncidencia(id: Long) = incidenciasApi.eliminar(id)
}