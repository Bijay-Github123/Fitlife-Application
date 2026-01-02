package com.example.fitlifeapplication.data.repository

import com.example.fitlifeapplication.data.db.RoutineInstanceDao
import com.example.fitlifeapplication.data.model.RoutineInstance
import kotlinx.coroutines.flow.Flow

class RoutineInstanceRepository(private val routineInstanceDao: RoutineInstanceDao) {

    fun getAllRoutineInstances(): Flow<List<RoutineInstance>> = routineInstanceDao.getAllRoutineInstances()
    
    fun getRoutineInstanceById(instanceId: Long): Flow<RoutineInstance?> = routineInstanceDao.getRoutineInstanceById(instanceId)

    fun getRoutineInstancesForUser(userId: Long): Flow<List<RoutineInstance>> = routineInstanceDao.getRoutineInstancesForUser(userId)

    fun getCountScheduledInWeek(userId: Long, startTime: Long, endTime: Long): Flow<Int> = routineInstanceDao.getCountScheduledInWeek(userId, startTime, endTime)

    fun getCountCompletedInWeek(userId: Long, startTime: Long, endTime: Long): Flow<Int> = routineInstanceDao.getCountCompletedInWeek(userId, startTime, endTime)

    suspend fun insert(routineInstance: RoutineInstance): Long {
        return routineInstanceDao.insert(routineInstance)
    }

    suspend fun update(routineInstance: RoutineInstance) {
        routineInstanceDao.update(routineInstance)
    }

    suspend fun delete(routineInstance: RoutineInstance) {
        routineInstanceDao.delete(routineInstance)
    }
}
