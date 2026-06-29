package com.medcore.mobile.di

import android.content.Context
import androidx.room.Room
import com.medcore.mobile.data.local.MedCoreDatabase
import com.medcore.mobile.data.local.VitalDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MedCoreDatabase {
        return Room.databaseBuilder(
            context,
            MedCoreDatabase::class.java,
            "medcore_db"
        ).build()
    }

    @Provides
    fun provideVitalDao(database: MedCoreDatabase): VitalDao {
        return database.vitalDao()
    }
}
