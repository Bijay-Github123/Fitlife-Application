package com.example.fitlifeapplication.ui.screen

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitlifeapplication.ui.components.FitLifeTopAppBar
import com.example.fitlifeapplication.ui.navigation.Screen
import com.example.fitlifeapplication.viewmodel.AppViewModelProvider
import com.example.fitlifeapplication.viewmodel.Workout
import com.example.fitlifeapplication.viewmodel.WorkoutsViewModel
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    navController: NavController,
    workoutsViewModel: WorkoutsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by workoutsViewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedWorkout by remember { mutableStateOf<Workout?>(null) }
    
    // State for Dialogs
    var showDeleteDialog by remember { mutableStateOf(false) }
    var workoutToDelete by remember { mutableStateOf<Workout?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Shake Detection Logic
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        var acceleration = 0f
        var currentAcceleration = SensorManager.GRAVITY_EARTH
        var lastAcceleration = SensorManager.GRAVITY_EARTH
        
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    
                    lastAcceleration = currentAcceleration
                    currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                    val delta = currentAcceleration - lastAcceleration
                    acceleration = acceleration * 0.9f + delta
                    
                    if (acceleration > 12) { // Shake threshold
                        showResetDialog = true
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = "Reset Workout List") },
            text = { Text("Are you sure want to reset workout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        workoutsViewModel.resetAllWorkouts()
                        showResetDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && workoutToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false 
                workoutToDelete = null
            },
            title = { Text(text = "Delete Workout") },
            text = { Text("Are you sure want to delete workout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        workoutToDelete?.let {
                            workoutsViewModel.deleteWorkout(it)
                        }
                        showDeleteDialog = false
                        workoutToDelete = null
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteDialog = false 
                        workoutToDelete = null
                    }
                ) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            FitLifeTopAppBar(
                navController = navController,
                showGreeting = false
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBF6FB))
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) { 
            item {
                Text(
                    text = "My Workouts",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
            }
            items(uiState.workouts, key = { it.routine.routineId }) { workout ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) { // Delete
                            workoutToDelete = workout
                            showDeleteDialog = true
                            false // Don't dismiss yet, wait for dialog
                        } else if (it == SwipeToDismissBoxValue.StartToEnd) { // Mark as done
                            workoutsViewModel.markAsCompleted(workout)
                            false 
                        } else {
                            false
                        }
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = { SwipeBackground(dismissState) },
                    content = {
                        WorkoutCard(
                            workout = workout, 
                            onDoneClick = { workoutsViewModel.markAsCompleted(workout) },
                            onClick = { 
                                // Navigate to Details (Placeholder)
                            },
                            onLongClick = {
                                selectedWorkout = workout
                                showBottomSheet = true
                            }
                        )
                    }
                )
            }
        }

        if (showBottomSheet && selectedWorkout != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                containerColor = Color.White
            ) {
                WorkoutOptionsSheetContent(
                    workout = selectedWorkout!!,
                    onDismiss = { showBottomSheet = false },
                    onDelete = {
                        // Close sheet then show dialog
                        showBottomSheet = false
                        workoutToDelete = selectedWorkout
                        showDeleteDialog = true
                    },
                    onMarkDone = {
                        workoutsViewModel.markAsCompleted(selectedWorkout!!)
                        showBottomSheet = false
                    },
                    onEdit = {
                        showBottomSheet = false
                        navController.navigate(Screen.EditWorkout.createRoute(selectedWorkout!!.routine.routineId))
                    },
                    onDelegate = {
                        showBottomSheet = false
                        navController.navigate(Screen.Delegate.createRoute(selectedWorkout!!.routine.routineId))
                    },
                    onSchedule = {
                        showBottomSheet = false
                        navController.navigate(Screen.Routines.route)
                    }
                )
            }
        }
    }
}

@Composable
fun WorkoutOptionsSheetContent(
    workout: Workout,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onMarkDone: () -> Unit,
    onEdit: () -> Unit,
    onDelegate: () -> Unit,
    onSchedule: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = workout.routine.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HorizontalDivider()
        
        ListItem(
            headlineContent = { Text("Start Workout") },
            leadingContent = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF1976D2)) },
            modifier = Modifier.clickable { onDismiss() }
        )
        ListItem(
            headlineContent = { Text(if (workout.isCompleted) "Mark as Not Done" else "Mark as Done") },
            leadingContent = { Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32)) },
            modifier = Modifier.clickable { onMarkDone() }
        )
        ListItem(
            headlineContent = { Text("Edit Workout") },
            leadingContent = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier.clickable { onEdit() }
        )
        ListItem(
            headlineContent = { Text("Delegate Item") },
            leadingContent = { Icon(Icons.Default.Send, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier.clickable { onDelegate() }
        )
        ListItem(
            headlineContent = { Text("Schedule / Add to Week") },
            leadingContent = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier.clickable { onSchedule() }
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text("Delete Routine", color = Color.Red) },
            leadingContent = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
            modifier = Modifier.clickable { onDelete() }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val direction = dismissState.dismissDirection
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            SwipeToDismissBoxValue.StartToEnd -> Color(0xFF2E7D32) // Green for Done
            SwipeToDismissBoxValue.EndToStart -> Color.Red // Red for Delete
            else -> Color.Transparent
        },
        label = "Swipe Background Color"
    )
    val alignment = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.Center
    }
    val icon = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Check
        SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
        else -> null
    }
    val scale by animateFloatAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
        label = "Swipe Icon Scale"
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        icon?.let {
            Icon(it, contentDescription = "Swipe Action", modifier = Modifier.scale(scale), tint = Color.White)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun WorkoutCard(
    workout: Workout, 
    onDoneClick: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            StatusIcon(isCompleted = workout.isCompleted)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.routine.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textDecoration = if (workout.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = workout.routine.description ?: "",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Using FlowRow for dynamic wrapping of equipment chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    workout.equipment.take(3).forEach { equipment ->
                        EquipmentChip(name = equipment.name)
                    }
                }
            }
            if (!workout.isCompleted) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDoneClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.heightIn(min = 36.dp)
                ) {
                    Text("Done", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StatusIcon(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (isCompleted) Color(0xFF66BB6A) else Color(0xFF42A5F5)),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(Icons.Default.Check, contentDescription = "Completed", tint = Color.White)
        } else {
            Icon(Icons.Default.FitnessCenter, contentDescription = "Pending", tint = Color.White)
        }
    }
}

@Composable
fun EquipmentChip(name: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        color = Color(0xFFF5F5F5)
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
