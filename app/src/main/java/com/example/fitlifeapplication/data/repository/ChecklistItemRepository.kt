package com.example.fitlifeapplication.data.repository

import com.example.fitlifeapplication.data.db.ChecklistItemDao
import com.example.fitlifeapplication.data.model.ChecklistItem
import kotlinx.coroutines.flow.Flow

class ChecklistItemRepository(private val checklistItemDao: ChecklistItemDao) {
    suspend fun insert(checklistItem: ChecklistItem): Long {
        return checklistItemDao.insertChecklistItem(checklistItem)
    }

    suspend fun update(checklistItem: ChecklistItem) {
        checklistItemDao.updateChecklistItem(checklistItem)
    }

    suspend fun delete(checklistItem: ChecklistItem) {
        checklistItemDao.deleteChecklistItem(checklistItem)
    }

    suspend fun deleteForRoutine(routineId: Long) {
        checklistItemDao.deleteChecklistItemsForRoutine(routineId)
    }

    fun getChecklistItemsForRoutine(routineId: Long): Flow<List<ChecklistItem>> {
        return checklistItemDao.getChecklistItemsForRoutine(routineId)
    }
}
