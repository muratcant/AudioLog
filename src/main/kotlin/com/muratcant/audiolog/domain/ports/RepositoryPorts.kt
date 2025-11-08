package com.muratcant.audiolog.domain.ports

import com.muratcant.audiolog.domain.common.*
import com.muratcant.audiolog.domain.user.User
import com.muratcant.audiolog.domain.music.Artist
import com.muratcant.audiolog.domain.music.Album
import com.muratcant.audiolog.domain.music.Track
import com.muratcant.audiolog.domain.session.ListeningSession
import com.muratcant.audiolog.domain.curation.Annotation
import com.muratcant.audiolog.domain.curation.Rating
import com.muratcant.audiolog.domain.curation.Tag
import com.muratcant.audiolog.domain.playlist.Playlist
import java.time.Instant

interface UserRepository {
    fun findById(id: UserId): User?
    fun findBySpotifyId(spotifyId: SpotifyId): User?
    fun save(user: User): User
}

interface TrackRepository {
    fun findById(id: TrackId): Track?
    fun findBySpotifyId(spotifyId: SpotifyId): Track?
    fun save(track: Track): Track
    fun saveAll(tracks: List<Track>): List<Track>
}

interface ArtistRepository {
    fun findById(id: ArtistId): Artist?
    fun findBySpotifyId(spotifyId: SpotifyId): Artist?
    fun save(artist: Artist): Artist
    fun saveAll(artists: List<Artist>): List<Artist>
}

interface AlbumRepository {
    fun findById(id: AlbumId): Album?
    fun findBySpotifyId(spotifyId: SpotifyId): Album?
    fun save(album: Album): Album
    fun saveAll(albums: List<Album>): List<Album>
}

interface ListeningSessionRepository {
    fun findById(id: ListeningSessionId): ListeningSession?
    fun findByUserId(userId: UserId, limit: Int = 50): List<ListeningSession>
    fun findByUserIdAndPlayedAtAfter(userId: UserId, after: Instant): List<ListeningSession>
    fun findByUserIdAndPlayedAtBetween(userId: UserId, start: Instant, end: Instant): List<ListeningSession>
    fun save(session: ListeningSession): ListeningSession
    fun saveAll(sessions: List<ListeningSession>): List<ListeningSession>
    fun existsByUserIdAndTrackIdAndPlayedAt(userId: UserId, trackId: TrackId, playedAt: Instant): Boolean
}

interface AnnotationRepository {
    fun findById(id: AnnotationId): Annotation?
    fun findByUserIdAndTrackId(userId: UserId, trackId: TrackId): List<Annotation>
    fun save(annotation: Annotation): Annotation
    fun delete(id: AnnotationId)
}

interface RatingRepository {
    fun findById(id: RatingId): Rating?
    fun findByUserIdAndTrackId(userId: UserId, trackId: TrackId): Rating?
    fun save(rating: Rating): Rating
}

interface TagRepository {
    fun findById(id: TagId): Tag?
    fun findByKey(key: String): Tag?
    fun save(tag: Tag): Tag
    fun saveAll(tags: List<Tag>): List<Tag>
}

interface PlaylistRepository {
    fun findById(id: PlaylistId): Playlist?
    fun findByUserId(userId: UserId): List<Playlist>
    fun save(playlist: Playlist): Playlist
}

