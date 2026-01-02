package com.example.fitlifeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapplication.data.manager.UserSessionManager
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
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

class HomeViewModel(
    userRepository: UserRepository,
    private val routineInstanceRepository: RoutineInstanceRepository,
    private val routineRepository: RoutineRepository,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _displayedMonth = MutableStateFlow(YearMonth.now())

    val homeUiState: StateFlow<HomeUiState> = userSessionManager.userId
        .flatMapLatest { userId ->
            if (userId != null) {
                combine(
                    userRepository.getUserById(userId),
                    routineInstanceRepository.getAllRoutineInstances(),
                    routineRepository.getAllRoutines(),
                    _displayedMonth
                ) { currentUser, routineInstances, allRoutines, month ->
                    
                    val userInstances = routineInstances.filter { it.userId == currentUser?.userId }
                    val completedCount = userInstances.count { it.completed }
                    val totalRoutinesCount = allRoutines.size
                    val totalThisWeek = userInstances.size
                    val progress = if (totalThisWeek > 0) completedCount.toFloat() / totalThisWeek.toFloat() else 0f

                    val todayInstance = userInstances.find { !it.completed }
                    val todayRoutine = todayInstance?.let { instance -> allRoutines.find { it.routineId == instance.routineId } }

                    // Monthly Calendar Data
                    val daysInMonth = getDaysInMonth(month)
                    val completedDays = userInstances
                        .filter { it.completed && it.completedAt != null }
                        .map { Instant.ofEpochMilli(it.completedAt!!).atZone(ZoneId.systemDefault()).toLocalDate() }
                        .toSet()

                    HomeUiState(
                        userName = currentUser?.displayName ?: "User",
                        weeklyProgress = progress,
                        workoutsCompleted = completedCount,
                        workoutsTotal = totalThisWeek,
                        totalWorkouts = totalRoutinesCount,
                        completed = completedCount,
                        totalTime = "8h 30m",
                        streakDays = 28,
                        todayWorkout = todayRoutine?.title ?: "HIIT Cardio Session",
                        todayWorkoutTime = "4:30 PM - University Gym",
                        todayWorkoutDuration = "30 min",
                        
                        // Monthly Calendar
                        displayedMonth = month,
                        daysInMonth = daysInMonth,
                        completedDays = completedDays
                    )
                }
            } else {
                 flowOf(HomeUiState(userName = "Guest"))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeUiState()
        )

    fun onPreviousMonthClick() {
        _displayedMonth.value = _displayedMonth.value.minusMonths(1)
    }

    fun onNextMonthClick() {
        _displayedMonth.value = _displayedMonth.value.plusMonths(1)
    }

    private fun getDaysInMonth(yearMonth: YearMonth): List<LocalDate?> {
        val firstDayOfMonth = yearMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday, 1 for Monday...
        val daysInMonth = yearMonth.lengthOfMonth()
        
        val days = mutableListOf<LocalDate?>()
        for (i in 0 until firstDayOfWeek) {
            days.add(null) // Add null for empty days at the start
        }
        for (i in 1..daysInMonth) {
            days.add(yearMonth.atDay(i))
        }
        return days
    }
}

data class HomeUiState(
    val userName: String = "",
    val weeklyProgress: Float = 0.0f,
    val workoutsCompleted: Int = 0,
    val workoutsTotal: Int = 0,
    val totalWorkouts: Int = 0,
    val totalTime: String = "",
    val completed: Int = 0,
    val streakDays: Int = 0,
    val todayWorkout: String = "",
    val todayWorkoutTime: String = "",
    val todayWorkoutDuration: String = "",
    
    // Monthly Calendar State
    val displayedMonth: YearMonth = YearMonth.now(),
    val daysInMonth: List<LocalDate?> = emptyList(),
    val completedDays: Set<LocalDate> = emptySet()
)
