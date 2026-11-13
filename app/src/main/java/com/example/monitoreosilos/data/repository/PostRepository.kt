package com.example.monitoreosilos.data.repository

import com.example.monitoreosilos.data.model.Post
import com.example.monitoreosilos.data.remote.RetrofitClient

class PostRepository {

    suspend fun getPosts(): List<Post> {
        return RetrofitClient.api.getPosts()
    }
}
