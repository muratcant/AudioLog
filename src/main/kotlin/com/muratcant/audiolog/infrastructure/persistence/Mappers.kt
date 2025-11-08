package com.muratcant.audiolog.infrastructure.persistence

import com.muratcant.audiolog.domain.common.*
import com.muratcant.audiolog.domain.music.Artist
import com.muratcant.audiolog.domain.music.Album
import com.muratcant.audiolog.domain.music.Track
import com.muratcant.audiolog.domain.session.ListeningSession
import com.muratcant.audiolog.domain.curation.Annotation
import com.muratcant.audiolog.domain.curation.Rating
import com.muratcant.audiolog.domain.curation.RatingDimension
import com.muratcant.audiolog.domain.user.User
import com.muratcant.audiolog.infrastructure.persistence.curation.*
import com.muratcant.audiolog.infrastructure.persistence.music.*
import com.muratcant.audiolog.infrastructure.persistence.playlist.*
import com.muratcant.audiolog.infrastructure.persistence.session.ListeningSessionEntity

// User mappers
fun UserEntity.toDomain(): User = User(
    id = UserId(id),
    spotifyId = SpotifyId(spotifyId),
    displayName = displayName,
    email = email,
    accessToken = accessToken,
    refreshToken = refreshToken,
    tokenExpiresAt = tokenExpiresAt
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id.value,
    spotifyId = spotifyId.value,
    displayName = displayName,
    email = email,
    accessToken = accessToken,
    refreshToken = refreshToken,
    tokenExpiresAt = tokenExpiresAt
)

// Artist mappers
fun ArtistEntity.toDomain(): Artist = Artist(
    id = ArtistId(id),
    spotifyId = SpotifyId(spotifyId),
    name = name,
    genres = genres.toList(),
    popularity = popularity,
    imageUrl = imageUrl
)

fun Artist.toEntity(): ArtistEntity = ArtistEntity(
    id = id.value,
    spotifyId = spotifyId.value,
    name = name,
    genres = genres.toMutableList(),
    popularity = popularity,
    imageUrl = imageUrl
)

// Album mappers
fun AlbumEntity.toDomain(): Album = Album(
    id = AlbumId(id),
    spotifyId = SpotifyId(spotifyId),
    name = name,
    artistIds = artistIds.map { ArtistId(it) }.toList(),
    releaseDate = releaseDate,
    releaseDatePrecision = releaseDatePrecision,
    imageUrl = imageUrl,
    totalTracks = totalTracks
)

fun Album.toEntity(): AlbumEntity = AlbumEntity(
    id = id.value,
    spotifyId = spotifyId.value,
    name = name,
    artistIds = artistIds.map { it.value }.toMutableList(),
    releaseDate = releaseDate,
    releaseDatePrecision = releaseDatePrecision,
    imageUrl = imageUrl,
    totalTracks = totalTracks
)

// Track mappers (externalUrls handled separately)
fun TrackEntity.toDomain(externalUrls: Map<String, String> = emptyMap()): Track = Track(
    id = TrackId(id),
    spotifyId = SpotifyId(spotifyId),
    name = name,
    artistIds = artistIds.map { ArtistId(it) }.toList(),
    albumId = albumId?.let { AlbumId(it) },
    duration = Duration(durationMs),
    explicit = explicit,
    popularity = popularity,
    previewUrl = previewUrl,
    externalUrls = externalUrls
)

fun Track.toEntity(): TrackEntity = TrackEntity(
    id = id.value,
    spotifyId = spotifyId.value,
    name = name,
    artistIds = artistIds.map { it.value }.toMutableList(),
    albumId = albumId?.value,
    durationMs = duration.milliseconds,
    explicit = explicit,
    popularity = popularity,
    previewUrl = previewUrl
)

// ListeningSession mappers
fun ListeningSessionEntity.toDomain(): ListeningSession = ListeningSession(
    id = ListeningSessionId(id),
    userId = UserId(userId),
    trackId = TrackId(trackId),
    playedAt = Timestamp(playedAt),
    contextType = contextType,
    contextUri = contextUri
)

fun ListeningSession.toEntity(): ListeningSessionEntity = ListeningSessionEntity(
    id = id.value,
    userId = userId.value,
    trackId = trackId.value,
    playedAt = playedAt.value,
    contextType = contextType,
    contextUri = contextUri
)

// Annotation mappers
fun AnnotationEntity.toDomain(): Annotation = Annotation(
    id = AnnotationId(id),
    userId = UserId(userId),
    trackId = TrackId(trackId),
    content = content,
    createdAt = Timestamp(createdAt),
    updatedAt = Timestamp(updatedAt)
)

fun Annotation.toEntity(): AnnotationEntity = AnnotationEntity(
    id = id.value,
    userId = userId.value,
    trackId = trackId.value,
    content = content,
    createdAt = createdAt.value,
    updatedAt = updatedAt.value
)

// Rating mappers
fun RatingEntity.toDomain(): Rating = Rating(
    id = RatingId(id),
    userId = UserId(userId),
    trackId = TrackId(trackId),
    overallScore = overallScore,
    dimensions = if (vocalScore != null || moodScore != null || lyricsScore != null) {
        RatingDimension(
            vocal = vocalScore,
            mood = moodScore,
            lyrics = lyricsScore
        )
    } else null,
    createdAt = Timestamp(createdAt),
    updatedAt = Timestamp(updatedAt)
)

fun Rating.toEntity(): RatingEntity = RatingEntity(
    id = id.value,
    userId = userId.value,
    trackId = trackId.value,
    overallScore = overallScore,
    vocalScore = dimensions?.vocal,
    moodScore = dimensions?.mood,
    lyricsScore = dimensions?.lyrics,
    createdAt = createdAt.value,
    updatedAt = updatedAt.value
)

