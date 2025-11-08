package com.muratcant.audiolog.domain.common

import java.util.UUID

data class UserId(val value: UUID = UUID.randomUUID())
data class TrackId(val value: UUID = UUID.randomUUID())
data class ArtistId(val value: UUID = UUID.randomUUID())
data class AlbumId(val value: UUID = UUID.randomUUID())
data class ListeningSessionId(val value: UUID = UUID.randomUUID())
data class AnnotationId(val value: UUID = UUID.randomUUID())
data class RatingId(val value: UUID = UUID.randomUUID())
data class TagId(val value: UUID = UUID.randomUUID())
data class PlaylistId(val value: UUID = UUID.randomUUID())

