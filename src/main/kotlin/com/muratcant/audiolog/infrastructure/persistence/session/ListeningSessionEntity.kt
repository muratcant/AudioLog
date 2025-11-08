package com.muratcant.audiolog.infrastructure.persistence.session

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "listening_sessions",
    indexes = [
        Index(name = "idx_listening_sessions_user_id", columnList = "user_id"),
        Index(name = "idx_listening_sessions_played_at", columnList = "played_at")
    ]
)
data class ListeningSessionEntity(
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "user_id", columnDefinition = "UUID", nullable = false)
    val userId: UUID = UUID.randomUUID(),
    
    @Column(name = "track_id", columnDefinition = "UUID", nullable = false)
    val trackId: UUID = UUID.randomUUID(),
    
    @Column(name = "played_at", nullable = false)
    val playedAt: Instant = Instant.now(),
    
    @Column(name = "context_type")
    val contextType: String? = null,
    
    @Column(name = "context_uri")
    val contextUri: String? = null
) {
    // JPA için default constructor
    constructor() : this(
        id = UUID.randomUUID(),
        userId = UUID.randomUUID(),
        trackId = UUID.randomUUID(),
        playedAt = Instant.now(),
        contextType = null,
        contextUri = null
    )
}

