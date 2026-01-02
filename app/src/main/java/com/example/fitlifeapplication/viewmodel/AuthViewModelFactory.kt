package com.example.fitlifeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitlifeapplication.data.manager.UserSessionManager
import com.example.fitlifeapplication.data.repository.UserRepository

class AuthViewModelFactory(
    private val userRepository: UserRepository,
    private val userSessionManager: UserSessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userRepository, userSessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
