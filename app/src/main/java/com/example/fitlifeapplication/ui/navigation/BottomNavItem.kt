package com.example.fitlifeapplication.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val screenRoute: String = route
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Workouts : BottomNavItem("workouts", Icons.Default.DateRange, "Workouts")
    object Create : BottomNavItem("create", Icons.Default.Create, "Create")
    object Delegate : BottomNavItem("delegate?routineId=-1", Icons.Default.Send, "Delegate", "delegate?routineId={routineId}")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}
