package com.muratcant.audiolog.infrastructure.persistence.playlist

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PlaylistJpaRepository : JpaRepository<PlaylistEntity, UUID> {
    fun findByUserId(userId: UUID): List<PlaylistEntity>
}

