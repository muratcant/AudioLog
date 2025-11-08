package com.muratcant.audiolog.infrastructure.web

import com.muratcant.audiolog.application.curation.AddAnnotation
import com.muratcant.audiolog.application.curation.RateTrack
import com.muratcant.audiolog.domain.common.TrackId
import com.muratcant.audiolog.domain.common.UserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/tracks")
@Tag(name = "Curation", description = "Şarkılara not ekleme ve değerlendirme işlemleri")
class CurationController(
    private val addAnnotation: AddAnnotation,
    private val rateTrack: RateTrack
) {
    
    @PostMapping("/{trackId}/annotations")
    @Operation(
        summary = "Şarkıya not ekle",
        description = "Belirtilen şarkıya kullanıcı notu ekler. Bir şarkıya birden fazla not eklenebilir."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Not başarıyla oluşturuldu",
                content = [Content(schema = Schema(implementation = AnnotationResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Geçersiz istek (boş içerik vb.)",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Kullanıcı veya şarkı bulunamadı",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    fun addAnnotation(
        @Parameter(description = "Şarkı UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable trackId: UUID,
        @Parameter(description = "Kullanıcı UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @RequestParam userId: UUID,
        @Parameter(description = "Not içeriği", required = true)
        @RequestBody request: AddAnnotationRequest
    ): ResponseEntity<AnnotationResponse> {
        val annotation = addAnnotation.execute(
            userId = UserId(userId),
            trackId = TrackId(trackId),
            content = request.content
        )
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(AnnotationResponse(
                id = annotation.id.value,
                trackId = annotation.trackId.value,
                userId = annotation.userId.value,
                content = annotation.content,
                createdAt = annotation.createdAt.value,
                updatedAt = annotation.updatedAt.value
            ))
    }
    
    @PostMapping("/{trackId}/ratings")
    @Operation(
        summary = "Şarkıyı değerlendir",
        description = "Belirtilen şarkıyı kullanıcı değerlendirir. " +
                "Genel puan (1-5) veya boyut bazlı puanlar (vocal, mood, lyrics) verilebilir. " +
                "Aynı kullanıcı aynı şarkıyı tekrar değerlendirirse, önceki değerlendirme güncellenir."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Değerlendirme başarıyla oluşturuldu/güncellendi",
                content = [Content(schema = Schema(implementation = RatingResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Geçersiz istek (puan aralığı dışında vb.)",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Kullanıcı veya şarkı bulunamadı",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    fun rateTrack(
        @Parameter(description = "Şarkı UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable trackId: UUID,
        @Parameter(description = "Kullanıcı UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @RequestParam userId: UUID,
        @Parameter(description = "Değerlendirme bilgileri", required = true)
        @RequestBody request: RateTrackRequest
    ): ResponseEntity<RatingResponse> {
        val rating = rateTrack.execute(
            userId = UserId(userId),
            trackId = TrackId(trackId),
            overallScore = request.overallScore,
            vocal = request.vocal,
            mood = request.mood,
            lyrics = request.lyrics
        )
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(RatingResponse(
                id = rating.id.value,
                trackId = rating.trackId.value,
                userId = rating.userId.value,
                overallScore = rating.overallScore,
                vocal = rating.dimensions?.vocal,
                mood = rating.dimensions?.mood,
                lyrics = rating.dimensions?.lyrics,
                createdAt = rating.createdAt.value,
                updatedAt = rating.updatedAt.value
            ))
    }
}

@Schema(description = "Şarkıya eklenecek not isteği")
data class AddAnnotationRequest(
    @Schema(description = "Not içeriği", example = "Bu şarkının introsundaki gitar tonu rüya gibi.", required = true)
    val content: String
)

@Schema(description = "Şarkı notu yanıtı")
data class AnnotationResponse(
    @Schema(description = "Not UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID,
    @Schema(description = "Şarkı UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    val trackId: UUID,
    @Schema(description = "Kullanıcı UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    val userId: UUID,
    @Schema(description = "Not içeriği", example = "Bu şarkının introsundaki gitar tonu rüya gibi.")
    val content: String,
    @Schema(description = "Oluşturulma zamanı")
    val createdAt: java.time.Instant,
    @Schema(description = "Güncellenme zamanı")
    val updatedAt: java.time.Instant
)

@Schema(description = "Şarkı değerlendirme isteği")
data class RateTrackRequest(
    @Schema(description = "Genel puan (1-5)", example = "4", minimum = "1", maximum = "5")
    val overallScore: Int? = null,
    @Schema(description = "Vokal puanı (1-5)", example = "5", minimum = "1", maximum = "5")
    val vocal: Int? = null,
    @Schema(description = "Mood puanı (1-5)", example = "3", minimum = "1", maximum = "5")
    val mood: Int? = null,
    @Schema(description = "Söz puanı (1-5)", example = "4", minimum = "1", maximum = "5")
    val lyrics: Int? = null
)

@Schema(description = "Şarkı değerlendirme yanıtı")
data class RatingResponse(
    @Schema(description = "Değerlendirme UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID,
    @Schema(description = "Şarkı UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    val trackId: UUID,
    @Schema(description = "Kullanıcı UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    val userId: UUID,
    @Schema(description = "Genel puan (1-5)", example = "4", nullable = true)
    val overallScore: Int?,
    @Schema(description = "Vokal puanı (1-5)", example = "5", nullable = true)
    val vocal: Int?,
    @Schema(description = "Mood puanı (1-5)", example = "3", nullable = true)
    val mood: Int?,
    @Schema(description = "Söz puanı (1-5)", example = "4", nullable = true)
    val lyrics: Int?,
    @Schema(description = "Oluşturulma zamanı")
    val createdAt: java.time.Instant,
    @Schema(description = "Güncellenme zamanı")
    val updatedAt: java.time.Instant
)

