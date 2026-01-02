package com.example.fitlifeapplication.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitlifeapplication.data.model.Equipment
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(equipment: Equipment): Long

    @Update
    suspend fun update(equipment: Equipment)

    @Delete
    suspend fun delete(equipment: Equipment)

    @Query("SELECT * FROM equipment ORDER BY name ASC")
    fun getAllEquipment(): Flow<List<Equipment>>

    @Query("SELECT * FROM equipment WHERE equipmentId = :equipmentId")
    fun getEquipmentById(equipmentId: Long): Flow<Equipment?>
}
