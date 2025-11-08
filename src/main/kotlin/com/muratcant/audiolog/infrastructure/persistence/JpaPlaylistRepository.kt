package com.muratcant.audiolog.infrastructure.persistence

import com.muratcant.audiolog.domain.common.PlaylistId
import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.playlist.Playlist
import com.muratcant.audiolog.domain.ports.PlaylistRepository
import com.muratcant.audiolog.infrastructure.persistence.playlist.PlaylistJpaRepository
import org.springframework.stereotype.Component

@Component
class JpaPlaylistRepository(
    private val jpaRepository: PlaylistJpaRepository
) : PlaylistRepository {
    
    override fun findById(id: PlaylistId): Playlist? {
        return jpaRepository.findById(id.value).orElse(null)?.let {
            Playlist(
                id = PlaylistId(it.id),
                userId = UserId(it.userId),
                spotifyId = it.spotifyId?.let { sid -> com.muratcant.audiolog.domain.common.SpotifyId(sid) },
                name = it.name,
                description = it.description,
                isPublic = it.isPublic,
                createdAt = com.muratcant.audiolog.domain.common.Timestamp(it.createdAt),
                updatedAt = com.muratcant.audiolog.domain.common.Timestamp(it.updatedAt)
            )
        }
    }
    
    override fun findByUserId(userId: UserId): List<Playlist> {
        return jpaRepository.findByUserId(userId.value).map {
            Playlist(
                id = PlaylistId(it.id),
                userId = UserId(it.userId),
                spotifyId = it.spotifyId?.let { sid -> com.muratcant.audiolog.domain.common.SpotifyId(sid) },
                name = it.name,
                description = it.description,
                isPublic = it.isPublic,
                createdAt = com.muratcant.audiolog.domain.common.Timestamp(it.createdAt),
                updatedAt = com.muratcant.audiolog.domain.common.Timestamp(it.updatedAt)
            )
        }
    }
    
    override fun save(playlist: Playlist): Playlist {
        val entity = com.muratcant.audiolog.infrastructure.persistence.playlist.PlaylistEntity(
            id = playlist.id.value,
            userId = playlist.userId.value,
            spotifyId = playlist.spotifyId?.value,
            name = playlist.name,
            description = playlist.description,
            isPublic = playlist.isPublic,
            createdAt = playlist.createdAt.value,
            updatedAt = playlist.updatedAt.value
        )
        val saved = jpaRepository.save(entity)
        return Playlist(
            id = PlaylistId(saved.id),
            userId = UserId(saved.userId),
            spotifyId = saved.spotifyId?.let { com.muratcant.audiolog.domain.common.SpotifyId(it) },
            name = saved.name,
            description = saved.description,
            isPublic = saved.isPublic,
            createdAt = com.muratcant.audiolog.domain.common.Timestamp(saved.createdAt),
            updatedAt = com.muratcant.audiolog.domain.common.Timestamp(saved.updatedAt)
        )
    }
}

