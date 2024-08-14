package com.example.ics342shaneschultesapplication

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ToDoListViewModel(application: Application, private val apiService: ToDoApiService) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val _todoListState = MutableStateFlow<List<ToDoItem>>(emptyList())
    val todoListState: StateFlow<List<ToDoItem>> = _todoListState

    private val userId: Int = sharedPreferences.getInt("user_id", -1)
    private val apiKey: String? = sharedPreferences.getString("token", null)

    init {
        fetchTodos()
    }

    fun fetchTodos() {
        if (userId != -1 && apiKey != null) {
            viewModelScope.launch {
                val response = apiService.getTodos(userId)
                if (response.isSuccessful) {
                    _todoListState.value = response.body() ?: emptyList()
                } else {
                    // Handle error
                }
            }
        }
    }

    fun createTodo(description: String) {
        if (userId != -1 && apiKey != null) {
            viewModelScope.launch {
                val response = apiService.createTodo(userId, ToDoCreateRequest(description))
                if (response.isSuccessful) {
                    fetchTodos()
                } else {
                    // Handle error
                }
            }
        }
    }

    fun updateTodoStatus(todoId: Int, completed: Boolean) {
        if (userId != -1 && apiKey != null) {
            viewModelScope.launch {
                val todo = _todoListState.value.find { it.id == todoId }
                if (todo != null) {
                    val response = apiService.updateTodo(
                        userId,
                        todoId,
                        ToDoUpdateRequest(todo.description, completed)
                    )
                    if (response.isSuccessful) {
                        fetchTodos()
                    } else {
                        // Handle error
                    }
                }
            }
        }
    }
}
