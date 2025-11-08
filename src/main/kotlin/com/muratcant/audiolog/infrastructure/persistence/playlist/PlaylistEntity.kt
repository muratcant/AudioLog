package com.muratcant.audiolog.infrastructure.persistence.playlist

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "playlists")
data class PlaylistEntity(
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "user_id", columnDefinition = "UUID", nullable = false)
    val userId: UUID = UUID.randomUUID(),
    
    @Column(name = "spotify_id")
    val spotifyId: String? = null,
    
    @Column(name = "name", nullable = false)
    val name: String = "",
    
    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,
    
    @Column(name = "is_public", nullable = false)
    val isPublic: Boolean = false,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()
) {
    // JPA için default constructor
    constructor() : this(
        id = UUID.randomUUID(),
        userId = UUID.randomUUID(),
        spotifyId = null,
        name = "",
        description = null,
        isPublic = false,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )
}

@Entity
@Table(
    name = "playlist_tracks",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_playlist_tracks", columnNames = ["playlist_id", "track_id"])
    ]
)
data class PlaylistTrackEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "playlist_id", columnDefinition = "UUID", nullable = false)
    val playlistId: UUID = UUID.randomUUID(),
    
    @Column(name = "track_id", columnDefinition = "UUID", nullable = false)
    val trackId: UUID = UUID.randomUUID(),
    
    @Column(name = "position", nullable = false)
    val position: Int = 0,
    
    @Column(name = "added_at", nullable = false)
    val addedAt: Instant = Instant.now()
) {
    // JPA için default constructor
    constructor() : this(
        id = null,
        playlistId = UUID.randomUUID(),
        trackId = UUID.randomUUID(),
        position = 0,
        addedAt = Instant.now()
    )
}

