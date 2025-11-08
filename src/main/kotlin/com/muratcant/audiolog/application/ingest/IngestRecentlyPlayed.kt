package com.muratcant.audiolog.application.ingest

import com.muratcant.audiolog.domain.common.*
import com.muratcant.audiolog.domain.music.Artist
import com.muratcant.audiolog.domain.music.Album
import com.muratcant.audiolog.domain.music.Track
import com.muratcant.audiolog.domain.session.ListeningSession
import com.muratcant.audiolog.domain.ports.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class IngestRecentlyPlayed(
    private val spotifyPort: SpotifyPort,
    private val userRepository: UserRepository,
    private val trackRepository: TrackRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val listeningSessionRepository: ListeningSessionRepository
) {
    
    @Transactional
    suspend fun execute(userId: UserId, since: Instant?): IngestResult {
        val user = userRepository.findById(userId)
            ?: throw DomainError.NotFound("User not found: ${userId.value}")
        
        val accessToken = user.accessToken
            ?: throw DomainError.ValidationError("User has no access token. Please re-authenticate.")
        
        if (user.isTokenExpired()) {
            throw DomainError.ValidationError("Access token expired. Please re-authenticate.")
        }
        
        val recentlyPlayed = spotifyPort.getRecentlyPlayed(accessToken, limit = 50, after = since)
        
        var ingestedCount = 0
        
        for (item in recentlyPlayed) {
            val trackSpotifyId = item.track.id
            val playedAt = item.playedAt
            
            // First, ensure track exists (will create if not)
            val track = ensureTrackExists(item.track)
            
            // Check if session already exists
            if (!listeningSessionRepository.existsByUserIdAndTrackIdAndPlayedAt(
                userId, track.id, playedAt
            )) {
                val session = ListeningSession(
                    id = ListeningSessionId(),
                    userId = userId,
                    trackId = track.id,
                    playedAt = Timestamp(playedAt),
                    contextType = item.contextType,
                    contextUri = item.contextUri
                )
                listeningSessionRepository.save(session)
                ingestedCount++
            }
        }
        
        return IngestResult(ingestedCount)
    }
    
    private suspend fun ensureTrackExists(spotifyTrack: SpotifyTrack): Track {
        // Check if track exists
        val existingTrack = trackRepository.findBySpotifyId(spotifyTrack.id)
        if (existingTrack != null) {
            return existingTrack
        }
        
        // Ensure artists exist
        val artists = spotifyTrack.artists.map { spotifyArtist ->
            artistRepository.findBySpotifyId(spotifyArtist.id) ?: run {
                val artist = Artist(
                    id = ArtistId(),
                    spotifyId = spotifyArtist.id,
                    name = spotifyArtist.name,
                    genres = spotifyArtist.genres,
                    popularity = spotifyArtist.popularity,
                    imageUrl = spotifyArtist.imageUrl
                )
                artistRepository.save(artist)
            }
        }
        
        // Ensure album exists if present
        val album = spotifyTrack.album?.let { spotifyAlbum ->
            albumRepository.findBySpotifyId(spotifyAlbum.id) ?: run {
                val newAlbum = Album(
                    id = AlbumId(),
                    spotifyId = spotifyAlbum.id,
                    name = spotifyAlbum.name,
                    artistIds = spotifyAlbum.artistIds.map { spotifyId ->
                        artists.find { it.spotifyId == spotifyId }?.id
                            ?: throw DomainError.NotFound("Artist not found for album: $spotifyId")
                    },
                    releaseDate = spotifyAlbum.releaseDate,
                    releaseDatePrecision = spotifyAlbum.releaseDatePrecision,
                    imageUrl = spotifyAlbum.imageUrl,
                    totalTracks = spotifyAlbum.totalTracks
                )
                albumRepository.save(newAlbum)
            }
        }
        
        // Create track
        val track = Track(
            id = TrackId(),
            spotifyId = spotifyTrack.id,
            name = spotifyTrack.name,
            artistIds = artists.map { it.id },
            albumId = album?.id,
            duration = Duration(spotifyTrack.durationMs),
            explicit = spotifyTrack.explicit,
            popularity = spotifyTrack.popularity,
            previewUrl = spotifyTrack.previewUrl,
            externalUrls = spotifyTrack.externalUrls
        )
        
        return trackRepository.save(track)
    }
}

data class IngestResult(val ingested: Int)

