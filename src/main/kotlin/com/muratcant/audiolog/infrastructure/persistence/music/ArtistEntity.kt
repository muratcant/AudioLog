package com.muratcant.audiolog.infrastructure.persistence.music

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "artists")
data class ArtistEntity(
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "spotify_id", unique = true, nullable = false)
    val spotifyId: String = "",
    
    @Column(name = "name", nullable = false)
    val name: String = "",
    
    @ElementCollection
    @CollectionTable(name = "artist_genres", joinColumns = [JoinColumn(name = "artist_id")])
    @Column(name = "genre")
    val genres: MutableList<String> = mutableListOf(),
    
    @Column(name = "popularity")
    val popularity: Int? = null,
    
    @Column(name = "image_url")
    val imageUrl: String? = null
) {
    // JPA için default constructor
    constructor() : this(
        id = UUID.randomUUID(),
        spotifyId = "",
        name = "",
        genres = mutableListOf(),
        popularity = null,
        imageUrl = null
    )
}

