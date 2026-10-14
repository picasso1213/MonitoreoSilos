package com.example.monitoreosilos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monitoreosilos.ui.viewmodel.UsuarioViewModel

private val roles = listOf("Operario", "Supervisor", "Jefatura")

@Composable
fun RegistroScreen(
    viewModel: UsuarioViewModel,
    onRegistrado: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val errores by viewModel.errores.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Registro de usuario",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(text = "Completa tus datos para usar la aplicación")
        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = estado.nombre,
            onCambio = viewModel::onNombreChange,
            etiqueta = "Nombre completo",
            error = errores.nombre
        )
        CampoTexto(
            valor = estado.correo,
            onCambio = viewModel::onCorreoChange,
            etiqueta = "Correo electrónico",
            error = errores.correo,
            keyboardType = KeyboardType.Email
        )
        CampoTexto(
            valor = estado.clave,
            onCambio = viewModel::onClaveChange,
            etiqueta = "Clave",
            error = errores.clave,
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation()
        )
        CampoTexto(
            valor = estado.direccion,
            onCambio = viewModel::onDireccionChange,
            etiqueta = "Dirección",
            error = errores.direccion
        )

        Text(text = "Rol", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            roles.forEach { rol ->
                FilterChip(
                    selected = estado.rol == rol,
                    onClick = { viewModel.onRolChange(rol) },
                    label = { Text(rol) }
                )
            }
        }
        errores.rol?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = estado.aceptaTerminos,
                onCheckedChange = viewModel::onTerminosChange
            )
            Text(text = "Acepto los términos de uso")
        }
        errores.terminos?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (viewModel.validarFormulario()) {
                    onRegistrado()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }
    }
}

@Composable
private fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
}