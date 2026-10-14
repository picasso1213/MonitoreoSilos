package com.example.monitoreosilos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.monitoreosilos.data.model.EstadoSilo
import com.example.monitoreosilos.data.model.Silo

fun EstadoSilo.color(): Color = when (this) {
    EstadoSilo.NORMAL -> Color(0xFF2E7D32)
    EstadoSilo.ADVERTENCIA -> Color(0xFFF9A825)
    EstadoSilo.CRITICO -> Color(0xFFC62828)
}

fun EstadoSilo.texto(): String = when (this) {
    EstadoSilo.NORMAL -> "Normal"
    EstadoSilo.ADVERTENCIA -> "Advertencia"
    EstadoSilo.CRITICO -> "Crítico"
}

// Componente reutilizado por SilosScreen y AlertasScreen
@Composable
fun SiloCard(silo: Silo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = silo.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = silo.estado.texto(),
                    color = silo.estado.color(),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Text(
                text = silo.granja,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { silo.porcentaje / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = silo.estado.color()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${silo.porcentaje}% · ${silo.nivelKg} / ${silo.capacidadKg} kg")
            Text(
                text = "Actualizado: ${silo.actualizado}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}