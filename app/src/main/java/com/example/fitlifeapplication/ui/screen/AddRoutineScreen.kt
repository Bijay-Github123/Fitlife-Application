package com.example.fitlifeapplication.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitlifeapplication.data.model.Routine
import com.example.fitlifeapplication.viewmodel.RoutinesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoutineScreen(
    navController: NavController,
    routinesViewModel: RoutinesViewModel,
    sharedText: String? = null
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf(sharedText ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add a new Routine") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Routine Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Routine Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    // Assuming the logged-in user's ID is 1L for now.
                    // A proper implementation would get this from an AuthViewModel or a user state manager.
                    val newRoutine = Routine(
                        title = name,
                        description = description,
                        ownerUserId = 1L, // Defaulting to user 1
                        exerciseOrder = emptyList() // Defaulting to an empty list
                    )
                    routinesViewModel.addRoutine(newRoutine)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Routine")
            }
        }
    }
}
