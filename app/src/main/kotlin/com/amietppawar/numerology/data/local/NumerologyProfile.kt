package com.amietppawar.numerology.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for saved numerology profiles
 * Represents a saved name/birth date calculation
 */
@Entity(tableName = "numerology_profiles")
data class NumerologyProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val birthDate: String? = null, // YYYY-MM-DD format, optional

    // Calculated numbers
    val nameNumber: Int,
    val nameNumberCompound: Int, // Full sum before reduction
    val birthNumber: Int? = null,
    val birthNumberCompound: Int? = null,
    val destinyNumber: Int? = null,
    val destinyNumberCompound: Int? = null,

    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val notes: String = "" // User's custom notes
)
