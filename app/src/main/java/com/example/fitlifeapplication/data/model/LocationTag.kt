package com.example.fitlifeapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_tags")
data class LocationTag(
    @PrimaryKey(autoGenerate = true)
    val locationId: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val notes: String?,
    val providesEquipment: List<Long> // Stored as JSON
)
