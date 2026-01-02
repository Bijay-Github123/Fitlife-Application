package com.example.fitlifeapplication.ui.screen

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlifeapplication.data.model.ChecklistItem
import com.example.fitlifeapplication.data.model.Routine
import com.example.fitlifeapplication.viewmodel.WeeklyPlanUiState
import com.example.fitlifeapplication.viewmodel.WeeklyPlanViewModel
import kotlin.math.sqrt

@Composable
fun WeeklyPlanSheet(
    viewModel: WeeklyPlanViewModel,
    onClose: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDelegationDialog by remember { mutableStateOf(false) }
    var showResetConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Shake Detection
    ShakeDetector {
        showResetConfirmationDialog = true
    }

    if (showResetConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmationDialog = false },
            title = { Text("Reset Workout List") },
            text = { Text("Are you sure want to reset?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetChecklist()
                        Toast.makeText(context, "List Reset!", Toast.LENGTH_SHORT).show()
                        showResetConfirmationDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetConfirmationDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .background(Color(0xFFFBF6FB))
            .padding(16.dp)
    ) {
        // Header
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Divider(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Weekly Workout Plan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "Shake device to reset list",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            TextButton(onClick = { showDelegationDialog = true }) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delegate")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Weekly Plan Summary
            item {
                WeeklyPlanSummary(uiState.selectedRoutines)
            }

            // Section 2: Equipment Checklist
            item {
                Text(
                    text = "Equipment Checklist",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                Text(
                    text = "Swipe Left to Delete • Swipe Right to Complete",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Categories
            uiState.checklist.forEach { (category, items) ->
                item {
                    ChecklistCategory(
                        category = category,
                        items = items,
                        onToggleItem = { viewModel.toggleCheckItem(it) },
                        onDeleteItem = { viewModel.deleteCheckItem(it) },
                        onMarkDone = { viewModel.markCheckItemAsDone(it) }
                    )
                }
            }
            
            // Padding at bottom
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    if (showDelegationDialog) {
        DelegationDialog(
            checklist = uiState.checklist,
            onDismiss = { showDelegationDialog = false }
        )
    }
}

@Composable
fun DelegationDialog(
    checklist: Map<String, List<ChecklistItem>>,
    onDismiss: () -> Unit
) {
    var mobileNumber by remember { mutableStateOf("") }
    var friendName by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    // Contact Picker
    val contactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val projection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )
                try {
                    val cursor = context.contentResolver.query(uri, projection, null, null, null)
                    cursor?.use { c ->
                        if (c.moveToFirst()) {
                            val nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                            val numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            val name = if (nameIndex >= 0) c.getString(nameIndex) else ""
                            val number = if (numberIndex >= 0) c.getString(numberIndex) else ""
                            
                            friendName = name
                            mobileNumber = number
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error getting contact: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val generatedMessage = remember(checklist, friendName) {
        buildString {
            append("Hey ${if (friendName.isNotBlank()) friendName else "there"}, here's what we need for this week's workouts:\n\n")
            
            checklist.forEach { (category, items) ->
                if (items.isNotEmpty()) {
                    append("*$category:*\n")
                    items.forEach { item ->
                        append("- ${item.title}")
                        // Mock logic for gym responsibility
                        if (category == "Strength") {
                            append(" (Gym provides)\n")
                        } else {
                            append(" (Please bring)\n")
                        }
                    }
                    append("\n")
                }
            }
            append("See you there!")
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delegate Checklist") },
        text = {
            Column {
                OutlinedTextField(
                    value = friendName,
                    onValueChange = { friendName = it },
                    label = { Text("Friend's Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = { Text("Mobile Number") },
                    trailingIcon = {
                        IconButton(onClick = { 
                            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                            contactLauncher.launch(intent)
                        }) {
                            Icon(Icons.Default.ContactPhone, contentDescription = "Pick Contact", tint = Color(0xFF2E7D32))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Message Preview:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE)),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)
                ) {
                    LazyColumn(modifier = Modifier.padding(8.dp)) {
                         item {
                            Text(generatedMessage, fontSize = 12.sp)
                         }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("smsto:$mobileNumber")
                        putExtra("sms_body", generatedMessage)
                    }
                    try {
                        context.startActivity(intent)
                        onDismiss()
                    } catch (e: Exception) {
                        Toast.makeText(context, "No SMS app found", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = mobileNumber.isNotBlank()
            ) {
                Text("Send SMS")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun WeeklyPlanSummary(routines: List<Routine>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Selected Workouts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            if (routines.isEmpty()) {
                Text("No workouts selected for this week.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            } else {
                routines.forEach { routine ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(getCategoryColor(routine.title))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = routine.title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text(
                                text = inferCategory(routine.title), 
                                style = MaterialTheme.typography.bodySmall, 
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChecklistCategory(
    category: String,
    items: List<ChecklistItem>,
    onToggleItem: (ChecklistItem) -> Unit,
    onDeleteItem: (ChecklistItem) -> Unit,
    onMarkDone: (ChecklistItem) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF424242)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation),
                    tint = Color.Gray
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Divider(color = Color(0xFFEEEEEE))
                    items.forEach { item ->
                        SwipeableChecklistItem(
                            item = item, 
                            onToggle = { onToggleItem(item) },
                            onDelete = { onDeleteItem(item) },
                            onMarkDone = { onMarkDone(item) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableChecklistItem(
    item: ChecklistItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onMarkDone: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Workout") },
            text = { Text("Are you sure want to delete ?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onMarkDone()
                    false // Don't dismiss, just mark done
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    showDeleteDialog = true
                    false // Don't dismiss immediately, wait for confirmation
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50) // Green for Done
                SwipeToDismissBoxValue.EndToStart -> Color(0xFFEF5350) // Red for Delete
                else -> Color.Transparent
            }
            val alignment = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val icon = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Done
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                else -> Icons.Default.Delete
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        content = {
            ChecklistItemRow(item = item, onToggle = onToggle)
        }
    )
}

@Composable
fun ChecklistItemRow(item: ChecklistItem, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.done,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (item.done) Color.Gray else Color.Black,
            textDecoration = if (item.done) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
        )
    }
}

@Composable
fun ShakeDetector(onShake: () -> Unit) {
    val context = LocalContext.current
    val currentOnShake by rememberUpdatedState(onShake)

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        var acceleration = 0f
        var currentAcceleration = SensorManager.GRAVITY_EARTH
        var lastAcceleration = SensorManager.GRAVITY_EARTH

        val listener = object : SensorEventListener {
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
                        currentOnShake()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}

fun inferCategory(title: String): String {
    return when {
        title.contains("Yoga", ignoreCase = true) -> "Yoga"
        title.contains("Cardio", ignoreCase = true) -> "Cardio"
        title.contains("HIIT", ignoreCase = true) -> "Cardio"
        title.contains("Strength", ignoreCase = true) -> "Strength"
        title.contains("Pilates", ignoreCase = true) -> "Pilates"
        else -> "General"
    }
}

fun getCategoryColor(title: String): Color {
    return when {
        title.contains("Yoga", ignoreCase = true) -> Color(0xFFAB47BC) // Purple
        title.contains("Cardio", ignoreCase = true) -> Color(0xFFEF5350) // Red
        title.contains("Strength", ignoreCase = true) -> Color(0xFFFFA726) // Orange
        else -> Color(0xFF42A5F5) // Blue
    }
}
