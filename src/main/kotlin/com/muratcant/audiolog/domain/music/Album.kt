package com.muratcant.audiolog.domain.music

import com.muratcant.audiolog.domain.common.AlbumId
import com.muratcant.audiolog.domain.common.ArtistId
import com.muratcant.audiolog.domain.common.SpotifyId
import com.muratcant.audiolog.domain.common.Timestamp

data class Album(
    val id: AlbumId,
    val spotifyId: SpotifyId,
    val name: String,
    val artistIds: List<ArtistId>,
    val releaseDate: String? = null,
    val releaseDatePrecision: String? = null,
    val imageUrl: String? = null,
    val totalTracks: Int? = null
)

