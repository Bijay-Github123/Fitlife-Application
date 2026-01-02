package com.example.fitlifeapplication.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fitlifeapplication.ui.theme.quickStatsCheck
import com.example.fitlifeapplication.ui.theme.quickStatsDumbbell
import com.example.fitlifeapplication.ui.theme.quickStatsStar
import com.example.fitlifeapplication.ui.theme.quickStatsStopwatch

@Composable
fun QuickStatsGrid(
    totalWorkouts: Int,
    totalTime: String,
    completed: Int,
    streakDays: Int,
    onTotalWorkoutsClick: (() -> Unit)? = null
) {
    Column {
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            QuickStatsCard(
                icon = Icons.Default.FitnessCenter,
                value = totalWorkouts.toString(),
                label = "Total Workouts",
                tint = quickStatsDumbbell,
                onClick = onTotalWorkoutsClick
            )
            QuickStatsCard(
                icon = Icons.Default.DateRange, 
                value = totalTime, 
                label = "Total Time", 
                tint = quickStatsStopwatch
            )
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            QuickStatsCard(
                icon = Icons.Default.Check, 
                value = completed.toString(), 
                label = "Completed", 
                tint = quickStatsCheck
            )
            QuickStatsCard(
                icon = Icons.Default.Star, 
                value = streakDays.toString(), 
                label = "Streak Days", 
                tint = quickStatsStar
            )
        }
    }
}
