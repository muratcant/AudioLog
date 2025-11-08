package com.muratcant.audiolog.infrastructure.persistence.curation

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "ratings",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_ratings_user_track", columnNames = ["user_id", "track_id"])
    ]
)
data class RatingEntity(
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "user_id", columnDefinition = "UUID", nullable = false)
    val userId: UUID = UUID.randomUUID(),
    
    @Column(name = "track_id", columnDefinition = "UUID", nullable = false)
    val trackId: UUID = UUID.randomUUID(),
    
    @Column(name = "overall_score")
    val overallScore: Int? = null,
    
    @Column(name = "vocal_score")
    val vocalScore: Int? = null,
    
    @Column(name = "mood_score")
    val moodScore: Int? = null,
    
    @Column(name = "lyrics_score")
    val lyricsScore: Int? = null,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()
) {
    // JPA için default constructor
    constructor() : this(
        id = UUID.randomUUID(),
        userId = UUID.randomUUID(),
        trackId = UUID.randomUUID(),
        overallScore = null,
        vocalScore = null,
        moodScore = null,
        lyricsScore = null,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )
}

