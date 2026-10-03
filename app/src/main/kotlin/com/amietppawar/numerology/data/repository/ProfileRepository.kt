package com.amietppawar.numerology.data.repository

import com.amietppawar.numerology.data.local.NumerologyProfile
import com.amietppawar.numerology.data.local.NumerologyProfileDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository for numerology profile operations
 * Acts as single source of truth for profile data
 */
class ProfileRepository(private val profileDao: NumerologyProfileDao) {

    /**
     * Save a new profile
     */
    suspend fun saveProfile(profile: NumerologyProfile) {
        profileDao.insert(profile)
    }

    /**
     * Update an existing profile
     */
    suspend fun updateProfile(profile: NumerologyProfile) {
        profileDao.update(profile)
    }

    /**
     * Delete a profile
     */
    suspend fun deleteProfile(profile: NumerologyProfile) {
        profileDao.delete(profile)
    }

    /**
     * Get a profile by ID
     */
    suspend fun getProfileById(id: Int): NumerologyProfile? {
        return profileDao.getById(id)
    }

    /**
     * Get all profiles as Flow (reactive)
     */
    fun getAllProfiles(): Flow<List<NumerologyProfile>> {
        return profileDao.getAllProfiles()
    }

    /**
     * Get all profiles as list (suspend)
     */
    suspend fun getAllProfilesList(): List<NumerologyProfile> {
        return profileDao.getAllProfilesList()
    }

    /**
     * Search profiles by name
     */
    fun searchProfiles(query: String): Flow<List<NumerologyProfile>> {
        return profileDao.searchByName(query)
    }

    /**
     * Get profile count
     */
    suspend fun getProfileCount(): Int {
        return profileDao.getCount()
    }

    /**
     * Delete all profiles
     */
    suspend fun deleteAllProfiles() {
        profileDao.deleteAll()
    }

    /**
     * Get most recently saved profile
     */
    suspend fun getLatestProfile(): NumerologyProfile? {
        return profileDao.getLatest()
    }
}
