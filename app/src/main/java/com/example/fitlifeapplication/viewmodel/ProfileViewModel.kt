package com.example.fitlifeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapplication.data.manager.UserSessionManager
import com.example.fitlifeapplication.data.model.User
import com.example.fitlifeapplication.data.repository.RoutineInstanceRepository
import com.example.fitlifeapplication.data.repository.RoutineRepository
import com.example.fitlifeapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val totalWorkouts: Int = 0,
    val completedWorkouts: Int = 0,
    val exercisesCount: Int = 0 // Placeholder or calculated
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val routineRepository: RoutineRepository,
    private val routineInstanceRepository: RoutineInstanceRepository,
    private val userSessionManager: UserSessionManager
) : ViewModel() {
    
    val uiState: StateFlow<ProfileUiState> = userSessionManager.userId
        .flatMapLatest { userId ->
            if (userId != null) {
                combine(
                    userRepository.getUserById(userId),
                    routineRepository.getAllRoutines(),
                    routineInstanceRepository.getAllRoutineInstances()
                ) { currentUser, routines, instances ->
                    
                    // Total workouts count based on "My Workouts" list (all routines created)
                    val totalWorkouts = routines.size
                    
                    // Completed workouts: How many of these routines have been marked as completed.
                    val completedCount = routines.count { routine ->
                        instances.any { instance -> 
                            instance.routineId == routine.routineId && instance.completed
                        }
                    }
                    
                    ProfileUiState(
                        user = currentUser,
                        totalWorkouts = totalWorkouts,
                        completedWorkouts = completedCount,
                        exercisesCount = 28 // Keeping hardcoded for now or fetch if needed
                    )
                }
            } else {
                flowOf(ProfileUiState())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileUiState()
        )
    
    // Kept for backward compatibility if accessed directly, though uiState is preferred
    val user: StateFlow<User?> = combine(uiState) { state -> state[0].user }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null
    )

    fun updateUser(displayName: String, email: String) {
        viewModelScope.launch {
            uiState.value.user?.let { currentUser ->
                val updatedUser = currentUser.copy(displayName = displayName, email = email)
                userRepository.update(updatedUser)
            }
        }
    }
}
