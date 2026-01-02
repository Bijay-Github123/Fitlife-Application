package com.example.fitlifeapplication.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitlifeapplication.data.model.LocationTag
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locationTag: LocationTag): Long

    @Update
    suspend fun update(locationTag: LocationTag)

    @Delete
    suspend fun delete(locationTag: LocationTag)

    @Query("SELECT * FROM location_tags ORDER BY name ASC")
    fun getAllLocationTags(): Flow<List<LocationTag>>

    @Query("SELECT * FROM location_tags WHERE locationId = :id")
    fun getLocationTagById(id: Long): Flow<LocationTag?>
}
