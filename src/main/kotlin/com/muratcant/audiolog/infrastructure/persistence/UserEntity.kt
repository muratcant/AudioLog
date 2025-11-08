package com.muratcant.audiolog.infrastructure.persistence

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_users_spotify_id", columnNames = ["spotify_id"])
    ]
)
data class UserEntity(
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "spotify_id", unique = true, nullable = false)
    val spotifyId: String = "",
    
    @Column(name = "display_name")
    val displayName: String? = null,
    
    @Column(name = "email")
    val email: String? = null,
    
    @Column(name = "access_token")
    val accessToken: String? = null,
    
    @Column(name = "refresh_token")
    val refreshToken: String? = null,
    
    @Column(name = "token_expires_at")
    val tokenExpiresAt: Instant? = null
) {
    // JPA için default constructor
    constructor() : this(
        id = UUID.randomUUID(),
        spotifyId = "",
        displayName = null,
        email = null,
        accessToken = null,
        refreshToken = null,
        tokenExpiresAt = null
    )
}

