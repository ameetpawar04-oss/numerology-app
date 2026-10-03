package com.amietppawar.numerology

import android.content.Context
import androidx.room.Room
import com.amietppawar.numerology.data.local.NumerologyDatabase

/**
 * Holds the single database instance for the whole app, so that rotating the
 * screen never opens a second copy.
 */
object AppGraph {

    @Volatile
    private var database: NumerologyDatabase? = null

    fun database(context: Context): NumerologyDatabase {
        val existing = database
        if (existing != null) return existing
        return synchronized(this) {
            val again = database
            if (again != null) {
                again
            } else {
                val created = Room.databaseBuilder(
                    context.applicationContext,
                    NumerologyDatabase::class.java,
                    "numerology_database"
                ).build()
                database = created
                created
            }
        }
    }
}
