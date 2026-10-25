package com.example.monitoreosilos.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EstudianteDao {

    @Insert
    suspend fun insertar(estudiante: Estudiante)

    @Query("SELECT * FROM estudiantes ORDER BY nombre")
    fun obtenerTodos(): Flow<List<Estudiante>>

    @Update
    suspend fun actualizar(estudiante: Estudiante)

    @Delete
    suspend fun eliminar(estudiante: Estudiante)
}
