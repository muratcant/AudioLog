package com.muratcant.audiolog.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserJpaRepository : JpaRepository<UserEntity, UUID> {
    fun findBySpotifyId(spotifyId: String): UserEntity?
}

