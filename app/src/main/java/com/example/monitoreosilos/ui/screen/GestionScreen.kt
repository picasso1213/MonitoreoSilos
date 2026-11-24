package com.example.monitoreosilos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monitoreosilos.data.remote.IncidenciaRemota
import com.example.monitoreosilos.data.remote.SiloRemoto
import com.example.monitoreosilos.ui.viewmodel.GestionUiState
import com.example.monitoreosilos.ui.viewmodel.GestionViewModel

@Composable
fun GestionScreen(viewModel: GestionViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    GestionContent(
        state = state,
        onRecargar = viewModel::cargar,
        onGuardarSilo = viewModel::guardarSilo,
        onEliminarSilo = viewModel::eliminarSilo,
        onCrearIncidencia = viewModel::crearIncidencia,
        onResolverIncidencia = viewModel::resolverIncidencia,
        onEliminarIncidencia = viewModel::eliminarIncidencia
    )
}

@Composable
fun GestionContent(
    state: GestionUiState,
    onRecargar: () -> Unit,
    onGuardarSilo: (SiloRemoto) -> Unit,
    onEliminarSilo: (Long) -> Unit,
    onCrearIncidencia: (Long, String) -> Unit,
    onResolverIncidencia: (Long) -> Unit,
    onEliminarIncidencia: (Long) -> Unit
) {
    var siloEnEdicion by remember { mutableStateOf<SiloRemoto?>(null) }
    var mostrarFormSilo by remember { mutableStateOf(false) }
    var mostrarFormIncidencia by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text("Servidor (microservicios)", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { siloEnEdicion = null; mostrarFormSilo = true }) { Text("Nuevo silo") }
            OutlinedButton(onClick = { mostrarFormIncidencia = true }) { Text("Nueva incidencia") }
            TextButton(onClick = onRecargar) { Text("Actualizar") }
        }

        if (state.cargando) CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 4.dp))
        }
        state.mensaje?.let {
            Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 4.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Text("Silos (${state.silos.size})", style = MaterialTheme.typography.titleMedium) }
            items(state.silos, key = { "s${it.id}" }) { silo ->
                SiloRemotoItem(
                    silo = silo,
                    onEditar = { siloEnEdicion = silo; mostrarFormSilo = true },
                    onEliminar = { silo.id?.let(onEliminarSilo) }
                )
            }
            item { Text("Incidencias (${state.incidencias.size})", style = MaterialTheme.typography.titleMedium) }
            items(state.incidencias, key = { "i${it.id}" }) { inc ->
                IncidenciaItem(
                    incidencia = inc,
                    onResolver = { inc.id?.let(onResolverIncidencia) },
                    onEliminar = { inc.id?.let(onEliminarIncidencia) }
                )
            }
        }
    }

    if (mostrarFormSilo) {
        FormSiloDialog(
            inicial = siloEnEdicion,
            onCancelar = { mostrarFormSilo = false },
            onGuardar = { onGuardarSilo(it); mostrarFormSilo = false }
        )
    }
    if (mostrarFormIncidencia) {
        FormIncidenciaDialog(
            onCancelar = { mostrarFormIncidencia = false },
            onGuardar = { siloId, desc -> onCrearIncidencia(siloId, desc); mostrarFormIncidencia = false }
        )
    }
}

@Composable
private fun SiloRemotoItem(silo: SiloRemoto, onEditar: () -> Unit, onEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("#${silo.id} ${silo.nombre} · ${silo.granja}", style = MaterialTheme.typography.titleSmall)
            Text("${silo.tipoAlimento} · ${silo.nivelKg}/${silo.capacidadKg} kg (${silo.porcentaje}%)")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onEditar) { Text("Editar") }
                TextButton(onClick = onEliminar) { Text("Eliminar") }
            }
        }
    }
}

@Composable
private fun IncidenciaItem(incidencia: IncidenciaRemota, onResolver: () -> Unit, onEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("Silo ${incidencia.siloId} · ${incidencia.estado}", style = MaterialTheme.typography.titleSmall)
            Text(incidencia.descripcion)
            incidencia.fecha?.let { Text(it, style = MaterialTheme.typography.labelSmall) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (incidencia.estado == "ABIERTA") {
                    TextButton(onClick = onResolver) { Text("Resolver") }
                }
                TextButton(onClick = onEliminar) { Text("Eliminar") }
            }
        }
    }
}

@Composable
private fun FormSiloDialog(inicial: SiloRemoto?, onCancelar: () -> Unit, onGuardar: (SiloRemoto) -> Unit) {
    var granja by remember { mutableStateOf(inicial?.granja ?: "") }
    var nombre by remember { mutableStateOf(inicial?.nombre ?: "") }
    var tipo by remember { mutableStateOf(inicial?.tipoAlimento ?: "") }
    var capacidad by remember { mutableStateOf(inicial?.capacidadKg?.toString() ?: "") }
    var nivel by remember { mutableStateOf(inicial?.nivelKg?.toString() ?: "") }
    var consumo by remember { mutableStateOf(inicial?.consumoDiarioKg?.toString() ?: "") }
    val numerico = KeyboardOptions(keyboardType = KeyboardType.Number)

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (inicial == null) "Nuevo silo" else "Editar silo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(granja, { granja = it }, label = { Text("Granja") }, singleLine = true)
                OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(tipo, { tipo = it }, label = { Text("Tipo de alimento") }, singleLine = true)
                OutlinedTextField(capacidad, { capacidad = it }, label = { Text("Capacidad (kg)") },
                    singleLine = true, keyboardOptions = numerico)
                OutlinedTextField(nivel, { nivel = it }, label = { Text("Nivel (kg)") },
                    singleLine = true, keyboardOptions = numerico)
                OutlinedTextField(consumo, { consumo = it }, label = { Text("Consumo diario (kg)") },
                    singleLine = true, keyboardOptions = numerico)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onGuardar(
                    SiloRemoto(
                        id = inicial?.id,
                        granja = granja,
                        nombre = nombre,
                        capacidadKg = capacidad.toIntOrNull() ?: 0,
                        nivelKg = nivel.toIntOrNull() ?: -1,
                        tipoAlimento = tipo,
                        consumoDiarioKg = consumo.toIntOrNull() ?: 0
                    )
                )
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
private fun FormIncidenciaDialog(onCancelar: () -> Unit, onGuardar: (Long, String) -> Unit) {
    var siloId by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nueva incidencia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(siloId, { siloId = it }, label = { Text("N° de silo") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(descripcion, { descripcion = it }, label = { Text("Descripción") })
            }
        },
        confirmButton = {
            TextButton(
                enabled = siloId.toLongOrNull() != null,
                onClick = { onGuardar(siloId.toLong(), descripcion) }
            ) { Text("Registrar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}