package com.muratcant.audiolog.infrastructure.persistence

import com.muratcant.audiolog.domain.common.AlbumId
import com.muratcant.audiolog.domain.common.SpotifyId
import com.muratcant.audiolog.domain.music.Album
import com.muratcant.audiolog.domain.ports.AlbumRepository
import com.muratcant.audiolog.infrastructure.persistence.music.AlbumJpaRepository
import org.springframework.stereotype.Component

@Component
class JpaAlbumRepository(
    private val jpaRepository: AlbumJpaRepository
) : AlbumRepository {
    
    override fun findById(id: AlbumId): Album? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }
    
    override fun findBySpotifyId(spotifyId: SpotifyId): Album? {
        return jpaRepository.findBySpotifyId(spotifyId.value)?.toDomain()
    }
    
    override fun save(album: Album): Album {
        return jpaRepository.save(album.toEntity()).toDomain()
    }
    
    override fun saveAll(albums: List<Album>): List<Album> {
        return jpaRepository.saveAll(albums.map { it.toEntity() }).map { it.toDomain() }
    }
}

