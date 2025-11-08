package com.muratcant.audiolog.application.reporting

import com.muratcant.audiolog.domain.common.ArtistId
import com.muratcant.audiolog.domain.common.SpotifyId
import com.muratcant.audiolog.domain.common.TrackId
import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.music.Artist
import com.muratcant.audiolog.domain.music.Track
import com.muratcant.audiolog.domain.ports.ArtistRepository
import com.muratcant.audiolog.domain.ports.ListeningSessionRepository
import com.muratcant.audiolog.domain.ports.TrackRepository
import com.muratcant.audiolog.domain.session.ListeningSession
import com.muratcant.audiolog.domain.common.Timestamp
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.*

class GetTopArtistsTest : DescribeSpec({
    
    val listeningSessionRepository = mockk<ListeningSessionRepository>()
    val trackRepository = mockk<TrackRepository>()
    val artistRepository = mockk<ArtistRepository>()
    
    val getTopArtists = GetTopArtists(
        listeningSessionRepository = listeningSessionRepository,
        trackRepository = trackRepository,
        artistRepository = artistRepository
    )
    
    describe("GetTopArtists") {
        
        context("when user has listening sessions") {
            val userId = UserId(UUID.randomUUID())
            val artist1Id = ArtistId(UUID.randomUUID())
            val artist2Id = ArtistId(UUID.randomUUID())
            val track1Id = TrackId(UUID.randomUUID())
            val track2Id = TrackId(UUID.randomUUID())
            
            val artist1 = Artist(
                id = artist1Id,
                spotifyId = SpotifyId("artist1"),
                name = "Artist 1",
                genres = emptyList(),
                popularity = 80,
                imageUrl = "https://example.com/artist1.jpg"
            )
            
            val artist2 = Artist(
                id = artist2Id,
                spotifyId = SpotifyId("artist2"),
                name = "Artist 2",
                genres = emptyList(),
                popularity = 70,
                imageUrl = "https://example.com/artist2.jpg"
            )
            
            val track1 = Track(
                id = track1Id,
                spotifyId = SpotifyId("track1"),
                name = "Track 1",
                artistIds = listOf(artist1Id),
                albumId = null,
                duration = com.muratcant.audiolog.domain.common.Duration(200000),
                explicit = false,
                popularity = 85,
                previewUrl = null,
                externalUrls = emptyMap()
            )
            
            val track2 = Track(
                id = track2Id,
                spotifyId = SpotifyId("track2"),
                name = "Track 2",
                artistIds = listOf(artist2Id),
                albumId = null,
                duration = com.muratcant.audiolog.domain.common.Duration(180000),
                explicit = false,
                popularity = 75,
                previewUrl = null,
                externalUrls = emptyMap()
            )
            
            val session1 = ListeningSession(
                id = com.muratcant.audiolog.domain.common.ListeningSessionId(),
                userId = userId,
                trackId = track1Id,
                playedAt = Timestamp(Instant.now()),
                contextType = "playlist",
                contextUri = "spotify:playlist:123"
            )
            
            val session2 = ListeningSession(
                id = com.muratcant.audiolog.domain.common.ListeningSessionId(),
                userId = userId,
                trackId = track1Id,
                playedAt = Timestamp(Instant.now().minusSeconds(3600)),
                contextType = "album",
                contextUri = "spotify:album:456"
            )
            
            val session3 = ListeningSession(
                id = com.muratcant.audiolog.domain.common.ListeningSessionId(),
                userId = userId,
                trackId = track2Id,
                playedAt = Timestamp(Instant.now().minusSeconds(7200)),
                contextType = null,
                contextUri = null
            )
            
            it("should return top artists sorted by play count") {
                // Given
                every { listeningSessionRepository.findByUserId(userId, 10000) } returns listOf(session1, session2, session3)
                every { trackRepository.findById(track1Id) } returns track1
                every { trackRepository.findById(track2Id) } returns track2
                every { artistRepository.findById(artist1Id) } returns artist1
                every { artistRepository.findById(artist2Id) } returns artist2
                
                // When
                val result = getTopArtists.execute(userId = userId, limit = 10)
                
                // Then
                result.size shouldBe 2
                result[0].name shouldBe "Artist 1"
                result[0].playCount shouldBe 2 // track1 iki kez dinlenmiş
                result[1].name shouldBe "Artist 2"
                result[1].playCount shouldBe 1 // track2 bir kez dinlenmiş
            }
            
            it("should respect limit parameter") {
                // Given
                every { listeningSessionRepository.findByUserId(userId, 10000) } returns listOf(session1, session2, session3)
                every { trackRepository.findById(track1Id) } returns track1
                every { trackRepository.findById(track2Id) } returns track2
                every { artistRepository.findById(artist1Id) } returns artist1
                every { artistRepository.findById(artist2Id) } returns artist2
                
                // When
                val result = getTopArtists.execute(userId = userId, limit = 1)
                
                // Then
                result.size shouldBe 1
                result[0].name shouldBe "Artist 1"
            }
        }
        
        context("when user has no listening sessions") {
            val userId = UserId(UUID.randomUUID())
            
            it("should return empty list") {
                // Given
                every { listeningSessionRepository.findByUserId(userId, 10000) } returns emptyList()
                
                // When
                val result = getTopArtists.execute(userId = userId, limit = 10)
                
                // Then
                result shouldBe emptyList()
            }
        }
        
        context("when track is not found") {
            val userId = UserId(UUID.randomUUID())
            val trackId = TrackId(UUID.randomUUID())
            
            val session = ListeningSession(
                id = com.muratcant.audiolog.domain.common.ListeningSessionId(),
                userId = userId,
                trackId = trackId,
                playedAt = Timestamp(Instant.now()),
                contextType = null,
                contextUri = null
            )
            
            it("should skip missing tracks") {
                // Given
                every { listeningSessionRepository.findByUserId(userId, 10000) } returns listOf(session)
                every { trackRepository.findById(trackId) } returns null
                
                // When
                val result = getTopArtists.execute(userId = userId, limit = 10)
                
                // Then
                result shouldBe emptyList()
            }
        }
    }
})

