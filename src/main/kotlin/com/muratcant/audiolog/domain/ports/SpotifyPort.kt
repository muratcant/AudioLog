package com.muratcant.audiolog.domain.ports

import com.muratcant.audiolog.domain.common.SpotifyId
import java.time.Instant

data class RecentlyPlayedItem(
    val track: SpotifyTrack,
    val playedAt: Instant,
    val contextType: String?,
    val contextUri: String?
)

data class SpotifyTrack(
    val id: SpotifyId,
    val name: String,
    val artists: List<SpotifyArtist>,
    val album: SpotifyAlbum?,
    val durationMs: Long,
    val explicit: Boolean,
    val popularity: Int?,
    val previewUrl: String?,
    val externalUrls: Map<String, String>
)

data class SpotifyArtist(
    val id: SpotifyId,
    val name: String,
    val genres: List<String>,
    val popularity: Int?,
    val imageUrl: String?
)

data class SpotifyAlbum(
    val id: SpotifyId,
    val name: String,
    val artistIds: List<SpotifyId>,
    val releaseDate: String?,
    val releaseDatePrecision: String?,
    val imageUrl: String?,
    val totalTracks: Int?
)

interface SpotifyPort {
    suspend fun getRecentlyPlayed(
        accessToken: String,
        limit: Int = 50,
        after: Instant? = null
    ): List<RecentlyPlayedItem>
    
    suspend fun getAudioFeatures(
        accessToken: String,
        trackIds: List<SpotifyId>
    ): Map<SpotifyId, AudioFeatures>
}

data class AudioFeatures(
    val trackId: SpotifyId,
    val acousticness: Float,
    val danceability: Float,
    val energy: Float,
    val instrumentalness: Float,
    val liveness: Float,
    val loudness: Float,
    val speechiness: Float,
    val tempo: Float,
    val valence: Float
)

