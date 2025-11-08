package com.muratcant.audiolog.infrastructure.persistence

import com.muratcant.audiolog.domain.common.ArtistId
import com.muratcant.audiolog.domain.common.SpotifyId
import com.muratcant.audiolog.domain.music.Artist
import com.muratcant.audiolog.domain.ports.ArtistRepository
import com.muratcant.audiolog.infrastructure.persistence.music.ArtistJpaRepository
import org.springframework.stereotype.Component

@Component
class JpaArtistRepository(
    private val jpaRepository: ArtistJpaRepository
) : ArtistRepository {
    
    override fun findById(id: ArtistId): Artist? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }
    
    override fun findBySpotifyId(spotifyId: SpotifyId): Artist? {
        return jpaRepository.findBySpotifyId(spotifyId.value)?.toDomain()
    }
    
    override fun save(artist: Artist): Artist {
        return jpaRepository.save(artist.toEntity()).toDomain()
    }
    
    override fun saveAll(artists: List<Artist>): List<Artist> {
        return jpaRepository.saveAll(artists.map { it.toEntity() }).map { it.toDomain() }
    }
}

