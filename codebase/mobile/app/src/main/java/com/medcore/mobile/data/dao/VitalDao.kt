package com.medcore.mobile.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.medcore.mobile.data.entity.VitalEntity

@Dao
interface VitalDao {
    @Insert
    suspend fun insert(vital: VitalEntity)

    @Query("SELECT * FROM vitals WHERE isSynced = 0")
    suspend fun getUnsyncedVitals(): List<VitalEntity>

    @Update
    suspend fun update(vital: VitalEntity)
}
