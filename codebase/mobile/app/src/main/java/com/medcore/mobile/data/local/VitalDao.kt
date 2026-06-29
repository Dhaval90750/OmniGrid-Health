package com.medcore.mobile.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalDao {
    @Query("SELECT * FROM pending_vitals WHERE synced = 0")
    fun getUnsyncedVitals(): Flow<List<PendingVital>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVital(vital: PendingVital)

    @Update
    suspend fun updateVital(vital: PendingVital)

    @Query("DELETE FROM pending_vitals WHERE synced = 1")
    suspend fun deleteSyncedVitals()
}
