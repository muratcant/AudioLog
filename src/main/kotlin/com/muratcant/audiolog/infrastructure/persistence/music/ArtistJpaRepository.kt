package com.muratcant.audiolog.infrastructure.persistence.music

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ArtistJpaRepository : JpaRepository<ArtistEntity, UUID> {
    fun findBySpotifyId(spotifyId: String): ArtistEntity?
}

