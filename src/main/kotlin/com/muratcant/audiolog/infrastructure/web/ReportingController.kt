package com.muratcant.audiolog.infrastructure.web

import com.muratcant.audiolog.application.reporting.GetTopArtists
import com.muratcant.audiolog.application.reporting.GetHourlyAnalysis
import com.muratcant.audiolog.application.reporting.GetStatistics
import com.muratcant.audiolog.domain.common.UserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/reports")
@Tag(name = "Reporting", description = "Dinleme geçmişi analizleri ve istatistikler")
class ReportingController(
    private val getTopArtists: GetTopArtists,
    private val getHourlyAnalysis: GetHourlyAnalysis,
    private val getStatistics: GetStatistics
) {

    @GetMapping("/top-artists")
    @Operation(
        summary = "En çok dinlenen sanatçılar",
        description = "Kullanıcının en çok dinlediği sanatçıları listeler. " +
                "İsteğe bağlı olarak tarih aralığı belirtilebilir."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Sanatçı listesi başarıyla getirildi",
                content = [Content(schema = Schema(implementation = TopArtistsResponse::class))]
            )
        ]
    )
    fun getTopArtists(
        @Parameter(description = "Kullanıcı UUID", required = true)
        @RequestParam userId: UUID,
        @Parameter(description = "Maksimum sanatçı sayısı", example = "10")
        @RequestParam(required = false, defaultValue = "10") limit: Int,
        @Parameter(description = "Başlangıç tarihi (Unix timestamp - saniye)", required = false)
        @RequestParam(required = false) startDate: Long?,
        @Parameter(description = "Bitiş tarihi (Unix timestamp - saniye)", required = false)
        @RequestParam(required = false) endDate: Long?
    ): ResponseEntity<TopArtistsResponse> {
        val start = startDate?.let { Instant.ofEpochSecond(it) }
        val end = endDate?.let { Instant.ofEpochSecond(it) }
        
        val topArtists = getTopArtists.execute(
            userId = UserId(userId),
            limit = limit,
            startDate = start,
            endDate = end
        )
        
        return ResponseEntity.ok(TopArtistsResponse(
            userId = userId,
            limit = limit,
            startDate = start,
            endDate = end,
            artists = topArtists.map { result ->
                TopArtistInfo(
                    artistId = result.artistId,
                    spotifyId = result.spotifyId,
                    name = result.name,
                    playCount = result.playCount,
                    imageUrl = result.imageUrl
                )
            }
        ))
    }

    @GetMapping("/hourly-analysis")
    @Operation(
        summary = "Günün saatine göre dinleme analizi",
        description = "Kullanıcının günün hangi saatlerinde daha çok müzik dinlediğini gösterir. " +
                "24 saatlik bir analiz döner (0-23 saatleri)."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Saatlik analiz başarıyla getirildi",
                content = [Content(schema = Schema(implementation = HourlyAnalysisResponse::class))]
            )
        ]
    )
    fun getHourlyAnalysis(
        @Parameter(description = "Kullanıcı UUID", required = true)
        @RequestParam userId: UUID,
        @Parameter(description = "Başlangıç tarihi (Unix timestamp - saniye)", required = false)
        @RequestParam(required = false) startDate: Long?,
        @Parameter(description = "Bitiş tarihi (Unix timestamp - saniye)", required = false)
        @RequestParam(required = false) endDate: Long?
    ): ResponseEntity<HourlyAnalysisResponse> {
        val start = startDate?.let { Instant.ofEpochSecond(it) }
        val end = endDate?.let { Instant.ofEpochSecond(it) }
        
        val hourlyStats = getHourlyAnalysis.execute(
            userId = UserId(userId),
            startDate = start,
            endDate = end
        )
        
        return ResponseEntity.ok(HourlyAnalysisResponse(
            userId = userId,
            startDate = start,
            endDate = end,
            hourlyStats = hourlyStats.map { stat ->
                HourlyStatInfo(
                    hour = stat.hour,
                    playCount = stat.playCount,
                    percentage = stat.percentage
                )
            }
        ))
    }

    @GetMapping("/statistics")
    @Operation(
        summary = "Genel dinleme istatistikleri",
        description = "Kullanıcının genel dinleme istatistiklerini döner: " +
                "toplam session sayısı, benzersiz şarkı/sanatçı sayısı, toplam dinleme süresi vb."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "İstatistikler başarıyla getirildi",
                content = [Content(schema = Schema(implementation = StatisticsResponse::class))]
            )
        ]
    )
    fun getStatistics(
        @Parameter(description = "Kullanıcı UUID", required = true)
        @RequestParam userId: UUID,
        @Parameter(description = "Başlangıç tarihi (Unix timestamp - saniye)", required = false)
        @RequestParam(required = false) startDate: Long?,
        @Parameter(description = "Bitiş tarihi (Unix timestamp - saniye)", required = false)
        @RequestParam(required = false) endDate: Long?
    ): ResponseEntity<StatisticsResponse> {
        val start = startDate?.let { Instant.ofEpochSecond(it) }
        val end = endDate?.let { Instant.ofEpochSecond(it) }
        
        val stats = getStatistics.execute(
            userId = UserId(userId),
            startDate = start,
            endDate = end
        )
        
        return ResponseEntity.ok(StatisticsResponse(
            userId = userId,
            totalSessions = stats.totalSessions,
            uniqueTracks = stats.uniqueTracks,
            uniqueArtists = stats.uniqueArtists,
            totalListeningTimeMs = stats.totalListeningTimeMs,
            totalListeningTimeHours = stats.totalListeningTimeMs / 1000.0 / 60.0 / 60.0,
            averageSessionsPerDay = stats.averageSessionsPerDay,
            periodStart = stats.periodStart,
            periodEnd = stats.periodEnd
        ))
    }
}

