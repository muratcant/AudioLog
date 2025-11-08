package com.muratcant.audiolog.infrastructure.persistence.curation

import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    name = "tags",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_tags_key", columnNames = ["key"])
    ]
)
data class TagEntity(
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "key", unique = true, nullable = false)
    val key: String = "",
    
    @Column(name = "value")
    val value: String? = null
) {
    // JPA için default constructor
    constructor() : this(
        id = UUID.randomUUID(),
        key = "",
        value = null
    )
}

@Entity
@Table(
    name = "track_tags",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_track_tags", columnNames = ["track_id", "tag_id"])
    ]
)
data class TrackTagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "track_id", columnDefinition = "UUID", nullable = false)
    val trackId: UUID = UUID.randomUUID(),
    
    @Column(name = "tag_id", columnDefinition = "UUID", nullable = false)
    val tagId: UUID = UUID.randomUUID()
) {
    // JPA için default constructor
    constructor() : this(
        id = null,
        trackId = UUID.randomUUID(),
        tagId = UUID.randomUUID()
    )
}

