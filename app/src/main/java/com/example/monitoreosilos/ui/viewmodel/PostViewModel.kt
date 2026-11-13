package com.example.monitoreosilos.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreosilos.data.model.Post
import com.example.monitoreosilos.data.repository.PostRepository
import kotlinx.coroutines.launch

data class PostUiState(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class PostViewModel : ViewModel() {

    private val repository = PostRepository()

    var uiState by mutableStateOf(PostUiState())
        private set

    fun cargarPosts() {
        viewModelScope.launch {
            uiState = PostUiState(loading = true)
            try {
                val resultado = repository.getPosts()
                uiState = PostUiState(posts = resultado)
            } catch (e: Exception) {
                uiState = PostUiState(error = "No fue posible cargar los datos")
            }
        }
    }
}
