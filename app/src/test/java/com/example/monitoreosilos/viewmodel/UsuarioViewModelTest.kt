package com.example.monitoreosilos.viewmodel

import com.example.monitoreosilos.ui.viewmodel.UsuarioViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UsuarioViewModelTest {

    private fun vmValido() = UsuarioViewModel().apply {
        onNombreChange("Camila Rojas")
        onCorreoChange("camila@granja.cl")
        onClaveChange("clave1234")
        onDireccionChange("Camino Los Aromos 123")
        onRolChange("Supervisor")
        onTerminosChange(true)
    }

    @Test
    fun formularioCompleto_esValido() {
        val vm = vmValido()
        assertTrue(vm.validarFormulario())
        assertNull(vm.errores.value.correo)
    }

    @Test
    fun formularioVacio_marcaTodosLosErrores() {
        val vm = UsuarioViewModel()
        assertFalse(vm.validarFormulario())
        val e = vm.errores.value
        assertEquals("El nombre es obligatorio", e.nombre)
        assertEquals("El correo es obligatorio", e.correo)
        assertEquals("Mínimo 8 caracteres", e.clave)
        assertEquals("La dirección es obligatoria", e.direccion)
        assertEquals("Selecciona un rol", e.rol)
        assertEquals("Debes aceptar los términos", e.terminos)
    }

    @Test
    fun correoInvalido_seRechaza() {
        val vm = vmValido().apply { onCorreoChange("no-es-correo") }
        assertFalse(vm.validarFormulario())
        assertEquals("Correo no válido", vm.errores.value.correo)
    }

    @Test
    fun claveSinNumero_seRechaza() {
        val vm = vmValido().apply { onClaveChange("sinnumeros") }
        assertFalse(vm.validarFormulario())
        assertEquals("Debe incluir al menos un número", vm.errores.value.clave)
    }

    @Test
    fun editarUnCampo_limpiaSuError() {
        val vm = UsuarioViewModel()
        vm.validarFormulario()
        vm.onNombreChange("Ana")
        assertNull(vm.errores.value.nombre)
        assertEquals("Ana", vm.estado.value.nombre)
    }
}