package com.example.monitoreosilos.data

import com.example.monitoreosilos.data.model.Post
import com.example.monitoreosilos.data.remote.ApiService
import com.example.monitoreosilos.data.repository.PostRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PostRepositoryTest {

    private val api = mockk<ApiService>()
    private val repo = PostRepository(api)
    private val post = Post(userId = 1, id = 7, title = "Aviso", body = "Cuerpo")

    @Test
    fun getPosts_retornaLaListaDeLaApi() = runTest {
        coEvery { api.getPosts() } returns listOf(post)
        assertEquals(listOf(post), repo.getPosts())
    }

    @Test
    fun getPost_pideElIdIndicado() = runTest {
        coEvery { api.getPost(7) } returns post
        assertEquals(post, repo.getPost(7))
        coVerify(exactly = 1) { api.getPost(7) }
    }

    @Test(expected = RuntimeException::class)
    fun getPosts_propagaLaExcepcionDeRed() = runTest {
        coEvery { api.getPosts() } throws RuntimeException("sin red")
        repo.getPosts()
    }
}