package com.example.monitoreosilos.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monitoreosilos.ui.viewmodel.AjustesViewModel

@Composable
fun AjustesScreen(viewModel: AjustesViewModel) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Animaciones: el valor objetivo cambia con el estado y Compose hace la transición
    val colorTarjeta by animateColorAsState(
        targetValue = if (estado.alertasActivadas) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        label = "colorTarjeta"
    )
    val escalaTitulo by animateFloatAsState(
        targetValue = if (estado.alertasActivadas) 1.05f else 1f,
        label = "escalaTitulo"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.graphicsLayer {
                scaleX = escalaTitulo
                scaleY = escalaTitulo
            }
        )

        Text(
            text = if (estado.nombreGuardado.isBlank()) {
                "¡Bienvenido!"
            } else {
                "¡Hola, ${estado.nombreGuardado}!"
            },
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = estado.nombreEditable,
            onValueChange = viewModel::onNombreChange,
            label = { Text("Tu nombre") },
            singleLine = true,
            isError = estado.errorNombre != null,
            supportingText = { estado.errorNombre?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = viewModel::guardarNombre) {
            Text("Guardar nombre")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorTarjeta)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Umbral de alerta")
                Text(
                    text = "${estado.umbralAlerta}%",
                    style = MaterialTheme.typography.displayMedium
                )
                Text(
                    text = "Se avisará cuando un silo tenga este nivel o menos",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(onClick = viewModel::bajarUmbral) {
                        Text("-")
                    }
                    Button(onClick = viewModel::subirUmbral) {
                        Text("+")
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Alertas activadas",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = estado.alertasActivadas,
                onCheckedChange = { viewModel.cambiarAlertasActivadas() }
            )
        }

        Text(
            text = if (estado.alertasActivadas) {
                "Alertas activadas"
            } else {
                "Alertas desactivadas"
            }
        )
    }
}