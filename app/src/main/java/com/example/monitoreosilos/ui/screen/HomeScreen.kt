package com.example.monitoreosilos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monitoreosilos.data.model.EstadoSilo
import com.example.monitoreosilos.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onVerSilosClick: () -> Unit
) {
    val silos by viewModel.silos.collectAsStateWithLifecycle()

    val normales = silos.count { it.estado == EstadoSilo.NORMAL }
    val advertencia = silos.count { it.estado == EstadoSilo.ADVERTENCIA }
    val criticos = silos.count { it.estado == EstadoSilo.CRITICO }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Monitoreo de silos",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Resumen del estado del alimento en tus granjas")
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResumenCard("Normal", normales, EstadoSilo.NORMAL, Modifier.weight(1f))
            ResumenCard("Advertencia", advertencia, EstadoSilo.ADVERTENCIA, Modifier.weight(1f))
            ResumenCard("Crítico", criticos, EstadoSilo.CRITICO, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onVerSilosClick) {
            Text("Ver silos")
        }
    }
}

@Composable
private fun ResumenCard(
    titulo: String,
    cantidad: Int,
    estado: EstadoSilo,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = cantidad.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = estado.color()
            )
            Text(text = titulo, style = MaterialTheme.typography.labelMedium)
        }
    }
}