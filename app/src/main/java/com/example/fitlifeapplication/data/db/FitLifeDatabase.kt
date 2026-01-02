package com.example.fitlifeapplication.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fitlifeapplication.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@Database(
    entities = [
        User::class,
        Equipment::class,
        Exercise::class,
        Routine::class,
        RoutineInstance::class,
        ChecklistItem::class,
        LocationTag::class,
        Expense::class,
        DelegationLog::class
    ],
    version = 8, // Incremented version to trigger onCreate/re-creation if destructive migration matches
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FitLifeDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun routineDao(): RoutineDao
    abstract fun routineInstanceDao(): RoutineInstanceDao
    abstract fun checklistItemDao(): ChecklistItemDao
    abstract fun locationTagDao(): LocationTagDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun delegationLogDao(): DelegationLogDao

    companion object {
        @Volatile
        private var INSTANCE: FitLifeDatabase? = null

        fun getDatabase(context: Context): FitLifeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitLifeDatabase::class.java,
                    "fitlife_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(FitLifeDatabaseCallback())
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    private class FitLifeDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    // DAOs
                    val userDao = database.userDao()
                    val routineDao = database.routineDao()
                    val routineInstanceDao = database.routineInstanceDao()
                    val locationTagDao = database.locationTagDao()
                    val equipmentDao = database.equipmentDao()
                    val exerciseDao = database.exerciseDao()

                    // Seed User
                    val userId = userDao.insert(User(email = "john@fitlife.test", displayName = "John Doe", passwordHash = "FitLife123!"))

                    // Seed Equipment
                    val dumbbellsId = equipmentDao.insert(Equipment(name = "Dumbbells", category = "Strength", notes = "Various weights"))
                    val yogaMatId = equipmentDao.insert(Equipment(name = "Yoga Mat", category = "Mat", notes = "Non-slip"))
                    val bandsId = equipmentDao.insert(Equipment(name = "Resistance Bands", category = "Accessories", notes = "Set of 3"))
                    val benchId = equipmentDao.insert(Equipment(name = "Bench", category = "Strength", notes = "Adjustable"))

                    // Seed Exercises
                    val benchPressId = exerciseDao.insert(Exercise(
                        name = "Dumbbell Bench Press",
                        description = "Press dumbbells up while lying on bench.",
                        sets = 3, reps = "12", durationSec = null,
                        equipmentIds = listOf(dumbbellsId, benchId),
                        imageUri = null
                    ))
                    val squatId = exerciseDao.insert(Exercise(
                        name = "Goblet Squat",
                        description = "Squat holding a dumbbell.",
                        sets = 3, reps = "15", durationSec = null,
                        equipmentIds = listOf(dumbbellsId),
                        imageUri = null
                    ))
                    val downDogId = exerciseDao.insert(Exercise(
                        name = "Downward Dog",
                        description = "Hold position.",
                        sets = 1, reps = null, durationSec = 60,
                        equipmentIds = listOf(yogaMatId),
                        imageUri = null
                    ))
                    val plankId = exerciseDao.insert(Exercise(
                        name = "Plank",
                        description = "Core stability.",
                        sets = 3, reps = null, durationSec = 45,
                        equipmentIds = listOf(yogaMatId),
                        imageUri = null
                    ))
                    val burpeesId = exerciseDao.insert(Exercise(
                        name = "Burpees",
                        description = "Full body cardio.",
                        sets = 3, reps = "15", durationSec = null,
                        equipmentIds = emptyList(),
                        imageUri = null
                    ))

                    // Seed Routines (Updated titles to match screenshot exactly)
                    val fullBodyId = routineDao.insert(Routine(
                        title = "Full-Body Strength", 
                        description = "A comprehensive workout for all major muscle groups.", 
                        ownerUserId = userId, 
                        exerciseOrder = listOf(benchPressId, squatId)
                    ))
                    val hiitId = routineDao.insert(Routine(
                        title = "HIIT Cardio", 
                        description = "High-intensity interval training to maximize calorie burn.", 
                        ownerUserId = userId, 
                        exerciseOrder = listOf(burpeesId)
                    ))
                    val yogaId = routineDao.insert(Routine(
                        title = "Morning Yoga", 
                        description = "A gentle flow to start your day.", 
                        ownerUserId = userId, 
                        exerciseOrder = listOf(downDogId)
                    ))
                    val pilatesId = routineDao.insert(Routine(
                        title = "Core Strengthening", 
                        description = "Strengthen your core with these Pilates exercises.", 
                        ownerUserId = userId, 
                        exerciseOrder = listOf(plankId)
                    ))

                    // Seed Locations
                    locationTagDao.insert(LocationTag(name = "University Gym", latitude = 34.0522, longitude = -118.2437, address = "123 University Dr", notes = "Main campus gym", providesEquipment = emptyList()))
                    locationTagDao.insert(LocationTag(name = "Nearby Park", latitude = 34.0622, longitude = -118.2537, address = "456 Park Ave", notes = "Outdoor workout area", providesEquipment = emptyList()))
                    locationTagDao.insert(LocationTag(name = "Yoga Studio", latitude = 34.0722, longitude = -118.2637, address = "789 Yoga St", notes = "Peaceful and quiet", providesEquipment = emptyList()))

                    // Seed Routine Instances (3 completed, 1 upcoming)
                    routineInstanceDao.insert(RoutineInstance(routineId = fullBodyId, userId = userId, scheduledDate = Date().time, reminderTime = null, completed = true))
                    routineInstanceDao.insert(RoutineInstance(routineId = yogaId, userId = userId, scheduledDate = Date().time, reminderTime = null, completed = false)) // Screenshot: Pending
                    routineInstanceDao.insert(RoutineInstance(routineId = pilatesId, userId = userId, scheduledDate = Date().time, reminderTime = null, completed = true))
                    routineInstanceDao.insert(RoutineInstance(routineId = hiitId, userId = userId, scheduledDate = Date().time, reminderTime = null, completed = false)) // Screenshot: Pending
                }
            }
        }
    }
}
