package com.medcore.mobile.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.medcore.mobile.data.entity.AssessmentEntity

@Dao
interface AssessmentDao {
    @Insert
    suspend fun insert(assessment: AssessmentEntity)

    @Query("SELECT * FROM assessments WHERE isSynced = 0")
    suspend fun getUnsyncedAssessments(): List<AssessmentEntity>

    @Update
    suspend fun update(assessment: AssessmentEntity)
}
