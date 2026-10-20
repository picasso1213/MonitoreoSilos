package com.example.monitoreosilos.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "preferencias_usuario"
)

class PreferenciasRepository(
    private val context: Context
) {
    companion object {
        private val NOMBRE = stringPreferencesKey("nombre_usuario")
        private val ALERTAS = booleanPreferencesKey("alertas_activadas")
        private val UMBRAL = intPreferencesKey("umbral_alerta")
    }

    val nombre = context.dataStore.data.map { preferencias ->
        preferencias[NOMBRE] ?: ""
    }

    val alertasActivadas = context.dataStore.data.map { preferencias ->
        preferencias[ALERTAS] ?: true
    }

    val umbralAlerta = context.dataStore.data.map { preferencias ->
        preferencias[UMBRAL] ?: 30
    }

    suspend fun guardarNombre(nombre: String) {
        context.dataStore.edit { preferencias ->
            preferencias[NOMBRE] = nombre
        }
    }

    suspend fun guardarAlertasActivadas(activadas: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[ALERTAS] = activadas
        }
    }

    suspend fun guardarUmbralAlerta(umbral: Int) {
        context.dataStore.edit { preferencias ->
            preferencias[UMBRAL] = umbral
        }
    }
}