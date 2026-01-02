package com.example.fitlifeapplication.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitlifeapplication.ui.components.FitLifeTopAppBar
import com.example.fitlifeapplication.ui.navigation.Screen
import com.example.fitlifeapplication.ui.theme.horizontalProgressBarBlue
import com.example.fitlifeapplication.viewmodel.AppViewModelProvider
import com.example.fitlifeapplication.viewmodel.CreateWorkoutViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    navController: NavController,
    createWorkoutViewModel: CreateWorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory),
    sharedText: String? = null,
    routineId: Long? = null
) {
    val uiState = createWorkoutViewModel.uiState
    val scope = rememberCoroutineScope()

    LaunchedEffect(sharedText) {
        sharedText?.let {
            createWorkoutViewModel.prefillFromSharedText(it)
        }
    }

    LaunchedEffect(routineId) {
        routineId?.let {
            createWorkoutViewModel.loadRoutine(it)
        }
    }

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
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(if (uiState.isEditing) "Edit Workout" else "Create New Workout", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.workoutName,
                onValueChange = { createWorkoutViewModel.updateWorkoutName(it) },
                label = { Text("Workout Name *") },
                leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { createWorkoutViewModel.updateDescription(it) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text("Equipment Needed *", modifier = Modifier.align(Alignment.Start))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = uiState.equipment,
                    onValueChange = { createWorkoutViewModel.updateEquipment(it) },
                    label = { Text("e.g., Dumbbells, Yoga Mat") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /* TODO: Add equipment chips */ }) {
                    Text("Add")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("Instructions (Optional)", modifier = Modifier.align(Alignment.Start))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = uiState.instructions,
                    onValueChange = { createWorkoutViewModel.updateInstructions(it) },
                    label = { Text("e.g., 3 sets of 10 reps") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /* TODO: Add instruction lines */ }) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp)) // Added space above the button

            Button(
                onClick = {
                    scope.launch {
                        createWorkoutViewModel.saveWorkout()
                        navController.navigate(Screen.Workouts.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                },
                enabled = uiState.isSaveEnabled,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = horizontalProgressBarBlue)
            ) {
                Text(if (uiState.isEditing) "Update Workout" else "Save Workout", fontSize = 16.sp)
            }
        }
    }
}
