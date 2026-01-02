package com.example.fitlifeapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapplication.data.model.Routine
import com.example.fitlifeapplication.data.repository.RoutineRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CreateWorkoutUiState(
    val workoutName: String = "",
    val description: String = "",
    val equipment: String = "",
    val instructions: String = "",
    val isSaveEnabled: Boolean = false,
    val isEditing: Boolean = false,
    val routineId: Long? = null
)

class CreateWorkoutViewModel(private val routineRepository: RoutineRepository) : ViewModel() {

    var uiState by mutableStateOf(CreateWorkoutUiState())
        private set

    fun loadRoutine(routineId: Long) {
        viewModelScope.launch {
            val routine = routineRepository.getRoutineById(routineId).first()
            if (routine != null) {
                // Parse the combined description back into separate fields
                val (parsedDescription, parsedEquipment, parsedInstructions) = parseCombinedDescription(routine.description)
                
                uiState = uiState.copy(
                    workoutName = routine.title,
                    description = parsedDescription,
                    equipment = parsedEquipment,
                    instructions = parsedInstructions,
                    isEditing = true,
                    routineId = routineId
                )
                validateInput()
            }
        }
    }

    private fun parseCombinedDescription(combined: String?): Triple<String, String, String> {
        if (combined.isNullOrBlank()) return Triple("", "", "")

        var description = combined
        var equipment = ""
        var instructions = ""

        // Extract Instructions
        if (description.contains("Instructions:")) {
            val parts = description.split("Instructions:")
            if (parts.size > 1) {
                instructions = parts[1].trim()
                description = parts[0].trim()
            }
        }

        // Extract Equipment Needed
        if (description.contains("Equipment Needed:")) {
            val parts = description.split("Equipment Needed:")
            if (parts.size > 1) {
                equipment = parts[1].trim()
                description = parts[0].trim()
            }
        }

        return Triple(description.trim(), equipment, instructions)
    }

    fun updateWorkoutName(name: String) {
        uiState = uiState.copy(workoutName = name)
        validateInput()
    }

    fun updateDescription(description: String) {
        uiState = uiState.copy(description = description)
        validateInput()
    }

    fun updateEquipment(equipment: String) {
        uiState = uiState.copy(equipment = equipment)
        validateInput()
    }

    fun updateInstructions(instructions: String) {
        uiState = uiState.copy(instructions = instructions)
    }

    fun prefillFromSharedText(text: String) {
        uiState = uiState.copy(description = text)
    }

    private fun validateInput() {
        val isNameValid = uiState.workoutName.isNotBlank()
        // Equipment is not strictly required if description is there, but let's keep name required
        uiState = uiState.copy(isSaveEnabled = isNameValid)
    }

    fun saveWorkout() {
        if (!uiState.isSaveEnabled) return

        viewModelScope.launch {
            val combinedDescription = buildString {
                if (uiState.description.isNotBlank()) append(uiState.description)
                if (uiState.equipment.isNotBlank()) {
                    if (this.isNotEmpty()) append("\n\n")
                    append("Equipment Needed: ${uiState.equipment}")
                }
                if (uiState.instructions.isNotBlank()) {
                    if (this.isNotEmpty()) append("\n\n")
                    append("Instructions: ${uiState.instructions}")
                }
            }

            if (uiState.isEditing && uiState.routineId != null) {
                val updatedRoutine = Routine(
                    routineId = uiState.routineId!!,
                    title = uiState.workoutName,
                    description = combinedDescription,
                    ownerUserId = 1L,
                    exerciseOrder = emptyList() // Preserving empty list for manual entries for now
                )
                routineRepository.update(updatedRoutine)
            } else {
                val newRoutine = Routine(
                    title = uiState.workoutName,
                    description = combinedDescription,
                    ownerUserId = 1L,
                    exerciseOrder = emptyList()
                )
                routineRepository.insert(newRoutine)
            }
        }
    }
}
