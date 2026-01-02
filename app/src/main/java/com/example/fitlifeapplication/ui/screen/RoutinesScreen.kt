package com.example.fitlifeapplication.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlifeapplication.data.model.Routine
import com.example.fitlifeapplication.viewmodel.AppViewModelProvider
import com.example.fitlifeapplication.viewmodel.RoutinesViewModel
import com.example.fitlifeapplication.viewmodel.WeeklyPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    routinesViewModel: RoutinesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    weeklyPlanViewModel: WeeklyPlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by routinesViewModel.routinesUiState.collectAsState()
    val selectedRoutines by weeklyPlanViewModel.selectedRoutinesFlow.collectAsState()
    
    var showWeeklyPlanSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse Workouts", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF6FB))
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showWeeklyPlanSheet = true },
                icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                text = { Text("Weekly Plan") },
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBF6FB))
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
        ) {
            items(uiState.routineList) { routine ->
                val isSelected = selectedRoutines.any { it.routineId == routine.routineId }
                
                RoutineBrowserCard(
                    routine = routine,
                    isSelected = isSelected,
                    onToggleSelection = { weeklyPlanViewModel.toggleSelection(routine) }
                )
            }
        }
        
        if (showWeeklyPlanSheet) {
            ModalBottomSheet(
                onDismissRequest = { showWeeklyPlanSheet = false },
                containerColor = Color(0xFFFBF6FB),
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
            ) {
                WeeklyPlanSheet(
                    viewModel = weeklyPlanViewModel,
                    onClose = { showWeeklyPlanSheet = false }
                )
            }
        }
    }
}

@Composable
fun RoutineBrowserCard(
    routine: Routine,
    isSelected: Boolean,
    onToggleSelection: () -> Unit
) {
    val borderColor by animateColorAsState(targetValue = if (isSelected) Color(0xFF2E7D32) else Color.Transparent)
    val backgroundColor = Color.White

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelected) BorderStroke(2.dp, borderColor) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFF1976D2))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = routine.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = inferCategory(routine.title), // Reuse helper
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                IconButton(
                    onClick = onToggleSelection,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F5F5))
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isSelected) "Remove from Plan" else "Add to Plan",
                        tint = if (isSelected) Color(0xFF2E7D32) else Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = routine.description ?: "No description",
                fontSize = 14.sp,
                color = Color(0xFF616161),
                maxLines = 2
            )
        }
    }
}
