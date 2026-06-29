package com.medcore.mobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_vitals")
data class PendingVital(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: String,
    val temperature: Double?,
    val heartRate: Int?,
    val bloodPressure: String?,
    val spo2: Int?,
    val painLevel: Int?,
    val timestamp: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)
