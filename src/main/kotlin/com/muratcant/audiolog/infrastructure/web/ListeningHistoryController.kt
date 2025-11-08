package com.muratcant.audiolog.infrastructure.web

import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.ports.ListeningSessionRepository
import com.muratcant.audiolog.domain.ports.TrackRepository
import com.muratcant.audiolog.domain.ports.ArtistRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/listening-history")
@Tag(name = "Listening History", description = "Dinleme geçmişini görüntüleme işlemleri")
class ListeningHistoryController(
    private val listeningSessionRepository: ListeningSessionRepository,
    private val trackRepository: TrackRepository,
    private val artistRepository: ArtistRepository
) {

    @GetMapping
    @Operation(
        summary = "Kullanıcının dinleme geçmişini getir",
        description = "Kullanıcının kaydedilmiş dinleme geçmişini track ve artist bilgileriyle birlikte döner. " +
                "En yeni dinlemeler önce gelir."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Dinleme geçmişi başarıyla getirildi",
                content = [Content(schema = Schema(implementation = ListeningHistoryResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Geçersiz istek parametreleri",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    fun getListeningHistory(
        @Parameter(description = "Kullanıcı UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @RequestParam userId: UUID,
        @Parameter(description = "Maksimum kayıt sayısı", required = false, example = "50")
        @RequestParam(required = false, defaultValue = "50") limit: Int
    ): ResponseEntity<ListeningHistoryResponse> {
        val sessions = listeningSessionRepository.findByUserId(UserId(userId), limit)
        
        val historyItems = sessions.map { session ->
            val track = trackRepository.findById(session.trackId)
            val artists = track?.artistIds?.mapNotNull { artistId ->
                artistRepository.findById(artistId)
            } ?: emptyList()
            
            ListeningHistoryItem(
                sessionId = session.id.value,
                playedAt = session.playedAt.value,
                contextType = session.contextType,
                contextUri = session.contextUri,
                track = track?.let {
                    TrackInfo(
                        id = it.id.value,
                        spotifyId = it.spotifyId.value,
                        name = it.name,
                        artists = artists.map { artist ->
                            ArtistInfo(
                                id = artist.id.value,
                                spotifyId = artist.spotifyId.value,
                                name = artist.name
                            )
                        },
                        albumId = it.albumId?.value,
                        durationMs = it.duration.milliseconds,
                        explicit = it.explicit,
                        popularity = it.popularity,
                        previewUrl = it.previewUrl,
                        externalUrls = it.externalUrls
                    )
                }
            )
        }
        
        return ResponseEntity.ok(
            ListeningHistoryResponse(
                userId = userId,
                totalCount = historyItems.size,
                items = historyItems
            )
        )
    }
}

@Schema(description = "Dinleme geçmişi yanıtı")
data class ListeningHistoryResponse(
    @Schema(description = "Kullanıcı UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    val userId: UUID,
    @Schema(description = "Toplam kayıt sayısı", example = "50")
    val totalCount: Int,
    @Schema(description = "Dinleme geçmişi kayıtları")
    val items: List<ListeningHistoryItem>
)

@Schema(description = "Dinleme geçmişi kaydı")
data class ListeningHistoryItem(
    @Schema(description = "Session UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    val sessionId: UUID,
    @Schema(description = "Dinlenme zamanı (ISO 8601)", example = "2025-11-08T14:30:00Z")
    val playedAt: java.time.Instant,
    @Schema(description = "Context tipi (playlist, album, vb.)", example = "playlist")
    val contextType: String?,
    @Schema(description = "Context URI", example = "spotify:playlist:37i9dQZF1DXcBWIGoYBM5M")
    val contextUri: String?,
    @Schema(description = "Şarkı bilgileri")
    val track: TrackInfo?
)

@Schema(description = "Şarkı bilgileri")
data class TrackInfo(
    @Schema(description = "Track UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID,
    @Schema(description = "Spotify Track ID", example = "4iV5W9uYEdYUVa79Axb7Rh")
    val spotifyId: String,
    @Schema(description = "Şarkı adı", example = "Blinding Lights")
    val name: String,
    @Schema(description = "Sanatçılar")
    val artists: List<ArtistInfo>,
    @Schema(description = "Album UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    val albumId: UUID?,
    @Schema(description = "Süre (milisaniye)", example = "200000")
    val durationMs: Long,
    @Schema(description = "Açık içerik", example = "false")
    val explicit: Boolean,
    @Schema(description = "Popülerlik skoru (0-100)", example = "85")
    val popularity: Int?,
    @Schema(description = "Önizleme URL'i", example = "https://p.scdn.co/mp3-preview/...")
    val previewUrl: String?,
    @Schema(description = "Harici URL'ler (Spotify, vb.)")
    val externalUrls: Map<String, String>
)

@Schema(description = "Sanatçı bilgileri")
data class ArtistInfo(
    @Schema(description = "Artist UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID,
    @Schema(description = "Spotify Artist ID", example = "1Xyo4u8uXC1ZmMpatF05PJ")
    val spotifyId: String,
    @Schema(description = "Sanatçı adı", example = "The Weeknd")
    val name: String
)

