package com.example.fitlifeapplication.data.repository

import com.example.fitlifeapplication.data.db.RoutineDao
import com.example.fitlifeapplication.data.model.Routine
import kotlinx.coroutines.flow.Flow

class RoutineRepository(private val routineDao: RoutineDao) {

    fun getAllRoutines(): Flow<List<Routine>> = routineDao.getAllRoutines()

    fun getRoutineById(id: Long): Flow<Routine?> = routineDao.getRoutineById(id)

    suspend fun getRoutineByTitle(title: String): Routine? = routineDao.getRoutineByTitle(title)

    suspend fun insert(routine: Routine): Long {
        return routineDao.insert(routine)
    }

    suspend fun update(routine: Routine) {
        routineDao.update(routine)
    }

    suspend fun delete(routine: Routine) {
        routineDao.delete(routine)
    }
}
