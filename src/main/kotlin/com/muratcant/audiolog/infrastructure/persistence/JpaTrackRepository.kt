package com.muratcant.audiolog.infrastructure.persistence

import com.muratcant.audiolog.domain.common.TrackId
import com.muratcant.audiolog.domain.common.SpotifyId
import com.muratcant.audiolog.domain.music.Track
import com.muratcant.audiolog.domain.ports.TrackRepository
import com.muratcant.audiolog.infrastructure.persistence.music.TrackJpaRepository
import org.springframework.stereotype.Component

@Component
class JpaTrackRepository(
    private val jpaRepository: TrackJpaRepository
) : TrackRepository {
    
    override fun findById(id: TrackId): Track? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }
    
    override fun findBySpotifyId(spotifyId: SpotifyId): Track? {
        return jpaRepository.findBySpotifyId(spotifyId.value)?.toDomain()
    }
    
    override fun save(track: Track): Track {
        return jpaRepository.save(track.toEntity()).toDomain()
    }
    
    override fun saveAll(tracks: List<Track>): List<Track> {
        return jpaRepository.saveAll(tracks.map { it.toEntity() }).map { it.toDomain() }
    }
}

