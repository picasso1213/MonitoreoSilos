package com.example.monitoreosilos.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ImagenUiState(
    val bitmap: Bitmap? = null,
    val uri: Uri? = null,
    val origen: String = "",
    val siloId: Int? = null
)

class CamaraGaleriaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ImagenUiState())
    val uiState = _uiState.asStateFlow()

    fun seleccionarSilo(id: Int) {
        _uiState.update { it.copy(siloId = id) }
    }

    fun imagenDesdeCamara(bitmap: Bitmap?) {
        _uiState.update {
            it.copy(
                bitmap = bitmap,
                uri = null,
                origen = if (bitmap != null) "Cámara" else ""
            )
        }
    }

    fun imagenDesdeGaleria(uri: Uri?) {
        _uiState.update {
            it.copy(
                bitmap = null,
                uri = uri,
                origen = if (uri != null) "Galería" else ""
            )
        }
    }

    // Quita la imagen pero conserva el silo elegido
    fun limpiarImagen() {
        _uiState.update {
            it.copy(bitmap = null, uri = null, origen = "")
        }
    }
}