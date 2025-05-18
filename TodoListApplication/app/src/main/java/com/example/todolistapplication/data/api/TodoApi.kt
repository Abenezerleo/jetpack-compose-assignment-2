package com.example.todolistapplication.data.api

import retrofit2.http.GET
import retrofit2.http.Path


data class TodoResponse(
    val id: Int,
    val userId: Int,
    val title: String,
    val completed: Boolean
)


interface TodoApi {

    @GET("todos")
    suspend fun getTodos(): List<TodoApiModel>

    @GET("todos/{id}")
    suspend fun getTodoById(@Path("id") id: Int): TodoApiModel
}

data class TodoApiModel(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
) 