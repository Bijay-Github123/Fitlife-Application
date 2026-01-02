package com.example.fitlifeapplication.data.repository

import com.example.fitlifeapplication.data.db.DelegationLogDao
import com.example.fitlifeapplication.data.model.DelegationLog
import kotlinx.coroutines.flow.Flow

class DelegationLogRepository(private val delegationLogDao: DelegationLogDao) {

    fun getAllLogs(): Flow<List<DelegationLog>> = delegationLogDao.getAllLogs()

    suspend fun insert(log: DelegationLog) {
        delegationLogDao.insert(log)
    }
}
