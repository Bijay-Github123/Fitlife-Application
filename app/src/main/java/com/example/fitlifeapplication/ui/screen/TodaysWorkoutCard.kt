package com.example.fitlifeapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitlifeapplication.ui.theme.appLogoCircleBackground
import com.example.fitlifeapplication.ui.theme.dumbbellIcon

@Composable
fun TodaysWorkoutCard(
    workoutName: String,
    workoutTime: String,
    workoutDuration: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(appLogoCircleBackground)
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = "Workout Icon", tint = dumbbellIcon)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(workoutName, style = MaterialTheme.typography.titleMedium)
                Text(workoutTime, style = MaterialTheme.typography.bodyMedium)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Transparent, shape = RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(workoutDuration, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
