package com.muratcant.audiolog.infrastructure.persistence.music

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "albums")
data class AlbumEntity(
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "spotify_id", unique = true, nullable = false)
    val spotifyId: String = "",
    
    @Column(name = "name", nullable = false)
    val name: String = "",
    
    @ElementCollection
    @CollectionTable(name = "album_artists", joinColumns = [JoinColumn(name = "album_id")])
    @Column(name = "artist_id", columnDefinition = "UUID")
    val artistIds: MutableList<UUID> = mutableListOf(),
    
    @Column(name = "release_date")
    val releaseDate: String? = null,
    
    @Column(name = "release_date_precision")
    val releaseDatePrecision: String? = null,
    
    @Column(name = "image_url")
    val imageUrl: String? = null,
    
    @Column(name = "total_tracks")
    val totalTracks: Int? = null
) {
    // JPA için default constructor
    constructor() : this(
        id = UUID.randomUUID(),
        spotifyId = "",
        name = "",
        artistIds = mutableListOf(),
        releaseDate = null,
        releaseDatePrecision = null,
        imageUrl = null,
        totalTracks = null
    )
}

