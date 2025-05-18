package com.example.todolistapplication.data

import com.example.todolistapplication.data.api.NetworkModule
import com.example.todolistapplication.data.dao.TodoDao
import com.example.todolistapplication.data.model.Todo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.io.IOException

class TodoRepository(private val todoDao: TodoDao) {
    private val todoApi = NetworkModule.todoApi
    val todos: Flow<List<Todo>> = todoDao.getAllTodos()

    suspend fun fetchFromApiAndUpdateCache() {
        try {
            val apiTodos = todoApi.getTodos()
            val todos = apiTodos.map { apiTodo ->
                Todo(
                    id = 0,
                    apiId = apiTodo.id,
                    userId = apiTodo.userId,
                    title = apiTodo.title,
                    description = apiTodo.title,
                    isCompleted = apiTodo.completed
                )
            }
            todoDao.deleteAllApiTodos()
            todoDao.insertTodos(todos)
        } catch (e: Exception) {
            val message = when (e) {
                is IOException -> "Network error: Check your internet connection"
                else -> "Failed to fetch todos: ${e.message}"
            }
            if (todoDao.getTodoCount() == 0) {
                throw Exception(message)
            }
            throw Exception("$message (Showing cached data)")
        }
    }

    suspend fun getTodoById(id: Int): Todo {
        return todoDao.getTodoById(id)
    }
    suspend fun insertTodo(title: String, description: String, dueDate: LocalDate? = null) {
        val todo = Todo(
            title = title,
            description = description,
            dueDate = dueDate
        )
        todoDao.insertTodo(todo)
    }
    suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo)
    }
    suspend fun deleteTodo(todo: Todo) {
        todoDao.deleteTodo(todo)
    }
    suspend fun deleteAllTodos() {
        todoDao.deleteAllTodos()
    }
} 