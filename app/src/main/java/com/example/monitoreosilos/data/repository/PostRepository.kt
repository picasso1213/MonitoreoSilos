package com.example.monitoreosilos.data.repository

import com.example.monitoreosilos.data.model.Post
import com.example.monitoreosilos.data.remote.ApiService

class PostRepository(private val api: ApiService) {

    suspend fun getPosts(): List<Post> = api.getPosts()

    suspend fun getPost(id: Int): Post = api.getPost(id)
}