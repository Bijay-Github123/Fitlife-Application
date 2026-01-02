package com.example.fitlifeapplication.data.repository

import com.example.fitlifeapplication.data.db.ExerciseDao
import com.example.fitlifeapplication.data.model.Exercise
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    fun getAllExercises(): Flow<List<Exercise>> = exerciseDao.getAllExercises()

    fun getExerciseById(exerciseId: Long): Flow<Exercise?> = exerciseDao.getExerciseById(exerciseId)

    suspend fun insert(exercise: Exercise): Long {
        return exerciseDao.insert(exercise)
    }

    suspend fun update(exercise: Exercise) {
        exerciseDao.update(exercise)
    }

    suspend fun delete(exercise: Exercise) {
        exerciseDao.delete(exercise)
    }
}
