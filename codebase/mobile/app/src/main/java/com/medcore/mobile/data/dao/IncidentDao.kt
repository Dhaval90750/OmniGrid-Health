package com.medcore.mobile.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.medcore.mobile.data.entity.IncidentEntity

@Dao
interface IncidentDao {
    @Insert
    suspend fun insert(incident: IncidentEntity)

    @Query("SELECT * FROM incidents WHERE isSynced = 0")
    suspend fun getUnsyncedIncidents(): List<IncidentEntity>

    @Update
    suspend fun update(incident: IncidentEntity)
}
