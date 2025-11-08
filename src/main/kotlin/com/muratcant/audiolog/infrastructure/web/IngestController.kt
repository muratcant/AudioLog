package com.muratcant.audiolog.infrastructure.web

import com.muratcant.audiolog.application.ingest.IngestRecentlyPlayed
import com.muratcant.audiolog.domain.common.UserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/ingest")
@Tag(name = "Ingest", description = "Spotify dinleme geçmişini çekme ve veritabanına aktarma işlemleri")
class IngestController(
    private val ingestRecentlyPlayed: IngestRecentlyPlayed
) {
    
    @RequestMapping(
        value = ["/recently-played"],
        method = [RequestMethod.GET, RequestMethod.POST]
    )
    @Operation(
        summary = "Spotify dinleme geçmişini çek",
        description = "Spotify API'den kullanıcının son dinlediği şarkıları çeker ve veritabanına kaydeder. " +
                "Aynı session'lar tekrar kaydedilmez. " +
                "Kullanıcının access token'ı veritabanından otomatik olarak çekilir."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "202",
                description = "İşlem başarıyla kabul edildi",
                content = [Content(schema = Schema(implementation = IngestResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Geçersiz istek parametreleri",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Kullanıcı bulunamadı",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "502",
                description = "Spotify API hatası",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    fun ingestRecentlyPlayed(
        @Parameter(description = "Kullanıcı UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @RequestParam userId: UUID,
        @Parameter(description = "Unix timestamp (saniye) - Bu tarihten sonraki dinlemeleri çeker", required = false)
        @RequestParam(required = false) since: Long?
    ): ResponseEntity<IngestResponse> {
        val sinceInstant = since?.let { Instant.ofEpochSecond(it) }
        val result = runBlocking {
            ingestRecentlyPlayed.execute(
                userId = UserId(userId),
                since = sinceInstant
            )
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(IngestResponse(
                ingested = result.ingested,
                message = if (result.ingested > 0) {
                    "Successfully ingested ${result.ingested} new listening session(s)"
                } else {
                    "No new listening sessions to ingest (all sessions already exist)"
                }
            ))
    }
}

@Schema(description = "Ingest işlemi yanıtı")
data class IngestResponse(
    @Schema(description = "Kaydedilen yeni dinleme session sayısı", example = "15")
    val ingested: Int,
    @Schema(description = "İşlem durumu mesajı", example = "Successfully ingested 15 listening sessions")
    val message: String = "Successfully ingested $ingested listening sessions"
)

