package com.muratcant.audiolog.application.reporting

import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.ports.ListeningSessionRepository
import com.muratcant.audiolog.domain.ports.TrackRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class GetStatistics(
    private val listeningSessionRepository: ListeningSessionRepository,
    private val trackRepository: TrackRepository
) {
    
    fun execute(
        userId: UserId,
        startDate: Instant? = null,
        endDate: Instant? = null
    ): StatisticsResult {
        val sessions = if (startDate != null && endDate != null) {
            listeningSessionRepository.findByUserIdAndPlayedAtBetween(userId, startDate, endDate)
        } else if (startDate != null) {
            listeningSessionRepository.findByUserIdAndPlayedAtAfter(userId, startDate)
        } else {
            listeningSessionRepository.findByUserId(userId, limit = 10000)
        }
        
        if (sessions.isEmpty()) {
            return StatisticsResult(
                totalSessions = 0,
                uniqueTracks = 0,
                uniqueArtists = 0,
                totalListeningTimeMs = 0,
                averageSessionsPerDay = 0.0,
                periodStart = startDate ?: Instant.now(),
                periodEnd = endDate ?: Instant.now()
            )
        }
        
        val uniqueTracks = sessions.map { it.trackId }.distinct().size
        val uniqueArtists = sessions.mapNotNull { session ->
            trackRepository.findById(session.trackId)?.artistIds
        }.flatten().distinct().size
        
        val totalListeningTimeMs = sessions.sumOf { session ->
            trackRepository.findById(session.trackId)?.duration?.milliseconds ?: 0L
        }
        
        val periodStart = startDate ?: sessions.minOfOrNull { it.playedAt.value } ?: Instant.now()
        val periodEnd = endDate ?: sessions.maxOfOrNull { it.playedAt.value } ?: Instant.now()
        val daysBetween = ChronoUnit.DAYS.between(periodStart, periodEnd) + 1
        val averageSessionsPerDay = if (daysBetween > 0) {
            sessions.size.toDouble() / daysBetween
        } else {
            sessions.size.toDouble()
        }
        
        return StatisticsResult(
            totalSessions = sessions.size,
            uniqueTracks = uniqueTracks,
            uniqueArtists = uniqueArtists,
            totalListeningTimeMs = totalListeningTimeMs,
            averageSessionsPerDay = averageSessionsPerDay,
            periodStart = periodStart,
            periodEnd = periodEnd
        )
    }
}

data class StatisticsResult(
    val totalSessions: Int,
    val uniqueTracks: Int,
    val uniqueArtists: Int,
    val totalListeningTimeMs: Long,
    val averageSessionsPerDay: Double,
    val periodStart: Instant,
    val periodEnd: Instant
)

