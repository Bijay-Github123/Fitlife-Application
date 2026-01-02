package com.example.fitlifeapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val expenseId: Long = 0,
    val userId: Long,
    val title: String,
    val amountCents: Long,
    val currency: String,
    val date: Long,
    val notes: String?
)
