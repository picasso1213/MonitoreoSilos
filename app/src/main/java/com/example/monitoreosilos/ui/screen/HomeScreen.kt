package com.example.monitoreosilos.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monitoreosilos.R
import com.example.monitoreosilos.data.model.EstadoSilo
import com.example.monitoreosilos.data.model.Silo
import com.example.monitoreosilos.ui.viewmodel.MainViewModel

// Pantalla con estado: obtiene los datos del ViewModel
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onVerSilosClick: () -> Unit
) {
    val silos by viewModel.silos.collectAsStateWithLifecycle()
    HomeContent(silos = silos, onVerSilosClick = onVerSilosClick)
}

// Pantalla sin estado: decide el diseño según el ancho disponible
@Composable
fun HomeContent(
    silos: List<Silo>,
    onVerSilosClick: () -> Unit
) {
    val normales = silos.count { it.estado == EstadoSilo.NORMAL }
    val advertencia = silos.count { it.estado == EstadoSilo.ADVERTENCIA }
    val criticos = silos.count { it.estado == EstadoSilo.CRITICO }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            // Compacta: todo en una columna
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SiloImagen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                Encabezado(textAlign = TextAlign.Center)
                ResumenFila(normales, advertencia, criticos)
                Button(onClick = onVerSilosClick) {
                    Text("Ver silos")
                }
            }
        } else {
            // Mediana y expandida: imagen a la izquierda, contenido a la derecha
            val margen = if (maxWidth < 840.dp) 24.dp else 48.dp
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(margen),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SiloImagen(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Encabezado(textAlign = TextAlign.Start)
                    ResumenFila(normales, advertencia, criticos)
                    Button(onClick = onVerSilosClick) {
                        Text("Ver silos")
                    }
                }
            }
        }
    }
}

@Composable
private fun SiloImagen(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_silo),
        contentDescription = "Ilustración de un silo de alimento",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun Encabezado(textAlign: TextAlign) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Monitoreo de silos",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = textAlign
        )
        Text(
            text = "Resumen del estado del alimento en tus granjas",
            textAlign = textAlign
        )
    }
}

@Composable
private fun ResumenFila(normales: Int, advertencia: Int, criticos: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ResumenCard("Normal", normales, EstadoSilo.NORMAL, Modifier.weight(1f))
        ResumenCard("Advertencia", advertencia, EstadoSilo.ADVERTENCIA, Modifier.weight(1f))
        ResumenCard("Crítico", criticos, EstadoSilo.CRITICO, Modifier.weight(1f))
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

// Datos de ejemplo solo para las vistas previas
private val silosDemo = listOf(
    Silo(1, "Granja Los Aromos", "Silo A1", 20000, 16400, "09-10-2026 08:15"),
    Silo(2, "Granja Los Aromos", "Silo A2", 20000, 5200, "09-10-2026 08:15"),
    Silo(3, "Granja Los Aromos", "Silo A3", 15000, 1800, "09-10-2026 08:10")
)

@Preview(name = "Compacta", widthDp = 400, heightDp = 800, showBackground = true)
@Preview(name = "Mediana", widthDp = 700, heightDp = 800, showBackground = true)
@Preview(name = "Expandida", widthDp = 1000, heightDp = 700, showBackground = true)
@Composable
private fun HomePreview() {
    MaterialTheme {
        HomeContent(silos = silosDemo, onVerSilosClick = {})
    }
}