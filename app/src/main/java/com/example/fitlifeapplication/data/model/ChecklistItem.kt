package com.example.fitlifeapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_items")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true)
    val checklistItemId: Long = 0,
    val routineId: Long?,
    val exerciseId: Long?,
    val equipmentId: Long?,
    val title: String,
    val category: String,
    val done: Boolean = false,
    val quantity: Int?
)
