package com.example.entradalivres.data.repository

import com.example.entradalivres.data.local.datasource.UserLocalDataSource
import com.example.entradalivres.data.mapper.toDomain
import com.example.entradalivres.data.mapper.toEntity
import com.example.entradalivres.data.remote.datasource.UserRemoteDataSource
import com.example.entradalivres.domain.model.AccessibilityFilter
import com.example.entradalivres.domain.model.AccessibilityProfile
import com.example.entradalivres.domain.model.User
import com.example.entradalivres.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * O "login" cria um perfil local (sem autenticação remota por enquanto).
 */
class UserRepositoryImpl(
    private val localDS: UserLocalDataSource,
    @Suppress("unused") private val remoteDS: UserRemoteDataSource,
) : UserRepository {

    override suspend fun login(name: String, email: String, profile: AccessibilityProfile?): User =
        withContext(Dispatchers.IO) {
            val existing = localDS.getUser()?.toDomain()
            val user = if (existing != null && existing.email.equals(email, ignoreCase = true)) {
                existing.copy(name = name, profile = profile ?: existing.profile)
            } else {
                User(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    email = email,
                    profile = profile,
                )
            }
            localDS.clearUser()
            localDS.saveUser(user.toEntity())
            user
        }

    override suspend fun updateProfile(
        profile: AccessibilityProfile?,
        preferredFilters: Set<AccessibilityFilter>
    ) = withContext(Dispatchers.IO) {
        localDS.updateProfile(profile?.name, preferredFilters.map { it.name })
    }

    override suspend fun updateName(name: String) = withContext(Dispatchers.IO) {
        localDS.updateName(name)
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        localDS.clearUser()
    }

    override fun observeUser(): Flow<User?> = localDS.observeUser().map { it?.toDomain() }
}
