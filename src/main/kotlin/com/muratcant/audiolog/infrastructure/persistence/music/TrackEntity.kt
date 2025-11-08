package com.muratcant.audiolog.infrastructure.persistence.music

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tracks")
data class TrackEntity(
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "spotify_id", unique = true, nullable = false)
    val spotifyId: String = "",
    
    @Column(name = "name", nullable = false)
    val name: String = "",
    
    @ElementCollection
    @CollectionTable(name = "track_artists", joinColumns = [JoinColumn(name = "track_id")])
    @Column(name = "artist_id", columnDefinition = "UUID")
    val artistIds: MutableList<UUID> = mutableListOf(),
    
    @Column(name = "album_id", columnDefinition = "UUID")
    val albumId: UUID? = null,
    
    @Column(name = "duration_ms", nullable = false)
    val durationMs: Long = 0,
    
    @Column(name = "explicit", nullable = false)
    val explicit: Boolean = false,
    
    @Column(name = "popularity")
    val popularity: Int? = null,
    
    @Column(name = "preview_url")
    val previewUrl: String? = null
) {
    // JPA için default constructor
    constructor() : this(
        id = UUID.randomUUID(),
        spotifyId = "",
        name = "",
        artistIds = mutableListOf(),
        albumId = null,
        durationMs = 0,
        explicit = false,
        popularity = null,
        previewUrl = null
    )
}

@Entity
@Table(name = "track_external_urls")
data class TrackExternalUrlEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "track_id", columnDefinition = "UUID", nullable = false)
    val trackId: UUID = UUID.randomUUID(),
    
    @Column(name = "platform", nullable = false)
    val platform: String = "",
    
    @Column(name = "url", nullable = false)
    val url: String = ""
) {
    // JPA için default constructor
    constructor() : this(
        id = null,
        trackId = UUID.randomUUID(),
        platform = "",
        url = ""
    )
}

