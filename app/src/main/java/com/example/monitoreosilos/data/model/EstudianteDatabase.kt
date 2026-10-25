package com.example.monitoreosilos.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Estudiante::class],
    version = 1,
    exportSchema = false
)
abstract class EstudianteDatabase : RoomDatabase() {

    abstract fun estudianteDao(): EstudianteDao

    companion object {
        @Volatile
        private var INSTANCE: EstudianteDatabase? = null

        fun getDatabase(context: Context): EstudianteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EstudianteDatabase::class.java,
                    "estudiantes_db"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
