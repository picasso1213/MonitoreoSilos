package com.example.monitoreosilos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SiloEntity::class,
        MovimientoEntity::class,
        MedicionEntity::class,
        IncidenciaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun siloDao(): SiloDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun medicionDao(): MedicionDao
    abstract fun incidenciaDao(): IncidenciaDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "monitoreo_silos.db"
                ).build().also { instancia = it }
            }
        }
    }
}