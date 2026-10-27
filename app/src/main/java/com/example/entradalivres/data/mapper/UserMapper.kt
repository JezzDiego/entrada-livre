package com.example.entradalivres.data.mapper

import com.example.entradalivres.data.local.entity.UserEntity
import com.example.entradalivres.data.remote.dto.UserDTO
import com.example.entradalivres.domain.model.AccessibilityFilter
import com.example.entradalivres.domain.model.AccessibilityProfile
import com.example.entradalivres.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    profile = profile.toEnumOrNull<AccessibilityProfile>(),
    preferredFilters = preferredFilters.mapNotNull { it.toEnumOrNull<AccessibilityFilter>() }.toSet(),
    createdAt = createdAt,
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    profile = profile?.name,
    preferredFilters = preferredFilters.map { it.name },
    createdAt = createdAt,
)

fun UserDTO.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    profile = profile.toEnumOrNull<AccessibilityProfile>(),
    preferredFilters = preferredFilters.orEmpty().mapNotNull { it.toEnumOrNull<AccessibilityFilter>() }.toSet(),
)

fun User.toDto(): UserDTO = UserDTO(
    id = id,
    name = name,
    email = email,
    profile = profile?.name,
    preferredFilters = preferredFilters.map { it.name },
)
