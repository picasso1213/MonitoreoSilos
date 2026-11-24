package com.example.monitoreosilos.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BackendClient {
    // 10.0.2.2 = el PC desde el emulador. En celular físico, usar la IP del PC.
    private const val HOST = "10.0.2.2"
    private const val URL_SILOS = "http://$HOST:8081/"
    private const val URL_INCIDENCIAS = "http://$HOST:8082/"

    private fun <T> crear(baseUrl: String, servicio: Class<T>): T =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(servicio)

    val silosApi: SilosApi by lazy { crear(URL_SILOS, SilosApi::class.java) }
    val incidenciasApi: IncidenciasApi by lazy { crear(URL_INCIDENCIAS, IncidenciasApi::class.java) }
}