package com.muratcant.audiolog.domain.user

import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.common.SpotifyId

data class User(
    val id: UserId,
    val spotifyId: SpotifyId,
    val displayName: String?,
    val email: String?,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val tokenExpiresAt: java.time.Instant? = null
) {
    fun isTokenExpired(): Boolean {
        return tokenExpiresAt?.isBefore(java.time.Instant.now()) ?: true
    }
}

