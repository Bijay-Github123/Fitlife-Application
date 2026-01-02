package com.example.fitlifeapplication.data.repository

import com.example.fitlifeapplication.data.db.EquipmentDao
import com.example.fitlifeapplication.data.model.Equipment
import kotlinx.coroutines.flow.Flow

class EquipmentRepository(private val equipmentDao: EquipmentDao) {

    fun getAllEquipment(): Flow<List<Equipment>> = equipmentDao.getAllEquipment()

    fun getEquipmentById(equipmentId: Long): Flow<Equipment?> = equipmentDao.getEquipmentById(equipmentId)

    suspend fun insert(equipment: Equipment): Long {
        return equipmentDao.insert(equipment)
    }

    suspend fun update(equipment: Equipment) {
        equipmentDao.update(equipment)
    }

    suspend fun delete(equipment: Equipment) {
        equipmentDao.delete(equipment)
    }
}
