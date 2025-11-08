package com.muratcant.audiolog.domain.music

import com.muratcant.audiolog.domain.common.ArtistId
import com.muratcant.audiolog.domain.common.SpotifyId

data class Artist(
    val id: ArtistId,
    val spotifyId: SpotifyId,
    val name: String,
    val genres: List<String> = emptyList(),
    val popularity: Int? = null,
    val imageUrl: String? = null
)

