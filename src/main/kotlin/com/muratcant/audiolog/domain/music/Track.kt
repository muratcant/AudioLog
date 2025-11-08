package com.muratcant.audiolog.domain.music

import com.muratcant.audiolog.domain.common.TrackId
import com.muratcant.audiolog.domain.common.SpotifyId
import com.muratcant.audiolog.domain.common.Duration
import com.muratcant.audiolog.domain.common.ArtistId
import com.muratcant.audiolog.domain.common.AlbumId

data class Track(
    val id: TrackId,
    val spotifyId: SpotifyId,
    val name: String,
    val artistIds: List<ArtistId>,
    val albumId: AlbumId?,
    val duration: Duration,
    val explicit: Boolean = false,
    val popularity: Int? = null,
    val previewUrl: String? = null,
    val externalUrls: Map<String, String> = emptyMap()
)

