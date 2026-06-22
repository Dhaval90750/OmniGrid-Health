package com.medcore.mobile.di

import android.content.Context
import androidx.room.Room
import com.medcore.mobile.data.AppDatabase
import com.medcore.mobile.data.dao.AssessmentDao
import com.medcore.mobile.data.dao.IncidentDao
import com.medcore.mobile.data.dao.VitalDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "medcore_offline_db"
        ).build()
    }

    @Provides
    fun provideVitalDao(db: AppDatabase): VitalDao = db.vitalDao()

    @Provides
    fun provideAssessmentDao(db: AppDatabase): AssessmentDao = db.assessmentDao()

    @Provides
    fun provideIncidentDao(db: AppDatabase): IncidentDao = db.incidentDao()
}
