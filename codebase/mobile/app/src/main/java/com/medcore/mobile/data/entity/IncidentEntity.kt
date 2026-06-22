package com.medcore.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reporterId: String,
    val type: String,
    val description: String,
    val imageUri: String?,
    val timestamp: Long,
    val isSynced: Boolean = false
)
