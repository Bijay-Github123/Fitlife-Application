package com.example.fitlifeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapplication.data.model.ChecklistItem
import com.example.fitlifeapplication.data.model.Routine
import com.example.fitlifeapplication.data.model.RoutineInstance
import com.example.fitlifeapplication.data.repository.ChecklistItemRepository
import com.example.fitlifeapplication.data.repository.EquipmentRepository
import com.example.fitlifeapplication.data.repository.ExerciseRepository
import com.example.fitlifeapplication.data.repository.RoutineInstanceRepository
import com.example.fitlifeapplication.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

data class WeeklyPlanUiState(
    val selectedRoutines: List<Routine> = emptyList(),
    val checklist: Map<String, List<ChecklistItem>> = emptyMap(), // Categorized: Category -> Items
    val isLoading: Boolean = false
)

class WeeklyPlanViewModel(
    private val routineRepository: RoutineRepository,
    private val routineInstanceRepository: RoutineInstanceRepository,
    private val exerciseRepository: ExerciseRepository,
    private val equipmentRepository: EquipmentRepository,
    private val checklistItemRepository: ChecklistItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyPlanUiState(isLoading = true))
    val uiState: StateFlow<WeeklyPlanUiState> = _uiState.asStateFlow()

    // Define current week range
    private val weekFields = WeekFields.of(Locale.getDefault())
    private val today = LocalDate.now()
    private val startOfWeek = today.with(TemporalAdjusters.previousOrSame(weekFields.firstDayOfWeek))
    private val endOfWeek = startOfWeek.plusDays(6)
    private val startOfWeekMillis = startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    private val endOfWeekMillis = endOfWeek.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    init {
        viewModelScope.launch {
            // Observe RoutineInstances for the current week to determine selected routines
            routineInstanceRepository.getRoutineInstancesForUser(1L) // Assuming user ID 1
                .collect { instances ->
                    val weeklyInstances = instances.filter { 
                        it.scheduledDate in startOfWeekMillis..endOfWeekMillis 
                    }
                    val routineIds = weeklyInstances.map { it.routineId }.distinct()
                    
                    val allRoutines = routineRepository.getAllRoutines().first()
                    val selectedRoutines = allRoutines.filter { it.routineId in routineIds }

                    updateChecklist(selectedRoutines)
                    
                    // Update UI State with selected routines
                    _uiState.value = _uiState.value.copy(selectedRoutines = selectedRoutines, isLoading = false)
                }
        }
        
        // Observe Checklist Items
        viewModelScope.launch {
            checklistItemRepository.getChecklistItemsForRoutine(-999L).collect { items ->
                val grouped = items.groupBy { it.category }
                _uiState.value = _uiState.value.copy(checklist = grouped)
            }
        }
    }
    
    // Explicitly expose selected routines flow for UI
    val selectedRoutinesFlow: StateFlow<List<Routine>> = routineInstanceRepository.getRoutineInstancesForUser(1L)
        .combine(routineRepository.getAllRoutines()) { instances, routines ->
             val weeklyInstances = instances.filter { 
                it.scheduledDate in startOfWeekMillis..endOfWeekMillis 
            }
            val routineIds = weeklyInstances.map { it.routineId }.distinct()
            routines.filter { it.routineId in routineIds }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun toggleSelection(routine: Routine) {
        viewModelScope.launch {
            val instances = routineInstanceRepository.getAllRoutineInstances().first()
            val existing = instances.find { 
                it.routineId == routine.routineId && 
                it.userId == 1L && 
                it.scheduledDate in startOfWeekMillis..endOfWeekMillis
            }

            if (existing != null) {
                routineInstanceRepository.delete(existing)
            } else {
                val newInstance = RoutineInstance(
                    routineId = routine.routineId,
                    userId = 1L,
                    scheduledDate = System.currentTimeMillis(),
                    reminderTime = null
                )
                routineInstanceRepository.insert(newInstance)
            }
        }
    }
    
    fun isSelected(routineId: Long): Boolean {
        // This is a synchronous check helper, might need to be reactive in UI
        return selectedRoutinesFlow.value.any { it.routineId == routineId }
    }

    private suspend fun updateChecklist(selectedRoutines: List<Routine>) {
        val requiredEquipmentIds = mutableSetOf<Long>()
        val requiredExerciseIds = mutableSetOf<Long>()
        
        val allExercises = exerciseRepository.getAllExercises().first()
        
        selectedRoutines.forEach { routine ->
            routine.exerciseOrder.forEach { exerciseId ->
                 requiredExerciseIds.add(exerciseId)
                 val exercise = allExercises.find { it.exerciseId == exerciseId }
                 exercise?.equipmentIds?.forEach { equipmentId ->
                     requiredEquipmentIds.add(equipmentId)
                 }
            }
        }
        
        val allEquipment = equipmentRepository.getAllEquipment().first()
        val requiredEquipment = allEquipment.filter { it.equipmentId in requiredEquipmentIds }
        val requiredExercises = allExercises.filter { it.exerciseId in requiredExerciseIds }
        
        val currentChecklist = checklistItemRepository.getChecklistItemsForRoutine(-999L).first()
        
        // Add missing Equipment
        requiredEquipment.forEach { equipment ->
            if (currentChecklist.none { it.equipmentId == equipment.equipmentId }) {
                checklistItemRepository.insert(
                    ChecklistItem(
                        routineId = -999L,
                        exerciseId = null,
                        equipmentId = equipment.equipmentId,
                        title = equipment.name,
                        category = equipment.category,
                        done = false,
                        quantity = 1
                    )
                )
            }
        }

        // Add missing Exercises
        requiredExercises.forEach { exercise ->
             if (currentChecklist.none { it.exerciseId == exercise.exerciseId }) {
                val details = if (exercise.sets != null || exercise.reps != null) " (${exercise.sets ?: 0} x ${exercise.reps ?: "0"})" else ""
                checklistItemRepository.insert(
                    ChecklistItem(
                        routineId = -999L,
                        exerciseId = exercise.exerciseId,
                        equipmentId = null,
                        title = "${exercise.name}$details",
                        category = "Exercises",
                        done = false,
                        quantity = exercise.sets ?: 1
                    )
                )
             }
        }
        
        // Cleanup extras
        currentChecklist.forEach { item ->
            val isEquipmentAndNeeded = item.equipmentId != null && item.equipmentId in requiredEquipmentIds
            val isExerciseAndNeeded = item.exerciseId != null && item.exerciseId in requiredExerciseIds
            
            if (!isEquipmentAndNeeded && !isExerciseAndNeeded) {
                checklistItemRepository.delete(item)
            }
        }
    }

    fun toggleCheckItem(item: ChecklistItem) {
        viewModelScope.launch {
            checklistItemRepository.update(item.copy(done = !item.done))
        }
    }
    
    fun deleteCheckItem(item: ChecklistItem) {
        viewModelScope.launch {
            checklistItemRepository.delete(item)
        }
    }

    fun markCheckItemAsDone(item: ChecklistItem) {
        viewModelScope.launch {
            if (!item.done) {
                checklistItemRepository.update(item.copy(done = true))
            }
        }
    }
    
    fun updateChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            checklistItemRepository.update(item)
        }
    }

    fun resetChecklist() {
        viewModelScope.launch {
             val currentItems = _uiState.value.checklist.values.flatten()
             currentItems.forEach { item ->
                 if (item.done) {
                    checklistItemRepository.update(item.copy(done = false))
                 }
             }
        }
    }
}
