package com.example.fitlifeapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey(autoGenerate = true)
    val equipmentId: Long = 0,
    val name: String,
    val category: String, // e.g., "Strength", "Mat", "Accessory"
    val notes: String?
)
