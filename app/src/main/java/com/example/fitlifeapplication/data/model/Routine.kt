package com.example.fitlifeapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true)
    val routineId: Long = 0,
    val title: String,
    val description: String?,
    val ownerUserId: Long,
    val exerciseOrder: List<Long>, // Stored as JSON
    val linkedLocationId: Long? = null,
    val isPublic: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)
