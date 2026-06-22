package com.medcore.mobile.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.medcore.mobile.data.dao.AssessmentDao
import com.medcore.mobile.data.dao.IncidentDao
import com.medcore.mobile.data.dao.VitalDao
import com.medcore.mobile.data.entity.AssessmentEntity
import com.medcore.mobile.data.entity.IncidentEntity
import com.medcore.mobile.data.entity.VitalEntity

@Database(entities = [VitalEntity::class, AssessmentEntity::class, IncidentEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vitalDao(): VitalDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun incidentDao(): IncidentDao
}
