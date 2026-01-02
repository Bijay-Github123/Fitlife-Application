package com.example.fitlifeapplication.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitlifeapplication.data.model.DelegationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DelegationLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: DelegationLog)

    @Query("SELECT * FROM delegation_logs ORDER BY sentAt DESC")
    fun getAllLogs(): Flow<List<DelegationLog>>
}
