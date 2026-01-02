package com.example.fitlifeapplication.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlifeapplication.ui.theme.circularProgressGreen
import com.example.fitlifeapplication.ui.theme.circularProgressOrange
import com.example.fitlifeapplication.ui.theme.horizontalProgressBarBlue
import com.example.fitlifeapplication.ui.theme.horizontalProgressBarGray
import com.example.fitlifeapplication.ui.theme.weeklyProgressCardBackground
import com.example.fitlifeapplication.viewmodel.AppViewModelProvider
import com.example.fitlifeapplication.viewmodel.HomeProgressViewModel

@Composable
fun WeeklyProgressCard(
    viewModel: HomeProgressViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onCardClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Animation states
    val animatedPercentCompleted by animateFloatAsState(
        targetValue = uiState.percentCompleted.toFloat(),
        animationSpec = tween(durationMillis = 1000), 
        label = "completed"
    )
    val animatedPercentRemaining by animateFloatAsState(
        targetValue = uiState.percentRemaining.toFloat(),
        animationSpec = tween(durationMillis = 1000), 
        label = "remaining"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = weeklyProgressCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Weekly Progress",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Completed Donut
                DonutChart(
                    percentage = animatedPercentCompleted,
                    color = circularProgressGreen,
                    label = "Completed"
                )
                Spacer(modifier = Modifier.width(24.dp))
                // Remaining Donut
                DonutChart(
                    percentage = animatedPercentRemaining,
                    color = circularProgressOrange,
                    label = "Remaining"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Horizontal Progress Bar Section
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.totalCompleted} of ${uiState.totalScheduled} workouts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = "${uiState.percentCompleted}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = horizontalProgressBarBlue
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedPercentCompleted / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = horizontalProgressBarBlue,
                    trackColor = horizontalProgressBarGray,
                )
            }
        }
    }
}

@Composable
fun DonutChart(
    percentage: Float,
    color: Color,
    label: String,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size)
        ) {
            Canvas(modifier = Modifier.size(size)) {
                // Background circle (optional, usually implied by empty space or light track)
                // drawCircle(color = Color(0xFFEEEEEE), style = Stroke(strokeWidth.toPx()))

                // Progress arc
                val sweepAngle = (percentage / 100) * 360
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
                    size = Size(size.toPx(), size.toPx())
                )
            }
            Text(
                text = "${percentage.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF9E9E9E)
        )
    }
}
