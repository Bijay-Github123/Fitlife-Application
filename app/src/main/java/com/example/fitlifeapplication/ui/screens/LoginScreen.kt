package com.example.fitlifeapplication.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitlifeapplication.FitLifeApplication
import com.example.fitlifeapplication.R
import com.example.fitlifeapplication.ui.components.FitLifeTextField
import com.example.fitlifeapplication.ui.components.GuestButton
import com.example.fitlifeapplication.ui.components.HeaderCard
import com.example.fitlifeapplication.ui.components.PrimaryButton
import com.example.fitlifeapplication.ui.navigation.Screen
import com.example.fitlifeapplication.viewmodel.AuthViewModel
import com.example.fitlifeapplication.viewmodel.AuthViewModelFactory
import com.example.fitlifeapplication.viewmodel.LoginState

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as FitLifeApplication
    val factory = AuthViewModelFactory(application.userRepository, application.userSessionManager)
    val authViewModel: AuthViewModel = viewModel(factory = factory)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val loginState by authViewModel.loginState.collectAsState()

    fun validate(): Boolean {
        var isValid = true
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
                    },
                    placeholder = "Password",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    isError = passwordError != null,
                    errorMessage = passwordError
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                        )
                        Text("Remember me", fontSize = 14.sp, color = Color(0xFF424242))
                    }
                    TextButton(onClick = { /* TODO: Forgot Password */ }) {
                        Text("Forgot Password?", color = Color(0xFF6A1B9A), fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
                
                if (loginState is LoginState.Error) {
                    Text(
                        text = (loginState as LoginState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (loginState is LoginState.Loading) {
                    CircularProgressIndicator()
                } else {
                    PrimaryButton(text = "Login", onClick = { 
                        if (validate()) {
                            authViewModel.login(email, password) 
                        }
                    })
                }
                
                LaunchedEffect(loginState) {
                    if (loginState is LoginState.Success) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                GuestButton(onClick = { 
                    authViewModel.loginAsGuest()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                    Text("Don't have an account? Sign Up", color = Color(0xFF6A1B9A), fontSize = 14.sp)
                }
            }
        }
    }
}
