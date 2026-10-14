package com.example.monitoreosilos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monitoreosilos.ui.viewmodel.UsuarioViewModel

@Composable
fun ResumenScreen(
    viewModel: UsuarioViewModel,
    onEditarClick: () -> Unit,
    onInicioClick: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Resumen del registro",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilaDato("Nombre", estado.nombre)
                FilaDato("Correo", estado.correo)
                FilaDato("Clave", "•".repeat(estado.clave.length))
                FilaDato("Dirección", estado.direccion)
                FilaDato("Rol", estado.rol)
                FilaDato(
                    "Términos",
                    if (estado.aceptaTerminos) "Aceptados" else "No aceptados"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onInicioClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir al inicio")
        }
        OutlinedButton(
            onClick = onEditarClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar datos")
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Column {
        Text(text = etiqueta, style = MaterialTheme.typography.labelMedium)
        Text(
            text = valor.ifBlank { "—" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}