package com.example.monitoreosilos.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.monitoreosilos.data.model.Silo

@Entity(tableName = "silos")
data class SiloEntity(
    @PrimaryKey val id: Int,
    val granja: String,
    val nombre: String,
    val capacidadKg: Int,
    val nivelKg: Int,
    val actualizado: String,
    val tipoAlimento: String,
    val consumoDiarioKg: Int
) {
    fun toModel() = Silo(
        id = id,
        granja = granja,
        nombre = nombre,
        capacidadKg = capacidadKg,
        nivelKg = nivelKg,
        actualizado = actualizado,
        tipoAlimento = tipoAlimento,
        consumoDiarioKg = consumoDiarioKg
    )
}

fun Silo.toEntity() = SiloEntity(
    id = id,
    granja = granja,
    nombre = nombre,
    capacidadKg = capacidadKg,
    nivelKg = nivelKg,
    actualizado = actualizado,
    tipoAlimento = tipoAlimento,
    consumoDiarioKg = consumoDiarioKg
)

// Carga o descarga de alimento registrada por un usuario
@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = SiloEntity::class,
            parentColumns = ["id"],
            childColumns = ["siloId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("siloId")]
)
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val siloId: Int,
    val tipo: String,          // "CARGA" o "DESCARGA"
    val cantidadKg: Int,
    val observacion: String,
    val fecha: String
)

// Historial de mediciones del nivel de cada silo
@Entity(
    tableName = "mediciones",
    foreignKeys = [
        ForeignKey(
            entity = SiloEntity::class,
            parentColumns = ["id"],
            childColumns = ["siloId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("siloId")]
)
data class MedicionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val siloId: Int,
    val nivelKg: Int,
    val fecha: String
)

// Incidencias reportadas por operarios o supervisores
@Entity(
    tableName = "incidencias",
    foreignKeys = [
        ForeignKey(
            entity = SiloEntity::class,
            parentColumns = ["id"],
            childColumns = ["siloId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("siloId")]
)
data class IncidenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val siloId: Int,
    val descripcion: String,
    val estado: String,        // "ABIERTA" o "RESUELTA"
    val fecha: String
)