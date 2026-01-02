package com.example.fitlifeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapplication.data.model.Routine
import com.example.fitlifeapplication.data.repository.RoutineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoutinesViewModel(private val routineRepository: RoutineRepository) : ViewModel() {

    val routinesUiState: StateFlow<RoutinesUiState> = 
        routineRepository.getAllRoutines().map { RoutinesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = RoutinesUiState()
            )

    fun addRoutine(routine: Routine) {
        viewModelScope.launch {
            routineRepository.insert(routine)
        }
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            routineRepository.delete(routine)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class RoutinesUiState(val routineList: List<Routine> = listOf())
