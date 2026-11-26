package com.example.monitoreosilos.viewmodel

import com.example.monitoreosilos.MainDispatcherRule
import com.example.monitoreosilos.data.PreferenciasRepository
import com.example.monitoreosilos.ui.viewmodel.AjustesViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AjustesViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val flujoNombre = MutableStateFlow("")
    private val flujoAlertas = MutableStateFlow(true)
    private val flujoUmbral = MutableStateFlow(30)

    private val repo = mockk<PreferenciasRepository>(relaxed = true).also { r ->
        every { r.nombre } returns flujoNombre
        every { r.alertasActivadas } returns flujoAlertas
        every { r.umbralAlerta } returns flujoUmbral
        coEvery { r.guardarNombre(any()) } coAnswers { flujoNombre.value = firstArg() }
        coEvery { r.guardarAlertasActivadas(any()) } coAnswers { flujoAlertas.value = firstArg() }
        coEvery { r.guardarUmbralAlerta(any()) } coAnswers { flujoUmbral.value = firstArg() }
    }

    // El uiState usa WhileSubscribed: hay que tener un colector activo para leerlo
    private fun TestScope.crearVm(): AjustesViewModel {
        val vm = AjustesViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        return vm
    }

    @Test
    fun alIniciar_cargaElNombreGuardadoEnElCampo() = runTest {
        flujoNombre.value = "Ana"
        val vm = crearVm()
        assertEquals("Ana", vm.uiState.value.nombreGuardado)
        assertEquals("Ana", vm.uiState.value.nombreEditable)
    }

    @Test
    fun guardarNombreVacio_muestraError() = runTest {
        val vm = crearVm()
        vm.onNombreChange("   ")
        vm.guardarNombre()
        assertEquals("El nombre no puede estar vacío", vm.uiState.value.errorNombre)
        coVerify(exactly = 0) { repo.guardarNombre(any()) }
    }

    @Test
    fun guardarNombreLargo_muestraError() = runTest {
        val vm = crearVm()
        vm.onNombreChange("a".repeat(31))
        vm.guardarNombre()
        assertEquals("Máximo 30 caracteres", vm.uiState.value.errorNombre)
    }

    @Test
    fun guardarNombreValido_guardaSinEspacios() = runTest {
        val vm = crearVm()
        vm.onNombreChange("  Camila  ")
        vm.guardarNombre()
        coVerify { repo.guardarNombre("Camila") }
        assertEquals("Camila", vm.uiState.value.nombreGuardado)
        assertNull(vm.uiState.value.errorNombre)
    }

    @Test
    fun editarElNombre_limpiaElError() = runTest {
        val vm = crearVm()
        vm.onNombreChange("")
        vm.guardarNombre()
        vm.onNombreChange("Ana")
        assertNull(vm.uiState.value.errorNombre)
    }

    @Test
    fun cambiarAlertas_invierteElValor() = runTest {
        val vm = crearVm()
        vm.cambiarAlertasActivadas()
        coVerify { repo.guardarAlertasActivadas(false) }
        assertFalse(vm.uiState.value.alertasActivadas)
    }

    @Test
    fun subirYBajarUmbral_avanzaDeACinco() = runTest {
        val vm = crearVm()
        vm.subirUmbral()
        assertEquals(35, vm.uiState.value.umbralAlerta)
        vm.bajarUmbral()
        vm.bajarUmbral()
        assertEquals(25, vm.uiState.value.umbralAlerta)
    }

    @Test
    fun elUmbral_noSuperaLosLimites() = runTest {
        val vm = crearVm()
        flujoUmbral.value = 50
        vm.subirUmbral()
        assertEquals(50, vm.uiState.value.umbralAlerta)
        flujoUmbral.value = 5
        vm.bajarUmbral()
        assertEquals(5, vm.uiState.value.umbralAlerta)
    }
}