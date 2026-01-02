package com.example.fitlifeapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val email: String,
    val displayName: String,
    val passwordHash: String, // In a real app, use a secure hashing library like BCrypt
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null
)
