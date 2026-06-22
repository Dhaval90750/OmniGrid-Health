package com.medcore.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vitals")
data class VitalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientUhid: String,
    val temperature: String,
    val pulse: String,
    val bp: String,
    val spo2: String,
    val painLevel: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)
