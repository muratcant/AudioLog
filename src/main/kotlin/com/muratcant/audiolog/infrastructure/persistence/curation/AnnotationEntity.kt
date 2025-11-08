package com.muratcant.audiolog.infrastructure.persistence.curation

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "annotations")
data class AnnotationEntity(
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "user_id", columnDefinition = "UUID", nullable = false)
    val userId: UUID = UUID.randomUUID(),
    
    @Column(name = "track_id", columnDefinition = "UUID", nullable = false)
    val trackId: UUID = UUID.randomUUID(),
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    val content: String = "",
    
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
        content = "",
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )
}

