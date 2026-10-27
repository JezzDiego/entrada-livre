package com.example.entradalivres.domain.repository

import com.example.entradalivres.domain.model.AccessibilityFilter
import com.example.entradalivres.domain.model.AccessibilityProfile
import com.example.entradalivres.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /** Cria (ou recupera) o perfil local da pessoa usuária. */
    suspend fun login(name: String, email: String, profile: AccessibilityProfile?): User

    suspend fun updateProfile(profile: AccessibilityProfile?, preferredFilters: Set<AccessibilityFilter>)

    suspend fun updateName(name: String)

    suspend fun logout()

    fun observeUser(): Flow<User?>
}
