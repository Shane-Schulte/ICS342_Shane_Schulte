package com.example.ics342shaneschultesapplication

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateAccountViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val _createAccountState = MutableStateFlow<CreateAccountState>(CreateAccountState.Empty)
    val createAccountState: StateFlow<CreateAccountState> = _createAccountState

    fun createAccount(name: String, email: String, password: String) {
        viewModelScope.launch {
            _createAccountState.value = CreateAccountState.Loading
            try {
                val response = RetrofitInstance.api.createAccount(UserCreateRequest(name, email, password))
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    sharedPreferences.edit().putString("token", user.token).putInt("user_id", user.id).apply()
                    _createAccountState.value = CreateAccountState.Success(user)
                } else {
                    _createAccountState.value = CreateAccountState.Error("Account creation failed")
                }
            } catch (e: Exception) {
                _createAccountState.value = CreateAccountState.Error("Account creation failed: ${e.message}")
            }
        }
    }

    sealed class CreateAccountState {
        object Empty : CreateAccountState()
        object Loading : CreateAccountState()
        data class Success(val user: User) : CreateAccountState()
        data class Error(val message: String) : CreateAccountState()
    }
}
