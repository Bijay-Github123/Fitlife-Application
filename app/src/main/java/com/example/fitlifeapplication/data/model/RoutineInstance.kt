package com.example.fitlifeapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_instances")
data class RoutineInstance(
    @PrimaryKey(autoGenerate = true)
    val instanceId: Long = 0,
    val routineId: Long,
    val userId: Long,
    val scheduledDate: Long,
    val reminderTime: Long?,
    val completed: Boolean = false,
    val completedAt: Long? = null
)
