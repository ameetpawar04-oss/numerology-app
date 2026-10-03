package com.amietppawar.numerology.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for NumerologyProfile
 * Provides database operations for saved profiles
 */
@Dao
interface NumerologyProfileDao {

    /**
     * Insert a new profile
     */
    @Insert
    suspend fun insert(profile: NumerologyProfile): Long

    /**
     * Update an existing profile
     */
    @Update
    suspend fun update(profile: NumerologyProfile)

    /**
     * Delete a profile
     */
    @Delete
    suspend fun delete(profile: NumerologyProfile)

    /**
     * Get a profile by ID
     */
    @Query("SELECT * FROM numerology_profiles WHERE id = :id")
    suspend fun getById(id: Int): NumerologyProfile?

    /**
     * Get all profiles, ordered by most recent first
     */
    @Query("SELECT * FROM numerology_profiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<NumerologyProfile>>

    /**
     * Get all profiles, ordered by most recent first (non-Flow version)
     */
    @Query("SELECT * FROM numerology_profiles ORDER BY createdAt DESC")
    suspend fun getAllProfilesList(): List<NumerologyProfile>

    /**
     * Search profiles by name (case-insensitive)
     */
    @Query("SELECT * FROM numerology_profiles WHERE LOWER(name) LIKE LOWER('%' || :query || '%') ORDER BY createdAt DESC")
    fun searchByName(query: String): Flow<List<NumerologyProfile>>

    /**
     * Get profile count
     */
    @Query("SELECT COUNT(*) FROM numerology_profiles")
    suspend fun getCount(): Int

    /**
     * Delete all profiles
     */
    @Query("DELETE FROM numerology_profiles")
    suspend fun deleteAll()

    /**
     * Get most recently created profile
     */
    @Query("SELECT * FROM numerology_profiles ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatest(): NumerologyProfile?
}
