package com.medcore.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assessments")
data class AssessmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientUhid: String,
    val type: String, // "MorseFalls" or "Braden"
    val score: Int,
    val details: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)
