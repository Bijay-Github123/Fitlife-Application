package com.example.fitlifeapplication

import android.app.Application
import com.example.fitlifeapplication.data.db.FitLifeDatabase
import com.example.fitlifeapplication.data.manager.UserSessionManager
import com.example.fitlifeapplication.data.repository.*

class FitLifeApplication : Application() {
    val database: FitLifeDatabase by lazy { FitLifeDatabase.getDatabase(this) }
    val userRepository: UserRepository by lazy { UserRepository(database.userDao()) }
    val routineRepository: RoutineRepository by lazy { RoutineRepository(database.routineDao()) }
    val exerciseRepository: ExerciseRepository by lazy { ExerciseRepository(database.exerciseDao()) }
    val equipmentRepository: EquipmentRepository by lazy { EquipmentRepository(database.equipmentDao()) }
    val routineInstanceRepository: RoutineInstanceRepository by lazy { RoutineInstanceRepository(database.routineInstanceDao()) }
    val delegationLogRepository: DelegationLogRepository by lazy { DelegationLogRepository(database.delegationLogDao()) }
    val userSessionManager: UserSessionManager by lazy { UserSessionManager(this) }
}
