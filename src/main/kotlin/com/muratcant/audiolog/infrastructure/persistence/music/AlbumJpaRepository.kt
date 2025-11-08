package com.muratcant.audiolog.infrastructure.persistence.music

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AlbumJpaRepository : JpaRepository<AlbumEntity, UUID> {
    fun findBySpotifyId(spotifyId: String): AlbumEntity?
}

