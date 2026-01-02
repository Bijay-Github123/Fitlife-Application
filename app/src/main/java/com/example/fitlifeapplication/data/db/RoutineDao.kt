package com.example.fitlifeapplication.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitlifeapplication.data.model.Routine
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(routine: Routine): Long

    @Update
    suspend fun update(routine: Routine)

    @Delete
    suspend fun delete(routine: Routine)

    @Query("SELECT * FROM routines ORDER BY title ASC")
    fun getAllRoutines(): Flow<List<Routine>>

    @Query("SELECT * FROM routines WHERE routineId = :routineId")
    fun getRoutineById(routineId: Long): Flow<Routine?>

    @Query("SELECT * FROM routines WHERE title = :title LIMIT 1")
    suspend fun getRoutineByTitle(title: String): Routine?
}
