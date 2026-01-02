package com.example.fitlifeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapplication.data.manager.UserSessionManager
import com.example.fitlifeapplication.data.model.User
import com.example.fitlifeapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Empty)
    val loginState: StateFlow<LoginState> = _loginState

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Empty)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                val user = userRepository.getUserByEmail(email)
                if (user != null && user.passwordHash == password) { // Replace with secure password check
                    userSessionManager.saveUser(user.userId)
                    _loginState.value = LoginState.Success(user)
                } else {
                    _loginState.value = LoginState.Error("Invalid email or password")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Login failed: ${e.message}")
            }
        }
    }
    
    fun loginAsGuest() {
        // Clear current session to indicate guest
        userSessionManager.clearSession()
    }

    fun register(displayName: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            try {
                _registrationState.value = RegistrationState.Loading
                if (password != confirmPassword) {
                    _registrationState.value = RegistrationState.Error("Passwords do not match")
                    return@launch
                }

                val existingUser = userRepository.getUserByEmail(email)
                if (existingUser != null) {
                    _registrationState.value = RegistrationState.Error("User with this email already exists")
                    return@launch
                }

                val user = User(email = email, displayName = displayName, passwordHash = password) // Add hashing
                val userId = userRepository.insert(user)
                if (userId > 0) {
                    userSessionManager.saveUser(userId)
                    _registrationState.value = RegistrationState.Success
                } else {
                    _registrationState.value = RegistrationState.Error("Failed to create user")
                }
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error("Registration failed: ${e.message}")
            }
        }
    }
}

sealed class LoginState {
    object Empty : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegistrationState {
    object Empty : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
