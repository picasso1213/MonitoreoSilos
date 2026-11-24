package com.example.monitoreosilos.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import com.example.monitoreosilos.ui.viewmodel.CamaraGaleriaViewModel
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class CamaraGaleriaViewModelTest {

    private val bitmap = mockk<Bitmap>()
    private val uri = mockk<Uri>()

    @Test
    fun seleccionarSilo_guardaElId() {
        val vm = CamaraGaleriaViewModel()
        vm.seleccionarSilo(4)
        assertEquals(4, vm.uiState.value.siloId)
    }

    @Test
    fun imagenDesdeCamara_guardaElBitmapYElOrigen() {
        val vm = CamaraGaleriaViewModel()
        vm.imagenDesdeCamara(bitmap)
        assertSame(bitmap, vm.uiState.value.bitmap)
        assertNull(vm.uiState.value.uri)
        assertEquals("Cámara", vm.uiState.value.origen)
    }

    @Test
    fun imagenDesdeCamara_nulaDejaElOrigenVacio() {
        val vm = CamaraGaleriaViewModel()
        vm.imagenDesdeCamara(null)
        assertEquals("", vm.uiState.value.origen)
    }

    @Test
    fun imagenDesdeGaleria_guardaLaUriYQuitaElBitmap() {
        val vm = CamaraGaleriaViewModel()
        vm.imagenDesdeCamara(bitmap)
        vm.imagenDesdeGaleria(uri)
        assertSame(uri, vm.uiState.value.uri)
        assertNull(vm.uiState.value.bitmap)
        assertEquals("Galería", vm.uiState.value.origen)
    }

    @Test
    fun imagenDesdeGaleria_nulaDejaElOrigenVacio() {
        val vm = CamaraGaleriaViewModel()
        vm.imagenDesdeGaleria(null)
        assertEquals("", vm.uiState.value.origen)
    }

    @Test
    fun limpiarImagen_conservaElSilo() {
        val vm = CamaraGaleriaViewModel()
        vm.seleccionarSilo(2)
        vm.imagenDesdeGaleria(uri)
        vm.limpiarImagen()
        assertNull(vm.uiState.value.uri)
        assertEquals("", vm.uiState.value.origen)
        assertEquals(2, vm.uiState.value.siloId)
    }
}