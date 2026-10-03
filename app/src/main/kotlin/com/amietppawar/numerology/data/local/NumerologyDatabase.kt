package com.amietppawar.numerology.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for numerology profiles
 * Stores: name calculations, birth/destiny numbers, and custom profiles
 */
@Database(
    entities = [NumerologyProfile::class],
    version = 1,
    exportSchema = false
)
abstract class NumerologyDatabase : RoomDatabase() {
    abstract fun profileDao(): NumerologyProfileDao
}