@Schema(description = "En çok dinlenen sanatçılar yanıtı")
data class TopArtistsResponse(
    @Schema(description = "Kullanıcı UUID")
    val userId: UUID,
    @Schema(description = "Maksimum sanatçı sayısı")
    val limit: Int,
    @Schema(description = "Başlangıç tarihi")
    val startDate: Instant?,
    @Schema(description = "Bitiş tarihi")
    val endDate: Instant?,
    @Schema(description = "Sanatçı listesi")
    val artists: List<TopArtistInfo>
)

@Schema(description = "Sanatçı bilgisi")
data class TopArtistInfo(
    @Schema(description = "Artist UUID")
    val artistId: UUID,
    @Schema(description = "Spotify Artist ID")
    val spotifyId: String,
    @Schema(description = "Sanatçı adı")
    val name: String,
    @Schema(description = "Dinlenme sayısı")
    val playCount: Int,
    @Schema(description = "Sanatçı görsel URL'i")
    val imageUrl: String?
)

@Schema(description = "Saatlik analiz yanıtı")
data class HourlyAnalysisResponse(
    @Schema(description = "Kullanıcı UUID")
    val userId: UUID,
    @Schema(description = "Başlangıç tarihi")
    val startDate: Instant?,
    @Schema(description = "Bitiş tarihi")
    val endDate: Instant?,
    @Schema(description = "Saatlik istatistikler (0-23)")
    val hourlyStats: List<HourlyStatInfo>
)

@Schema(description = "Saatlik istatistik")
data class HourlyStatInfo(
    @Schema(description = "Saat (0-23)", example = "14")
    val hour: Int,
    @Schema(description = "Dinlenme sayısı", example = "25")
    val playCount: Int,
    @Schema(description = "Yüzde (0-100)", example = "15")
    val percentage: Int
)

@Schema(description = "Genel istatistikler yanıtı")
data class StatisticsResponse(
    @Schema(description = "Kullanıcı UUID")
    val userId: UUID,
    @Schema(description = "Toplam dinleme session sayısı")
    val totalSessions: Int,
    @Schema(description = "Benzersiz şarkı sayısı")
    val uniqueTracks: Int,
    @Schema(description = "Benzersiz sanatçı sayısı")
    val uniqueArtists: Int,
    @Schema(description = "Toplam dinleme süresi (milisaniye)")
    val totalListeningTimeMs: Long,
    @Schema(description = "Toplam dinleme süresi (saat)")
    val totalListeningTimeHours: Double,
    @Schema(description = "Günlük ortalama session sayısı")
    val averageSessionsPerDay: Double,
    @Schema(description = "Analiz başlangıç tarihi")
    val periodStart: Instant,
    @Schema(description = "Analiz bitiş tarihi")
    val periodEnd: Instant
)

