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
import com.example.ics342shaneschultesapplication.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val mockApiService = mockk<ToDoApiService>()
    private val application = mockk<Application>(relaxed = true)
    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(application, mockApiService)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login should be successful with correct credentials`() = runTest(testDispatcher) {
        val mockUser = User(380, "user1", "test1@test.com", "a61941da-4818-4d6f-ad96-71d0a02faad0")

        // Setup mock response
        coEvery { mockApiService.login(any()) } returns retrofit2.Response.success(mockUser)

        // Trigger the login
        viewModel.login("test1@test.com", "password")

        // Wait for the coroutine to finish
        advanceUntilIdle()

        // Verify API call
        coVerify { mockApiService.login(any()) }

        // Assert the state
        val loginState = viewModel.loginState.first()
        assertEquals(LoginViewModel.LoginState.Success(mockUser), loginState)
    }

    @Test
    fun `login should fail with incorrect credentials`() = runTest(testDispatcher) {
        // Setup mock response for failure
        coEvery { mockApiService.login(any()) } returns retrofit2.Response.error(401, "".toResponseBody("application/json".toMediaTypeOrNull()))

        // Trigger the login
        viewModel.login("wrong@example.com", "wrongpassword")

        // Wait for the coroutine to finish
        advanceUntilIdle()

        // Verify API call
        coVerify { mockApiService.login(any()) }

        // Assert the state
        val loginState = viewModel.loginState.first()
        assertEquals(LoginViewModel.LoginState.Error("Invalid credentials"), loginState)
    }
}
