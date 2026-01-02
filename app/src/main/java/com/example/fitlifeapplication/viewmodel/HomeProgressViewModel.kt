package com.example.fitlifeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapplication.data.manager.UserSessionManager
import com.example.fitlifeapplication.data.repository.RoutineInstanceRepository
import com.example.fitlifeapplication.data.repository.RoutineRepository
import com.example.fitlifeapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

data class HomeProgressUiState(
    val totalScheduled: Int = 0,
    val totalCompleted: Int = 0,
    val percentCompleted: Int = 0,
    val percentRemaining: Int = 0,
    val isLoading: Boolean = true
)

class HomeProgressViewModel(
    private val routineInstanceRepository: RoutineInstanceRepository,
    private val userRepository: UserRepository,
    private val routineRepository: RoutineRepository,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    // Get current week start and end in millis (kept for reference or future use if we revert to weekly specific)
    private val weekFields = WeekFields.of(Locale.getDefault())
    private val today = LocalDate.now()
    private val startOfWeek = today.with(TemporalAdjusters.previousOrSame(weekFields.firstDayOfWeek))
    private val endOfWeek = startOfWeek.plusDays(6)
    
    private val startOfWeekMillis = startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    private val endOfWeekMillis = endOfWeek.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val uiState: StateFlow<HomeProgressUiState> = userSessionManager.userId
        .flatMapLatest { userId ->
            if (userId != null) {
                combine(
                    routineRepository.getAllRoutines(),
                    routineInstanceRepository.getAllRoutineInstances()
                ) { routines, instances ->
                    // Total workouts count based on "My Workouts" list (all routines created)
                    val totalWorkouts = routines.size
                    
                    // Completed workouts: How many of these routines have been marked as completed.
                    // We check if there's any completed instance for this routine associated with the *current user*.
                    val completedCount = routines.count { routine ->
                        instances.any { instance -> 
                            instance.routineId == routine.routineId && 
                            instance.userId == userId && 
                            instance.completed
                        }
                    }
                    
                    val percent = if (totalWorkouts > 0) ((completedCount.toFloat() / totalWorkouts) * 100).toInt() else 0
                    HomeProgressUiState(
                        totalScheduled = totalWorkouts,
                        totalCompleted = completedCount,
                        percentCompleted = percent,
                        percentRemaining = 100 - percent,
                        isLoading = false
                    )
                }
            } else {
                 flowOf(HomeProgressUiState(isLoading = false))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeProgressUiState()
        )
}
