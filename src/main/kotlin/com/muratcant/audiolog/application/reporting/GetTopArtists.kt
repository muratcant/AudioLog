package com.muratcant.audiolog.application.reporting

import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.ports.ListeningSessionRepository
import com.muratcant.audiolog.domain.ports.TrackRepository
import com.muratcant.audiolog.domain.ports.ArtistRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class GetTopArtists(
    private val listeningSessionRepository: ListeningSessionRepository,
    private val trackRepository: TrackRepository,
    private val artistRepository: ArtistRepository
) {
    
    fun execute(
        userId: UserId,
        limit: Int = 10,
        startDate: Instant? = null,
        endDate: Instant? = null
    ): List<TopArtistResult> {
        val sessions = if (startDate != null && endDate != null) {
            listeningSessionRepository.findByUserIdAndPlayedAtBetween(userId, startDate, endDate)
        } else if (startDate != null) {
            listeningSessionRepository.findByUserIdAndPlayedAtAfter(userId, startDate)
        } else {
            // Get all sessions for accurate stats (no limit for reporting)
            // Use a reasonable limit to avoid memory issues
            listeningSessionRepository.findByUserId(userId, limit = 10000)
        }
        
        // Group by artist and count
        val artistCounts = mutableMapOf<com.muratcant.audiolog.domain.common.ArtistId, Int>()
        
        for (session in sessions) {
            val track = trackRepository.findById(session.trackId) ?: continue
            for (artistId in track.artistIds) {
                artistCounts[artistId] = artistCounts.getOrDefault(artistId, 0) + 1
            }
        }
        
        // Get artist details and sort by count
        return artistCounts.entries
            .mapNotNull { (artistId, count) ->
                val artist = artistRepository.findById(artistId) ?: return@mapNotNull null
                TopArtistResult(
                    artistId = artist.id.value,
                    spotifyId = artist.spotifyId.value,
                    name = artist.name,
                    playCount = count,
                    imageUrl = artist.imageUrl
                )
            }
            .sortedByDescending { it.playCount }
            .take(limit)
    }
}

data class TopArtistResult(
    val artistId: java.util.UUID,
    val spotifyId: String,
    val name: String,
    val playCount: Int,
    val imageUrl: String?
)

