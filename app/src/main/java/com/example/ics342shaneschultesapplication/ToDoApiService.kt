package com.example.ics342shaneschultesapplication

import retrofit2.Response
import retrofit2.http.*

interface ToDoApiService {
    @POST("api/users/register")
    suspend fun createAccount(@Body userCreateRequest: UserCreateRequest): Response<User>

    @POST("api/users/login")
    suspend fun login(@Body userLoginRequest: UserLoginRequest): Response<User>

    @GET("api/users/{user_id}/todos")
    suspend fun getTodos(@Path("user_id") userId: Int): Response<List<ToDoItem>>

    @POST("api/users/{user_id}/todos")
    suspend fun createTodo(
        @Path("user_id") userId: Int,
        @Body todoCreateRequest: ToDoCreateRequest
    ): Response<ToDoItem>

    @PUT("api/users/{user_id}/todos/{id}")
    suspend fun updateTodo(
        @Path("user_id") userId: Int,
        @Path("id") todoId: Int,
        @Body todoUpdateRequest: ToDoUpdateRequest
    ): Response<ToDoItem>
}
