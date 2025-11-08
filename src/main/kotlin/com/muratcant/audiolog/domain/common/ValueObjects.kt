package com.muratcant.audiolog.domain.common

import java.time.Instant

data class SpotifyId(val value: String) {
    init {
        require(value.isNotBlank()) { "Spotify ID cannot be blank" }
    }
}

data class Timestamp(val value: Instant) {
    companion object {
        fun now() = Timestamp(Instant.now())
    }
}

data class Duration(val milliseconds: Long) {
    init {
        require(milliseconds >= 0) { "Duration cannot be negative" }
    }
    
    fun toSeconds(): Long = milliseconds / 1000
}

