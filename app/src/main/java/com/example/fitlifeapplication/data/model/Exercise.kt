package com.example.fitlifeapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val exerciseId: Long = 0,
    val name: String,
    val description: String,
    val sets: Int?,
    val reps: String?, // e.g., "8-12", "30 sec"
    val durationSec: Int?,
    val equipmentIds: List<Long>, // Stored as JSON
    val imageUri: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)
