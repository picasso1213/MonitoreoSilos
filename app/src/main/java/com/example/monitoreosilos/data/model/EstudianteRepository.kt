package com.example.monitoreosilos.data.model

import kotlinx.coroutines.flow.Flow

class EstudianteRepository(
    private val dao: EstudianteDao
) {

    fun obtenerTodos(): Flow<List<Estudiante>> {
        return dao.obtenerTodos()
    }

    suspend fun insertar(estudiante: Estudiante) {
        dao.insertar(estudiante)
    }

    suspend fun actualizar(estudiante: Estudiante) {
        dao.actualizar(estudiante)
    }

    suspend fun eliminar(estudiante: Estudiante) {
        dao.eliminar(estudiante)
    }
}
