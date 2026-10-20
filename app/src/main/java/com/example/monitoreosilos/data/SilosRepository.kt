package com.example.monitoreosilos.data

import androidx.room.withTransaction
import com.example.monitoreosilos.data.local.AppDatabase
import com.example.monitoreosilos.data.local.IncidenciaEntity
import com.example.monitoreosilos.data.local.MedicionEntity
import com.example.monitoreosilos.data.local.MovimientoEntity
import com.example.monitoreosilos.data.local.toEntity
import com.example.monitoreosilos.data.model.Silo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TipoMovimiento { CARGA, DESCARGA }

class SilosRepository(private val db: AppDatabase) {

    private val siloDao = db.siloDao()
    private val movimientoDao = db.movimientoDao()
    private val medicionDao = db.medicionDao()
    private val incidenciaDao = db.incidenciaDao()

    val silos: Flow<List<Silo>> = siloDao.observarTodos().map { lista ->
        lista.map { it.toModel() }
    }
    val movimientos: Flow<List<MovimientoEntity>> = movimientoDao.observarTodos()
    val incidencias: Flow<List<IncidenciaEntity>> = incidenciaDao.observarTodas()

    fun movimientosDeSilo(siloId: Int): Flow<List<MovimientoEntity>> =
        movimientoDao.observarPorSilo(siloId)

    fun historialDeSilo(siloId: Int): Flow<List<MedicionEntity>> =
        medicionDao.observarPorSilo(siloId)

    suspend fun obtenerSilos(): List<Silo> = siloDao.obtenerTodos().map { it.toModel() }

    private fun ahora(): String =
        SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

    // Carga los datos ficticios solo la primera vez que se abre la app
    suspend fun sembrarSiHaceFalta() {
        if (siloDao.contar() > 0) return
        db.withTransaction {
            siloDao.guardarTodos(SiloSeed.silosIniciales.map { it.toEntity() })
            medicionDao.insertarTodas(historialInicial())
        }
    }

    // Cinco días de mediciones ficticias hacia atrás, según el consumo diario
    private fun historialInicial(): List<MedicionEntity> {
        val formato = SimpleDateFormat("dd-MM-yyyy 08:00", Locale.getDefault())
        val mediciones = mutableListOf<MedicionEntity>()
        SiloSeed.silosIniciales.forEach { silo ->
            for (diasAtras in 5 downTo 1) {
                val calendario = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -diasAtras) }
                val nivel = (silo.nivelKg + silo.consumoDiarioKg * diasAtras)
                    .coerceAtMost(silo.capacidadKg)
                mediciones += MedicionEntity(
                    siloId = silo.id,
                    nivelKg = nivel,
                    fecha = formato.format(calendario.time)
                )
            }
            mediciones += MedicionEntity(siloId = silo.id, nivelKg = silo.nivelKg, fecha = silo.actualizado)
        }
        return mediciones
    }

    // Simula el consumo: cada silo baja un porcentaje de su capacidad
    suspend fun aplicarConsumo(porcentajeCapacidad: Int = 10) {
        db.withTransaction {
            val fecha = ahora()
            siloDao.obtenerTodos().forEach { silo ->
                val nuevoNivel = (silo.nivelKg - silo.capacidadKg * porcentajeCapacidad / 100)
                    .coerceAtLeast(0)
                siloDao.guardar(silo.copy(nivelKg = nuevoNivel, actualizado = fecha))
                medicionDao.insertar(MedicionEntity(siloId = silo.id, nivelKg = nuevoNivel, fecha = fecha))
            }
        }
    }

    suspend fun restablecer() {
        db.withTransaction {
            val fecha = ahora()
            val iniciales = SiloSeed.silosIniciales.map { it.copy(actualizado = fecha).toEntity() }
            siloDao.guardarTodos(iniciales)
            iniciales.forEach {
                medicionDao.insertar(MedicionEntity(siloId = it.id, nivelKg = it.nivelKg, fecha = fecha))
            }
        }
    }

    // Registra una carga o descarga y actualiza el nivel del silo
    suspend fun registrarMovimiento(
        siloId: Int,
        tipo: TipoMovimiento,
        cantidadKg: Int,
        observacion: String
    ) {
        db.withTransaction {
            val silo = siloDao.obtener(siloId) ?: return@withTransaction
            val fecha = ahora()
            val nuevoNivel = when (tipo) {
                TipoMovimiento.CARGA -> (silo.nivelKg + cantidadKg).coerceAtMost(silo.capacidadKg)
                TipoMovimiento.DESCARGA -> (silo.nivelKg - cantidadKg).coerceAtLeast(0)
            }
            siloDao.guardar(silo.copy(nivelKg = nuevoNivel, actualizado = fecha))
            movimientoDao.insertar(
                MovimientoEntity(
                    siloId = siloId,
                    tipo = tipo.name,
                    cantidadKg = cantidadKg,
                    observacion = observacion,
                    fecha = fecha
                )
            )
            medicionDao.insertar(MedicionEntity(siloId = siloId, nivelKg = nuevoNivel, fecha = fecha))
        }
    }

    suspend fun registrarIncidencia(siloId: Int, descripcion: String) {
        incidenciaDao.insertar(
            IncidenciaEntity(
                siloId = siloId,
                descripcion = descripcion,
                estado = "ABIERTA",
                fecha = ahora()
            )
        )
    }

    suspend fun resolverIncidencia(id: Int) = incidenciaDao.resolver(id)
}