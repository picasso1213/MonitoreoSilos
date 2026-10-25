package com.example.monitoreosilos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.monitoreosilos.data.model.Estudiante
import com.example.monitoreosilos.ui.viewmodel.EstudianteViewModel

@Composable
fun EstudianteScreen(
    viewModel: EstudianteViewModel
) {
    val estudiantes by viewModel.estudiantes
        .collectAsState()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }

    var estudianteEditando by remember {
        mutableStateOf<Estudiante?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Gestión de Estudiantes",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = carrera,
            onValueChange = { carrera = it },
            label = { Text("Carrera") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (
                    nombre.isNotBlank() &&
                    correo.isNotBlank() &&
                    carrera.isNotBlank()
                ) {
                    val actual = estudianteEditando

                    if (actual == null) {
                        viewModel.insertar(
                            nombre,
                            correo,
                            carrera
                        )
                    } else {
                        viewModel.actualizar(
                            actual.copy(
                                nombre = nombre.trim(),
                                correo = correo.trim(),
                                carrera = carrera.trim()
                            )
                        )
                    }

                    nombre = ""
                    correo = ""
                    carrera = ""
                    estudianteEditando = null
                }
            }
        ) {
            Text(
                if (estudianteEditando == null) {
                    "Registrar estudiante"
                } else {
                    "Guardar cambios"
                }
            )
        }

        Text(
            text = "Estudiantes registrados: ${estudiantes.size}",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = estudiantes,
                key = { it.id }
            ) { estudiante ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = estudiante.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(text = estudiante.correo)
                        Text(text = estudiante.carrera)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    estudianteEditando = estudiante
                                    nombre = estudiante.nombre
                                    correo = estudiante.correo
                                    carrera = estudiante.carrera
                                }
                            ) {
                                Text("Editar")
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.eliminar(estudiante)

                                    if (
                                        estudianteEditando?.id ==
                                        estudiante.id
                                    ) {
                                        estudianteEditando = null
                                        nombre = ""
                                        correo = ""
                                        carrera = ""
                                    }
                                }
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
