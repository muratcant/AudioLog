package com.muratcant.audiolog.infrastructure.spotify

import com.muratcant.audiolog.domain.common.SpotifyId
import com.muratcant.audiolog.domain.ports.SpotifyPort
import com.muratcant.audiolog.domain.ports.AudioFeatures
import com.muratcant.audiolog.domain.ports.RecentlyPlayedItem
import com.muratcant.audiolog.domain.ports.SpotifyAlbum
import com.muratcant.audiolog.domain.ports.SpotifyArtist
import com.muratcant.audiolog.domain.ports.SpotifyTrack
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.Instant
import java.time.ZoneOffset

class SpotifyWebClient(
    private val webClient: WebClient,
    private val retry: Retry
) : SpotifyPort {
    
    override suspend fun getRecentlyPlayed(
        accessToken: String,
        limit: Int,
        after: Instant?
    ): List<RecentlyPlayedItem> {
        return retry.executeSuspendFunction {
            val uriBuilder = webClient.get()
                .uri { builder ->
                    builder.path("/v1/me/player/recently-played")
                        .queryParam("limit", limit)
                        after?.let { builder.queryParam("after", it.epochSecond * 1000) }
                    builder.build()
                }
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            
            val response = uriBuilder.retrieve()
                .bodyToMono(RecentlyPlayedResponse::class.java)
                .awaitSingle()
            
            response.items.map { item ->
                RecentlyPlayedItem(
                    track = item.track.toDomain(),
                    playedAt = Instant.ofEpochMilli(item.playedAt),
                    contextType = item.context?.type,
                    contextUri = item.context?.uri
                )
            }
        }
    }
    
    override suspend fun getAudioFeatures(
        accessToken: String,
        trackIds: List<SpotifyId>
    ): Map<SpotifyId, AudioFeatures> {
        if (trackIds.isEmpty()) return emptyMap()
        
        val chunks = trackIds.chunked(100)
        val allFeatures = mutableMapOf<SpotifyId, AudioFeatures>()
        
        for (chunk in chunks) {
            val ids = chunk.joinToString(",") { it.value }
            val response = retry.executeSuspendFunction {
                webClient.get()
                    .uri { builder ->
                        builder.path("/v1/audio-features")
                            .queryParam("ids", ids)
                        builder.build()
                    }
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                    .retrieve()
                    .bodyToMono(AudioFeaturesResponse::class.java)
                    .awaitSingle()
            }
            
            response.audioFeatures.forEach { feature ->
                val trackId = SpotifyId(feature.id)
                allFeatures[trackId] = AudioFeatures(
                    trackId = trackId,
                    acousticness = feature.acousticness,
                    danceability = feature.danceability,
                    energy = feature.energy,
                    instrumentalness = feature.instrumentalness,
                    liveness = feature.liveness,
                    loudness = feature.loudness,
                    speechiness = feature.speechiness,
                    tempo = feature.tempo,
                    valence = feature.valence
                )
            }
        }
        
        return allFeatures
    }
}

// Response DTOs
data class RecentlyPlayedResponse(
    val items: List<RecentlyPlayedItemResponse>
)

data class RecentlyPlayedItemResponse(
    val track: TrackResponse,
    val playedAt: Long,
    val context: ContextResponse?
)

data class TrackResponse(
    val id: String,
    val name: String,
    val artists: List<ArtistResponse>,
    val album: AlbumResponse?,
    @JsonProperty("duration_ms")
    val durationMs: Long,
    val explicit: Boolean,
    val popularity: Int?,
    @JsonProperty("preview_url")
    val previewUrl: String?,
    @JsonProperty("external_urls")
    val externalUrls: Map<String, String>? = emptyMap()
) {
    fun toDomain(): SpotifyTrack = SpotifyTrack(
        id = SpotifyId(id),
        name = name,
        artists = artists.map { it.toDomain() },
        album = album?.toDomain(),
        durationMs = durationMs,
        explicit = explicit,
        popularity = popularity,
        previewUrl = previewUrl,
        externalUrls = externalUrls ?: emptyMap()
    )
}

data class ArtistResponse(
    val id: String,
    val name: String,
    val genres: List<String> = emptyList(),
    val popularity: Int? = null,
    val images: List<ImageResponse> = emptyList()
) {
    fun toDomain(): SpotifyArtist = SpotifyArtist(
        id = SpotifyId(id),
        name = name,
        genres = genres,
        popularity = popularity,
        imageUrl = images.firstOrNull()?.url
    )
}

data class AlbumResponse(
    val id: String,
    val name: String,
    val artists: List<ArtistResponse>,
    val releaseDate: String?,
    val releaseDatePrecision: String?,
    val images: List<ImageResponse> = emptyList(),
    val totalTracks: Int? = null
) {
    fun toDomain(): SpotifyAlbum = SpotifyAlbum(
        id = SpotifyId(id),
        name = name,
        artistIds = artists.map { SpotifyId(it.id) },
        releaseDate = releaseDate,
        releaseDatePrecision = releaseDatePrecision,
        imageUrl = images.firstOrNull()?.url,
        totalTracks = totalTracks
    )
}

data class ImageResponse(
    val url: String,
    val height: Int? = null,
    val width: Int? = null
)

data class ContextResponse(
    val type: String?,
    val uri: String?
)

data class AudioFeaturesResponse(
    val audioFeatures: List<AudioFeatureResponse>
)

data class AudioFeatureResponse(
    val id: String,
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

