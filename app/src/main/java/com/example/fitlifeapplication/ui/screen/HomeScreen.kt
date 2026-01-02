package com.example.fitlifeapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitlifeapplication.ui.components.FitLifeTopAppBar
import com.example.fitlifeapplication.ui.navigation.BottomNavItem
import com.example.fitlifeapplication.ui.navigation.Screen
import com.example.fitlifeapplication.viewmodel.AppViewModelProvider
import com.example.fitlifeapplication.viewmodel.HomeUiState
import com.example.fitlifeapplication.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by homeViewModel.homeUiState.collectAsState()

    Scaffold(
        topBar = {
            FitLifeTopAppBar(
                navController = navController,
                userName = homeUiState.userName,
                showGreeting = true
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            WeeklyProgressCard(
                onCardClick = { /* Navigate to Weekly Details if implemented */ }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Quick Stats",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.padding(start = 5.dp, bottom = 10.dp)
            )
            QuickStatsGrid(
                totalWorkouts = homeUiState.totalWorkouts,
                totalTime = homeUiState.totalTime,
                completed = homeUiState.completed,
                streakDays = homeUiState.streakDays,
                onTotalWorkoutsClick = { 
                    navController.navigate(BottomNavItem.Workouts.route) 
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Today's Workout",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.padding(start = 5.dp, bottom = 10.dp)
            )
            TodaysWorkoutCard(
                workoutName = homeUiState.todayWorkout,
                workoutTime = homeUiState.todayWorkoutTime,
                workoutDuration = homeUiState.todayWorkoutDuration
            )
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Plan your Week",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.padding(start = 5.dp, bottom = 10.dp)
            )
            // New Card to navigate to Weekly Plan
            PlanWeekCard(navController = navController)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Weekly Goal Section
            Text(
                text = "Weekly Goal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.padding(start = 5.dp, bottom = 10.dp)
            )
            WeeklyGoalCalendar(homeUiState = homeUiState)
            
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "FitLife Monthly Calendar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.padding(start = 5.dp, bottom = 10.dp)
            )
            MonthlyCalendar(
                homeUiState = homeUiState,
                onPreviousMonthClick = homeViewModel::onPreviousMonthClick,
                onNextMonthClick = homeViewModel::onNextMonthClick
            )

            Spacer(modifier = Modifier.height(20.dp))
            GestureInfoCard()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun PlanWeekCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.Routines.route) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF90CAF9)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Event, 
                        contentDescription = "Plan Week",
                        tint = Color(0xFF1565C0)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Plan Your Week", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1565C0))
                    Text("Select routines to build your weekly schedule", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun WeeklyGoalCalendar(homeUiState: HomeUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            val currentDate = LocalDate.now()
            val startOfWeek = currentDate.minusDays((currentDate.dayOfWeek.value % 7).toLong())
            
            days.forEachIndexed { index, dayName ->
                val date = startOfWeek.plusDays(index.toLong())
                val isCompleted = homeUiState.completedDays.contains(date)
                val isToday = date == currentDate
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = dayName,
                        fontSize = 12.sp,
                        color = if (isToday) Color(0xFF1565C0) else Color.Gray,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = date.dayOfMonth.toString(),
                        fontSize = 12.sp,
                        color = if (isToday) Color(0xFF1565C0) else Color.Gray,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> Color(0xFF4CAF50) // Green for done
                                    isToday -> Color(0xFFE3F2FD) // Light blue for today (pending)
                                    else -> Color(0xFFEEEEEE) // Gray for upcoming
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyCalendar(
    homeUiState: HomeUiState,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit
) {
    val month = homeUiState.displayedMonth
    val daysInMonth = homeUiState.daysInMonth
    val completedDays = homeUiState.completedDays

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with month, year, and navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onPreviousMonthClick) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
                }
                Text(
                    text = "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = onNextMonthClick) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Days of the week header
            Row(modifier = Modifier.fillMaxWidth()) {
                val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar grid
            val totalCells = 42 // 6 rows * 7 columns
            Column {
                for (i in 0 until 6) { // 6 rows for a full month display
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (j in 0 until 7) { // 7 columns
                            val index = i * 7 + j
                            if (index < daysInMonth.size) {
                                val day = daysInMonth[index]
                                Box(modifier = Modifier.weight(1f)) {
                                    if (day != null) {
                                        CalendarDay(
                                            day = day,
                                            isCompleted = completedDays.contains(day)
                                        )
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.weight(1f)) { }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDay(day: LocalDate, isCompleted: Boolean) {
    val today = LocalDate.now()
    val isToday = day == today

    Box(
        modifier = Modifier
            .aspectRatio(1f) // Makes the box a square
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        val backgroundColor = when {
            isCompleted -> Color(0xFF4CAF50) // Green
            isToday -> Color(0xFFE3F2FD) // Light blue
            else -> Color.Transparent
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable { /* Future: Show details for this day */ },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                color = if (isCompleted) Color.White else if (isToday) Color(0xFF1565C0) else Color.Black,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check, 
                    contentDescription = "Completed",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp).align(Alignment.BottomEnd)
                )
            }
        }
    }
}
