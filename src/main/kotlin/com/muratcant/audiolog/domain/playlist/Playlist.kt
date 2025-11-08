package com.muratcant.audiolog.domain.playlist

import com.muratcant.audiolog.domain.common.PlaylistId
import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.common.TrackId
import com.muratcant.audiolog.domain.common.SpotifyId
import com.muratcant.audiolog.domain.common.Timestamp

data class Playlist(
    val id: PlaylistId,
    val userId: UserId,
    val spotifyId: SpotifyId? = null,
    val name: String,
    val description: String? = null,
    val isPublic: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    init {
        require(name.isNotBlank()) { "Playlist name cannot be blank" }
    }
}

data class PlaylistTrack(
    val playlistId: PlaylistId,
    val trackId: TrackId,
    val position: Int,
    val addedAt: Timestamp = Timestamp.now()
)

