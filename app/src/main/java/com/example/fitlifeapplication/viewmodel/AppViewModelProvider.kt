package com.example.fitlifeapplication.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitlifeapplication.FitLifeApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                fitLifeApplication().userRepository,
                fitLifeApplication().routineInstanceRepository,
                fitLifeApplication().routineRepository,
                fitLifeApplication().userSessionManager
            )
        }

        // Initializer for HomeProgressViewModel
        initializer {
            HomeProgressViewModel(
                fitLifeApplication().routineInstanceRepository,
                fitLifeApplication().userRepository,
                fitLifeApplication().routineRepository,
                fitLifeApplication().userSessionManager
            )
        }

        // Initializer for RoutinesViewModel
        initializer {
            RoutinesViewModel(fitLifeApplication().routineRepository)
        }

        // Initializer for WorkoutsViewModel
        initializer {
            WorkoutsViewModel(
                fitLifeApplication().routineRepository,
                fitLifeApplication().routineInstanceRepository,
                fitLifeApplication().equipmentRepository,
                fitLifeApplication().userSessionManager
            )
        }

        // Initializer for CreateWorkoutViewModel
        initializer {
            CreateWorkoutViewModel(fitLifeApplication().routineRepository)
        }

        // Initializer for DelegateViewModel
        initializer {
            DelegateViewModel(
                fitLifeApplication().routineRepository,
                fitLifeApplication().delegationLogRepository,
                fitLifeApplication().exerciseRepository,
                fitLifeApplication().equipmentRepository
            )
        }

        // Initializer for EquipmentViewModel
        initializer {
            EquipmentViewModel(fitLifeApplication().equipmentRepository)
        }
        
        // Initializer for WeeklyPlanViewModel
        initializer {
            val app = fitLifeApplication()
            val checklistItemRepository = com.example.fitlifeapplication.data.repository.ChecklistItemRepository(app.database.checklistItemDao())
            
            WeeklyPlanViewModel(
                app.routineRepository,
                app.routineInstanceRepository,
                app.exerciseRepository,
                app.equipmentRepository,
                checklistItemRepository
            )
        }

        // Initializer for ProfileViewModel
        initializer {
            ProfileViewModel(
                fitLifeApplication().userRepository,
                fitLifeApplication().routineRepository,
                fitLifeApplication().routineInstanceRepository,
                fitLifeApplication().userSessionManager
            )
        }
    }
}

fun CreationExtras.fitLifeApplication(): FitLifeApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitLifeApplication)
