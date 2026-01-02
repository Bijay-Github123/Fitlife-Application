package com.example.fitlifeapplication.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitlifeapplication.FitLifeApplication
import com.example.fitlifeapplication.ui.components.FitLifeTextField
import com.example.fitlifeapplication.ui.components.HeaderCard
import com.example.fitlifeapplication.ui.components.PrimaryButton
import com.example.fitlifeapplication.ui.navigation.Screen
import com.example.fitlifeapplication.viewmodel.AuthViewModel
import com.example.fitlifeapplication.viewmodel.AuthViewModelFactory
import com.example.fitlifeapplication.viewmodel.RegistrationState

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as FitLifeApplication
    val factory = AuthViewModelFactory(application.userRepository, application.userSessionManager)
    val authViewModel: AuthViewModel = viewModel(factory = factory)

    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var displayNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val registrationState by authViewModel.registrationState.collectAsState()

    fun validate(): Boolean {
        var isValid = true
        
        if (displayName.isBlank()) {
            displayNameError = "Display name cannot be empty"
            isValid = false
        } else {
            displayNameError = null
        }

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Invalid email format"
            isValid = false
        } else {
            emailError = null
        }
        
        if (password.isBlank()) {
            passwordError = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordError = null
        }
        
        if (confirmPassword != password) {
            confirmPasswordError = "Passwords do not match"
            isValid = false
        } else {
            confirmPasswordError = null
        }

        return isValid
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderCard()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(28.dp))
                FitLifeTextField(
                    value = displayName,
                    onValueChange = { 
                        displayName = it
                        if (displayNameError != null) displayNameError = null
                    },
                    placeholder = "Display Name",
                    leadingIcon = Icons.Default.Person,
                    isError = displayNameError != null,
                    errorMessage = displayNameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                FitLifeTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        if (emailError != null) emailError = null
                    },
                    placeholder = "Email",
                    leadingIcon = Icons.Default.Email,
                    isError = emailError != null,
                    errorMessage = emailError
                )
                Spacer(modifier = Modifier.height(16.dp))
                FitLifeTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        if (passwordError != null) passwordError = null
                        if (confirmPasswordError != null && confirmPassword == it) confirmPasswordError = null 
                    },
                    placeholder = "Password",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError != null,
                    errorMessage = passwordError
                )
                Spacer(modifier = Modifier.height(16.dp))
                FitLifeTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        if (confirmPasswordError != null) confirmPasswordError = null
                    },
                    placeholder = "Confirm Password",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = confirmPasswordError != null,
                    errorMessage = confirmPasswordError
                )
                Spacer(modifier = Modifier.height(28.dp))
                
                if (registrationState is RegistrationState.Error) {
                    Text(
                        text = (registrationState as RegistrationState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (registrationState is RegistrationState.Loading || registrationState is RegistrationState.Success) {
                    CircularProgressIndicator()
                } else {
                    PrimaryButton(text = "Register", onClick = { 
                        if (validate()) {
                            authViewModel.register(displayName, email, password, confirmPassword) 
                        }
                    })
                }

                LaunchedEffect(registrationState) {
                    if (registrationState is RegistrationState.Success) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                    Text("Already have an account? Sign In", color = Color(0xFF6A1B9A))
                }
            }
        }
    }
}
