package com.example.ics342shaneschultesapplication

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import kotlinx.coroutines.flow.first

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ToDoListViewModelTest {

    private val mockApiService = mockk<ToDoApiService>()
    private val application = mockk<Application>(relaxed = true)
    private lateinit var viewModel: ToDoListViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ToDoListViewModel(application, mockApiService)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchTodos should update todoListState with fetched todos`() = runTest(testDispatcher) {
        val mockTodos = listOf(
            ToDoItem(2057, "Wash Clothes", completed = false),
            ToDoItem(2058, "Wash Dishes", completed = true)
        )

        coEvery { mockApiService.getTodos(any()) } returns retrofit2.Response.success(mockTodos)

        viewModel.fetchTodos()

        advanceUntilIdle()

        coVerify { mockApiService.getTodos(any()) }
        assertEquals(mockTodos, viewModel.todoListState.first())
    }

    @Test
    fun `createTodo should add a new todo and fetch updated list`() = runTest(testDispatcher) {
        val mockTodo = ToDoItem(1, "Test Todo", completed = false)

        coEvery { mockApiService.createTodo(any(), any()) } returns retrofit2.Response.success(mockTodo)
        coEvery { mockApiService.getTodos(any()) } returns retrofit2.Response.success(listOf(mockTodo))

        viewModel.createTodo("Test Todo")

        advanceUntilIdle()

        coVerify { mockApiService.createTodo(any(), any()) }
        coVerify { mockApiService.getTodos(any()) }
        assertEquals(listOf(mockTodo), viewModel.todoListState.first())
    }

    @Test
    fun `updateTodoStatus should update the todo and fetch updated list`() = runTest(testDispatcher) {
        val mockTodo = ToDoItem(1, "Test Todo", completed = false)
        val updatedTodo = mockTodo.copy(completed = true)

        coEvery { mockApiService.getTodos(any()) } returns retrofit2.Response.success(listOf(mockTodo))
        coEvery { mockApiService.updateTodo(any(), any(), any()) } returns retrofit2.Response.success(updatedTodo)
        coEvery { mockApiService.getTodos(any()) } returns retrofit2.Response.success(listOf(updatedTodo))

        viewModel.updateTodoStatus(1, true)

        advanceUntilIdle()

        coVerify { mockApiService.updateTodo(any(), any(), any()) }
        coVerify { mockApiService.getTodos(any()) }
        assertEquals(listOf(updatedTodo), viewModel.todoListState.first())
    }
}
