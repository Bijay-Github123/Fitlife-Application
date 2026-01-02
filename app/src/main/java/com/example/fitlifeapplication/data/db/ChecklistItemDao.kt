package com.example.fitlifeapplication.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fitlifeapplication.data.model.ChecklistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDao {
    @Insert
    suspend fun insertChecklistItem(checklistItem: ChecklistItem): Long

    @Update
    suspend fun updateChecklistItem(checklistItem: ChecklistItem)

    @Delete
    suspend fun deleteChecklistItem(checklistItem: ChecklistItem)

    @Query("DELETE FROM checklist_items WHERE routineId = :routineId")
    suspend fun deleteChecklistItemsForRoutine(routineId: Long)

    @Query("SELECT * FROM checklist_items WHERE routineId = :routineId")
    fun getChecklistItemsForRoutine(routineId: Long): Flow<List<ChecklistItem>>
}
