package com.muratcant.audiolog.infrastructure.persistence.music

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TrackJpaRepository : JpaRepository<TrackEntity, UUID> {
    fun findBySpotifyId(spotifyId: String): TrackEntity?
}

