package com.example.fitlifeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapplication.data.model.DelegationLog
import com.example.fitlifeapplication.data.model.Routine
import com.example.fitlifeapplication.data.repository.DelegationLogRepository
import com.example.fitlifeapplication.data.repository.EquipmentRepository
import com.example.fitlifeapplication.data.repository.ExerciseRepository
import com.example.fitlifeapplication.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class DelegateUiState(
    val routines: List<Routine> = emptyList(),
    val friendName: String = "",
    val mobileNumber: String = "",
    val selectedRoutine: Routine? = null,
    val messagePreview: String = "",
    val isSendEnabled: Boolean = false,
    val equipmentNames: List<String> = emptyList()
)

class DelegateViewModel(
    private val routineRepository: RoutineRepository,
    private val delegationLogRepository: DelegationLogRepository,
    private val exerciseRepository: ExerciseRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DelegateUiState())
    val uiState: StateFlow<DelegateUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            routineRepository.getAllRoutines().collect { routines ->
                _uiState.value = _uiState.value.copy(routines = routines)
            }
        }
    }

    fun loadRoutine(routineId: Long) {
        viewModelScope.launch {
            val routine = routineRepository.getRoutineById(routineId).first()
            if (routine != null) {
                selectRoutine(routine)
            }
        }
    }

    fun updateFriendName(name: String) {
        _uiState.value = _uiState.value.copy(friendName = name)
        generateMessagePreview()
    }

    fun updateMobileNumber(number: String) {
        _uiState.value = _uiState.value.copy(mobileNumber = number)
        validateInput()
    }

    fun selectRoutine(routine: Routine) {
        viewModelScope.launch {
            val allExercises = exerciseRepository.getAllExercises().first()
            val allEquipment = equipmentRepository.getAllEquipment().first()
            
            val equipmentSet = mutableSetOf<String>()
            
            routine.exerciseOrder.forEach { exerciseId ->
                val exercise = allExercises.find { it.exerciseId == exerciseId }
                exercise?.equipmentIds?.forEach { equipId ->
                    val equip = allEquipment.find { it.equipmentId == equipId }
                    if (equip != null) {
                        equipmentSet.add(equip.name)
                    }
                }
            }

            // Extract equipment from description (added via CreateWorkoutScreen)
            routine.description?.lines()?.forEach { line ->
                if (line.trim().startsWith("Equipment Needed:")) {
                    val equipmentString = line.substringAfter("Equipment Needed:")
                    val items = equipmentString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    equipmentSet.addAll(items)
                }
            }
            
            _uiState.value = _uiState.value.copy(
                selectedRoutine = routine,
                equipmentNames = equipmentSet.toList()
            )
            generateMessagePreview()
            validateInput()
        }
    }

    private fun generateMessagePreview() {
        val state = _uiState.value
        val routineTitle = state.selectedRoutine?.title ?: "[Workout]"
        val friendName = if (state.friendName.isNotBlank()) " ${state.friendName}" else ""
        
        val equipmentListString = if (state.equipmentNames.isNotEmpty()) {
            state.equipmentNames.joinToString("\n") { "- $it" }
        } else {
            "- No specific equipment needed"
        }

        val message = "Hi$friendName,\nYou need to bring the following equipments for \"$routineTitle\":\n$equipmentListString\nThank you!"
        
        _uiState.value = _uiState.value.copy(messagePreview = message)
    }

    private fun validateInput() {
        val isMobileValid = _uiState.value.mobileNumber.isNotBlank()
        val isRoutineSelected = _uiState.value.selectedRoutine != null
        _uiState.value = _uiState.value.copy(isSendEnabled = isMobileValid && isRoutineSelected)
    }

    fun sendDelegation() {
        if (!_uiState.value.isSendEnabled) return

        viewModelScope.launch {
            val log = DelegationLog(
                fromUserId = 1,
                toContact = _uiState.value.mobileNumber,
                messageText = _uiState.value.messagePreview,
                sentAt = System.currentTimeMillis(),
                status = "INTENT_OPENED"
            )
            delegationLogRepository.insert(log)
        }
    }
}
