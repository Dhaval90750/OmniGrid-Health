package com.medcore.mobile.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PendingActionDao {
    @Query("SELECT * FROM pending_actions ORDER BY timestamp ASC")
    suspend fun getAllPendingActions(): List<PendingAction>

    @Insert
    suspend fun insertAction(action: PendingAction)

    @Update
    suspend fun updateAction(action: PendingAction)

    @Delete
    suspend fun deleteAction(action: PendingAction)
}
