package com.example.fitlifeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapplication.data.manager.UserSessionManager
import com.example.fitlifeapplication.data.model.Equipment
import com.example.fitlifeapplication.data.model.Routine
import com.example.fitlifeapplication.data.model.RoutineInstance
import com.example.fitlifeapplication.data.repository.EquipmentRepository
import com.example.fitlifeapplication.data.repository.RoutineInstanceRepository
import com.example.fitlifeapplication.data.repository.RoutineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class Workout(
    val routine: Routine,
    val instance: RoutineInstance?,
    val isCompleted: Boolean,
    val equipment: List<Equipment>
)

data class WorkoutsUiState(val workouts: List<Workout> = emptyList())

class WorkoutsViewModel(
    private val routineRepository: RoutineRepository,
    private val routineInstanceRepository: RoutineInstanceRepository,
    private val equipmentRepository: EquipmentRepository,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    val uiState: StateFlow<WorkoutsUiState> = userSessionManager.userId
        .flatMapLatest { userId ->
            if (userId != null) {
                combine(
                    routineRepository.getAllRoutines(),
                    routineInstanceRepository.getAllRoutineInstances(),
                    equipmentRepository.getAllEquipment()
                ) { routines, instances, allEquipment ->
                    val workoutList = routines.map { routine ->
                        // Filter for instance that belongs to this specific user
                        val instance = instances.find { it.routineId == routine.routineId && it.userId == userId }
                        
                        val parsedEquipmentNames = parseEquipmentFromDescription(routine.description)
                        
                        val routineEquipment = if (parsedEquipmentNames != null) {
                             parsedEquipmentNames.map { name ->
                                Equipment(name = name, category = "General", notes = null) 
                            }
                        } else {
                            allEquipment.shuffled().take(2)
                        }

                        Workout(routine, instance, instance?.completed ?: false, routineEquipment)
                    }
                    WorkoutsUiState(workoutList)
                }
            } else {
                 flowOf(WorkoutsUiState())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = WorkoutsUiState()
        )

    private fun parseEquipmentFromDescription(description: String?): List<String>? {
        if (description == null) return null
        
        val lines = description.lines()
        val equipmentLine = lines.find { it.startsWith("Equipment Needed:", ignoreCase = true) }
        
        return equipmentLine?.substringAfter(":")?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
    }

    fun markAsCompleted(workout: Workout) {
        viewModelScope.launch {
            val currentUserId = userSessionManager.userId.value ?: return@launch
            
            if (workout.instance != null) {
                val updatedInstance = workout.instance.copy(
                    completed = !workout.instance.completed,
                    completedAt = if (!workout.instance.completed) System.currentTimeMillis() else null
                )
                routineInstanceRepository.update(updatedInstance)
            } else {
                val newInstance = RoutineInstance(
                    routineId = workout.routine.routineId,
                    userId = currentUserId,
                    scheduledDate = System.currentTimeMillis(),
                    reminderTime = null,
                    completed = true,
                    completedAt = System.currentTimeMillis()
                )
                routineInstanceRepository.insert(newInstance)
            }
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            workout.instance?.let { routineInstanceRepository.delete(it) }
            routineRepository.delete(workout.routine)
        }
    }

    fun resetAllWorkouts() {
        viewModelScope.launch {
            val currentUserId = userSessionManager.userId.value ?: return@launch
            val instances = routineInstanceRepository.getAllRoutineInstances().first()
            instances.filter { it.userId == currentUserId }.forEach { instance ->
                if (instance.completed) {
                    val updatedInstance = instance.copy(
                        completed = false,
                        completedAt = null
                    )
                    routineInstanceRepository.update(updatedInstance)
                }
            }
        }
    }
}
