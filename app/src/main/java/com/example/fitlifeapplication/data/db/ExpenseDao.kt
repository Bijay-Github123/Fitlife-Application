package com.example.fitlifeapplication.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fitlifeapplication.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense): Long

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpensesForUser(userId: Long): Flow<List<Expense>>
}
