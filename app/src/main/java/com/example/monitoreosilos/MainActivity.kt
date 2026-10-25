package com.example.monitoreosilos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.monitoreosilos.ui.navigation.AppNavigation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import com.example.monitoreosilos.data.model.EstudianteDatabase
import com.example.monitoreosilos.data.model.EstudianteRepository
import com.example.monitoreosilos.ui.screen.EstudianteScreen
import com.example.monitoreosilos.ui.viewmodel.EstudianteViewModel

class EstudianteViewModelFactory(
    private val repository: EstudianteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(
                EstudianteViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return EstudianteViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = EstudianteDatabase.getDatabase(this)
        val repository = EstudianteRepository(
            database.estudianteDao()
        )
        val factory = EstudianteViewModelFactory(repository)

        setContent {
            MaterialTheme {
                Surface {
                    AppNavigation()
                }
            }
            val viewModel: EstudianteViewModel = viewModel(
                factory = factory
            )
            EstudianteScreen(viewModel)
        }
    }
}