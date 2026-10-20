package com.example.monitoreosilos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SiloDao {
    @Query("SELECT * FROM silos ORDER BY id")
    fun observarTodos(): Flow<List<SiloEntity>>

    @Query("SELECT * FROM silos ORDER BY id")
    suspend fun obtenerTodos(): List<SiloEntity>

    @Query("SELECT * FROM silos WHERE id = :id")
    suspend fun obtener(id: Int): SiloEntity?

    @Query("SELECT COUNT(*) FROM silos")
    suspend fun contar(): Int

    @Upsert
    suspend fun guardarTodos(silos: List<SiloEntity>)

    @Upsert
    suspend fun guardar(silo: SiloEntity)
}

@Dao
interface MovimientoDao {
    @Insert
    suspend fun insertar(movimiento: MovimientoEntity)

    @Query("SELECT * FROM movimientos ORDER BY id DESC")
    fun observarTodos(): Flow<List<MovimientoEntity>>

    @Query("SELECT * FROM movimientos WHERE siloId = :siloId ORDER BY id DESC")
    fun observarPorSilo(siloId: Int): Flow<List<MovimientoEntity>>
}

@Dao
interface MedicionDao {
    @Insert
    suspend fun insertar(medicion: MedicionEntity)

    @Insert
    suspend fun insertarTodas(mediciones: List<MedicionEntity>)

    @Query("SELECT * FROM mediciones WHERE siloId = :siloId ORDER BY id DESC LIMIT 30")
    fun observarPorSilo(siloId: Int): Flow<List<MedicionEntity>>
}

@Dao
interface IncidenciaDao {
    @Insert
    suspend fun insertar(incidencia: IncidenciaEntity)

    @Query("SELECT * FROM incidencias ORDER BY id DESC")
    fun observarTodas(): Flow<List<IncidenciaEntity>>

    @Query("UPDATE incidencias SET estado = 'RESUELTA' WHERE id = :id")
    suspend fun resolver(id: Int)
}