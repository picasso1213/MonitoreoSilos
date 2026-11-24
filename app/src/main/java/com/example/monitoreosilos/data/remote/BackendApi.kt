package com.example.monitoreosilos.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class SiloRemoto(
    val id: Long? = null,
    val granja: String,
    val nombre: String,
    val capacidadKg: Int,
    val nivelKg: Int,
    val tipoAlimento: String,
    val consumoDiarioKg: Int
) {
    val porcentaje: Int
        get() = if (capacidadKg == 0) 0 else (nivelKg * 100) / capacidadKg
}

data class IncidenciaRemota(
    val id: Long? = null,
    val siloId: Long,
    val descripcion: String,
    val estado: String = "ABIERTA",
    val fecha: String? = null
)

interface SilosApi {
    @GET("api/silos")
    suspend fun listar(): List<SiloRemoto>

    @POST("api/silos")
    suspend fun crear(@Body silo: SiloRemoto): SiloRemoto

    @PUT("api/silos/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body silo: SiloRemoto): SiloRemoto

    @DELETE("api/silos/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}

interface IncidenciasApi {
    @GET("api/incidencias")
    suspend fun listar(): List<IncidenciaRemota>

    @POST("api/incidencias")
    suspend fun crear(@Body incidencia: IncidenciaRemota): IncidenciaRemota

    @PATCH("api/incidencias/{id}/resolver")
    suspend fun resolver(@Path("id") id: Long): IncidenciaRemota

    @DELETE("api/incidencias/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}