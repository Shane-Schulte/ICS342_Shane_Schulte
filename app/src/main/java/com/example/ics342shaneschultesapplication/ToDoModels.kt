package com.example.ics342shaneschultesapplication

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ToDoItem(
    val id: Int,
    val description: String,
    val completed: Boolean
) {
    companion object {
        private var currentId = 0

        fun create(description: String, completed: Boolean = false): ToDoItem {
            currentId += 1
            return ToDoItem(id = currentId, description = description, completed = completed)
        }
    }
}

@JsonClass(generateAdapter = true)
data class ToDoCreateRequest(
    val description: String,
    val completed: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ToDoUpdateRequest(
    val description: String,
    val completed: Boolean
)

@JsonClass(generateAdapter = true)
data class UserCreateRequest(
    val name: String,
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class UserLoginRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val token: String
)
