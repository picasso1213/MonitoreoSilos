package com.example.monitoreosilos.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.monitoreosilos.ui.viewmodel.CamaraGaleriaViewModel
import com.example.monitoreosilos.ui.viewmodel.MainViewModel

@Composable
fun CamaraGaleriaScreen(
    viewModel: CamaraGaleriaViewModel,
    mainViewModel: MainViewModel
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val silos by mainViewModel.silos.collectAsStateWithLifecycle()

    val siloSeleccionado = silos.find { it.id == estado.siloId }
    val bitmap = estado.bitmap
    val uri = estado.uri

    // La pantalla gestiona los contratos de Android y entrega el resultado al ViewModel
    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { resultado ->
        if (resultado != null) {
            viewModel.imagenDesdeCamara(resultado)
        }
    }

    val galeria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { resultado ->
        if (resultado != null) {
            viewModel.imagenDesdeGaleria(resultado)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Evidencia fotográfica",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(text = "Elige un silo y adjunta una foto")

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(silos, key = { it.id }) { silo ->
                FilterChip(
                    selected = silo.id == estado.siloId,
                    onClick = { viewModel.seleccionarSilo(silo.id) },
                    label = { Text(silo.nombre) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { camara.launch(null) },
                enabled = siloSeleccionado != null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Tomar foto")
            }
            Button(
                onClick = { galeria.launch("image/*") },
                enabled = siloSeleccionado != null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Galería")
            }
        }

        if (siloSeleccionado == null) {
            Text(
                text = "Primero selecciona un silo",
                style = MaterialTheme.typography.bodySmall
            )
        }

        when {
            bitmap != null -> {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Fotografía tomada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentScale = ContentScale.Fit
                )
            }

            uri != null -> {
                AsyncImage(
                    model = uri,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentScale = ContentScale.Fit
                )
            }

            else -> {
                Text("No hay ninguna imagen seleccionada")
            }
        }

        if (estado.origen.isNotEmpty()) {
            if (siloSeleccionado != null) {
                Text(
                    text = "Evidencia de ${siloSeleccionado.nombre} · ${siloSeleccionado.granja}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text("Imagen obtenida desde: ${estado.origen}")
            OutlinedButton(onClick = viewModel::limpiarImagen) {
                Text("Limpiar imagen")
            }
        }
    }
}