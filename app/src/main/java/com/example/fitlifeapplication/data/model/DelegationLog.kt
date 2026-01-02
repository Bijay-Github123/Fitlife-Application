package com.example.fitlifeapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "delegation_logs")
data class DelegationLog(
    @PrimaryKey(autoGenerate = true)
    val delegationId: Long = 0,
    val fromUserId: Long,
    val toContact: String,
    val messageText: String,
    val sentAt: Long = System.currentTimeMillis(),
    val status: String // "INTENT_OPENED", "SENT"
)
