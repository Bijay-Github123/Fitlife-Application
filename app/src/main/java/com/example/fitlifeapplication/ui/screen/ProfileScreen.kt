package com.example.fitlifeapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitlifeapplication.data.model.User
import com.example.fitlifeapplication.ui.components.FitLifeTopAppBar
import com.example.fitlifeapplication.ui.navigation.Screen
import com.example.fitlifeapplication.viewmodel.AppViewModelProvider
import com.example.fitlifeapplication.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    Scaffold(
        topBar = {
            FitLifeTopAppBar(
                navController = navController,
                showGreeting = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBF6FB))
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Profile Header
            ProfileHeader(user)

            // 2. Delegation Settings
            DelegationSettings()

            // 3. Weekly Progress Summary
            WeeklyProgressSummary(
                completedWorkouts = uiState.completedWorkouts,
                totalWorkouts = uiState.totalWorkouts
            )

            // 4. Saved Locations
            SavedLocations()

            // 5. Expenses Overview
            ExpensesOverview()

            // 6. Fitness Integrations
            FitnessIntegrations()

            // 7. Workout Reminders
            WorkoutReminders()

            // 8. App & Account Settings
            AccountSettings(navController, onLogout)
        }
    }
}

@Composable
fun ProfileHeader(user: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(60.dp),
                    tint = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user?.displayName ?: "Guest",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Text(
                text = user?.email ?: "Guest",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Goal: Build muscle & stay active",
                fontSize = 14.sp,
                color = Color(0xFF1976D2),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DelegationSettings() {
    var smsEnabled by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Delegation Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Default Contact", fontWeight = FontWeight.Medium)
                    Text("Mark (Best Friend)", fontSize = 12.sp, color = Color.Gray)
                }
                TextButton(onClick = { /* TODO: Edit Contact */ }) {
                    Text("Edit")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable Auto-SMS Reminders")
                Switch(
                    checked = smsEnabled,
                    onCheckedChange = { smsEnabled = it }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Template Preview: \"Hi [Name], please bring [Equipment] for [Workout]...\"",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun WeeklyProgressSummary(completedWorkouts: Int, totalWorkouts: Int) {
    val percentage = if (totalWorkouts > 0) ((completedWorkouts.toFloat() / totalWorkouts) * 100).toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Weekly Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProgressStat(value = "$completedWorkouts", label = "Workouts")
                ProgressStat(value = "28", label = "Exercises")
                ProgressStat(value = "$percentage%", label = "Completed", color = Color(0xFF43A047))
            }
        }
    }
}

@Composable
fun ProgressStat(value: String, label: String, color: Color = Color.Black) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun SavedLocations() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Saved Locations",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LocationItem("University Gym", "123 Campus Dr")
            LocationItem("Nearby Park", "456 Park Ave")
            LocationItem("Yoga Studio", "789 Main St")
        }
    }
}

@Composable
fun LocationItem(name: String, address: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF1976D2))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = name, fontWeight = FontWeight.Medium)
            Text(text = address, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ExpensesOverview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Monthly Expenses",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Spent", fontWeight = FontWeight.Medium)
                Text("$120.00", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            ExpenseItem("Gym Membership", "$50.00")
            ExpenseItem("Supplements", "$70.00")
        }
    }
}

@Composable
fun ExpenseItem(name: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, fontSize = 14.sp, color = Color.Gray)
        Text(text = amount, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun FitnessIntegrations() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Integrations",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(12.dp))
            IntegrationItem(icon = Icons.Default.WatchLater, name = "Wear OS", status = "Connected")
            IntegrationItem(icon = Icons.Default.FitnessCenter, name = "Local Gym Classes", status = "Connect")
            IntegrationItem(icon = Icons.Default.Share, name = "Social Fitness", status = "Link Account")
        }
    }
}

@Composable
fun IntegrationItem(icon: ImageVector, name: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1976D2))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = name, fontWeight = FontWeight.Medium)
        }
        Text(
            text = status,
            fontSize = 12.sp,
            color = if (status == "Connected") Color(0xFF43A047) else Color.Gray,
            fontWeight = if (status == "Connected") FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun WorkoutReminders() {
    var remindersEnabled by remember { mutableStateOf(true) }
    var restDayAlerts by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Workout Reminders",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daily Workout Reminder")
                Switch(checked = remindersEnabled, onCheckedChange = { remindersEnabled = it })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Rest Day Alerts")
                Switch(checked = restDayAlerts, onCheckedChange = { restDayAlerts = it })
            }
        }
    }
}

@Composable
fun AccountSettings(navController: NavController, onLogout: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Account",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingsItem(
                icon = Icons.Default.Edit,
                title = "Edit Profile",
                onClick = { navController.navigate(Screen.EditProfile.route) }
            )
            SettingsItem(icon = Icons.Default.Notifications, title = "Notifications")
            SettingsItem(icon = Icons.Default.Lock, title = "Privacy & Permissions")
            SettingsItem(icon = Icons.Default.Info, title = "Help & About")
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Log Out", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}
