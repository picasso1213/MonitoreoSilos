package com.example.monitoreosilos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreosilos.data.model.Post
import com.example.monitoreosilos.data.remote.RetrofitClient
import com.example.monitoreosilos.data.repository.PostRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PostUiState(
    val posts: List<Post> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class PostViewModel(
    private val repositorio: PostRepository = PostRepository(RetrofitClient.api)
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    init {
        cargarPosts()
    }

    // Sirve también para "Actualizar" y "Reintentar"
    fun cargarPosts() {
        viewModelScope.launch {
            _uiState.value = PostUiState(cargando = true)
            try {
                _uiState.value = PostUiState(posts = repositorio.getPosts())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = PostUiState(error = "No fue posible cargar los avisos")
            }
        }
    }
}

