package com.example.fitlifeapplication.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitlifeapplication.R
import com.example.fitlifeapplication.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, nextDestination: String) {
    var startAnimation by remember { mutableStateOf(false) }
    
    // Scale animation for the logo
    val scale = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = OvershootInterpolator(2f)::getInterpolation
        ),
        label = "Logo Scale"
    )

    // Alpha animation for text
    val alpha = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 500
        ),
        label = "Text Alpha"
    )

    // Infinite pulse animation for the loading indicator (logo itself)
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse Scale"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000) // 3 seconds delay
        navController.navigate(nextDestination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3)), // Bright Blue Background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Logo
            Icon(
                painter = painterResource(id = R.drawable.ic_dumbbell), // Your App Logo
                contentDescription = "FitLife Logo",
                tint = Color.White,
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value * (if (startAnimation) pulseScale else 1f))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Text
            Text(
                text = "FitLife",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = alpha.value)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Personal Fitness Planner",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = alpha.value * 0.8f)
            )
        }
    }
}

// Interpolator for the bounce effect
class OvershootInterpolator(private val tension: Float = 2.0f) {
    fun getInterpolation(t: Float): Float {
        var t = t
        t -= 1.0f
        return t * t * ((tension + 1) * t + tension) + 1.0f
    }
}
