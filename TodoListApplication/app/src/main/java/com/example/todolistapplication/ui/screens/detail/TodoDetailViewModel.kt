package com.example.todolistapplication.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistapplication.data.TodoRepository
import com.example.todolistapplication.data.model.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TodoDetailUiState {
    data object Loading : TodoDetailUiState()
    data class Success(val todo: Todo) : TodoDetailUiState()
    data class Error(val message: String) : TodoDetailUiState()
}

class TodoDetailViewModel(
    private val todoId: Int,
    private val repository: TodoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<TodoDetailUiState>(TodoDetailUiState.Loading)
    val uiState: StateFlow<TodoDetailUiState> = _uiState

    init {
        loadTodo()
    }

    fun loadTodo() {
        viewModelScope.launch {
            try {
                val todo = repository.getTodoById(todoId)
                _uiState.value = TodoDetailUiState.Success(todo)
            } catch (e: Exception) {
                _uiState.value = TodoDetailUiState.Error(e.message ?: "Failed to load todo")
            }
        }
    }

    fun toggleTodoCompleted(todo: Todo) {
        viewModelScope.launch {
            try {
                repository.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
                loadTodo()
            } catch (e: Exception) {
                _uiState.value = TodoDetailUiState.Error("Failed to update todo: ${e.message}")
            }
        }
    }

    fun deleteTodo(todo: Todo, onNavigateBack: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteTodo(todo)
                onNavigateBack()
            } catch (e: Exception) {
                _uiState.value = TodoDetailUiState.Error("Failed to delete todo: ${e.message}")
            }
        }
    }
} 