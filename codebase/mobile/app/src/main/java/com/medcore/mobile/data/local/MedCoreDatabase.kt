package com.medcore.mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PendingVital::class], version = 1, exportSchema = false)
abstract class MedCoreDatabase : RoomDatabase() {
    abstract fun vitalDao(): VitalDao
}
