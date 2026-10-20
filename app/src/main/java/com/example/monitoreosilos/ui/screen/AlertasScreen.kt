package com.example.monitoreosilos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monitoreosilos.ui.viewmodel.AjustesViewModel
import com.example.monitoreosilos.ui.viewmodel.MainViewModel

@Composable
fun AlertasScreen(
    viewModel: MainViewModel,
    ajustesViewModel: AjustesViewModel
) {
    val silos by viewModel.silos.collectAsStateWithLifecycle()
    val ajustes by ajustesViewModel.uiState.collectAsStateWithLifecycle()

    // Silos con el porcentaje del umbral guardado o menos, del más bajo al más alto
    val alertas = silos
        .filter { it.porcentaje <= ajustes.umbralAlerta }
        .sortedBy { it.porcentaje }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Alertas",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        when {
            !ajustes.alertasActivadas -> MensajeCentrado(
                "Las alertas están desactivadas. Actívalas en Ajustes."
            )

            alertas.isEmpty() -> MensajeCentrado(
                "No hay silos con ${ajustes.umbralAlerta}% o menos"
            )

            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 320.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(alertas, key = { it.id }) { silo ->
                    SiloCard(silo)
                }
            }
        }
    }
}

@Composable
private fun MensajeCentrado(texto: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto, textAlign = TextAlign.Center)
    }
}