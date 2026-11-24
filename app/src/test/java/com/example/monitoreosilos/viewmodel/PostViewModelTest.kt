package com.example.monitoreosilos.viewmodel

import com.example.monitoreosilos.MainDispatcherRule
import com.example.monitoreosilos.data.model.Post
import com.example.monitoreosilos.data.repository.PostRepository
import com.example.monitoreosilos.ui.viewmodel.PostViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class PostViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val repo = mockk<PostRepository>()
    private val posts = listOf(Post(1, 1, "A", "a"), Post(1, 2, "B", "b"))

    @Test
    fun alIniciar_cargaLosAvisos() {
        coEvery { repo.getPosts() } returns posts
        val vm = PostViewModel(repo)
        assertEquals(posts, vm.uiState.value.posts)
        assertFalse(vm.uiState.value.cargando)
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun siLaApiFalla_muestraError() {
        coEvery { repo.getPosts() } throws RuntimeException("sin red")
        val vm = PostViewModel(repo)
        assertNotNull(vm.uiState.value.error)
        assertEquals(emptyList<Post>(), vm.uiState.value.posts)
    }

    @Test
    fun reintentar_vuelveACargarTrasUnError() {
        coEvery { repo.getPosts() } throws RuntimeException("sin red")
        val vm = PostViewModel(repo)
        coEvery { repo.getPosts() } returns posts
        vm.cargarPosts()
        assertEquals(posts, vm.uiState.value.posts)
        assertNull(vm.uiState.value.error)
        coVerify(exactly = 2) { repo.getPosts() }
    }
}