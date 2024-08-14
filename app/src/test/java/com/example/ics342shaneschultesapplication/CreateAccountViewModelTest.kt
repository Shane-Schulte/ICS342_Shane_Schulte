package com.example.ics342shaneschultesapplication

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import kotlinx.coroutines.flow.first

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CreateAccountViewModelTest {

    private val mockApiService = mockk<ToDoApiService>()
    private val application = mockk<Application>(relaxed = true)
    private lateinit var viewModel: CreateAccountViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CreateAccountViewModel(application, mockApiService)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create account should be successful with valid details`() = runTest(testDispatcher) {
        val mockUser = User(380, "user1", "test1@test.com", "a61941da-4818-4d6f-ad96-71d0a02faad0")

        // Setup mock response
        coEvery { mockApiService.createAccount(any()) } returns retrofit2.Response.success(mockUser)

        // Trigger the account creation
        viewModel.createAccount("user1", "test1@test.com", "password")

        // Wait for the coroutine to finish
        advanceUntilIdle()

        // Verify API call
        coVerify { mockApiService.createAccount(any()) }

        // Assert the state
        val createAccountState = viewModel.createAccountState.first()
        assertEquals(CreateAccountViewModel.CreateAccountState.Success(mockUser), createAccountState)
    }

    @Test
    fun `create account should fail with invalid details`() = runTest(testDispatcher) {
        // Setup mock response for failure
        coEvery { mockApiService.createAccount(any()) } returns retrofit2.Response.error(400, "".toResponseBody("application/json".toMediaTypeOrNull()))

        // Trigger the account creation
        viewModel.createAccount("user1", "invalid@test.com", "short")

        // Wait for the coroutine to finish
        advanceUntilIdle()

        // Verify API call
        coVerify { mockApiService.createAccount(any()) }

        // Assert the state
        val createAccountState = viewModel.createAccountState.first()
        assertEquals(CreateAccountViewModel.CreateAccountState.Error("Account creation failed"), createAccountState)
    }
}
