package com.muratcant.audiolog.application.reporting

import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.ports.ListeningSessionRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId

@Service
class GetHourlyAnalysis(
    private val listeningSessionRepository: ListeningSessionRepository
) {
    
    fun execute(
        userId: UserId,
        startDate: Instant? = null,
        endDate: Instant? = null
    ): List<HourlyStatistic> {
        val sessions = if (startDate != null && endDate != null) {
            listeningSessionRepository.findByUserIdAndPlayedAtBetween(userId, startDate, endDate)
        } else if (startDate != null) {
            listeningSessionRepository.findByUserIdAndPlayedAtAfter(userId, startDate)
        } else {
            // Get all sessions for accurate stats
            listeningSessionRepository.findByUserId(userId, limit = 10000)
        }
        
        // Group by hour (0-23)
        val hourlyCounts = IntArray(24) { 0 }
        
        for (session in sessions) {
            val hour = session.playedAt.value.atZone(ZoneId.systemDefault()).hour
            hourlyCounts[hour]++
        }
        
        return hourlyCounts.mapIndexed { hour, count ->
            HourlyStatistic(
                hour = hour,
                playCount = count,
                percentage = if (sessions.isNotEmpty()) {
                    (count.toDouble() / sessions.size * 100).toInt()
                } else 0
            )
        }
    }
}

data class HourlyStatistic(
    val hour: Int, // 0-23
    val playCount: Int,
    val percentage: Int
)

