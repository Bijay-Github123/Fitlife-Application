package com.example.fitlifeapplication.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitlifeapplication.data.model.RoutineInstance
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineInstanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(routineInstance: RoutineInstance): Long

    @Update
    suspend fun update(routineInstance: RoutineInstance)

    @Delete
    suspend fun delete(routineInstance: RoutineInstance)

    @Query("SELECT * FROM routine_instances ORDER BY scheduledDate ASC")
    fun getAllRoutineInstances(): Flow<List<RoutineInstance>>

    @Query("SELECT * FROM routine_instances WHERE instanceId = :instanceId")
    fun getRoutineInstanceById(instanceId: Long): Flow<RoutineInstance?>

    @Query("SELECT * FROM routine_instances WHERE userId = :userId ORDER BY scheduledDate ASC")
    fun getRoutineInstancesForUser(userId: Long): Flow<List<RoutineInstance>>

    @Query("SELECT COUNT(*) FROM routine_instances WHERE userId = :userId AND scheduledDate BETWEEN :startTime AND :endTime")
    fun getCountScheduledInWeek(userId: Long, startTime: Long, endTime: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM routine_instances WHERE userId = :userId AND completed = 1 AND scheduledDate BETWEEN :startTime AND :endTime")
    fun getCountCompletedInWeek(userId: Long, startTime: Long, endTime: Long): Flow<Int>
}
