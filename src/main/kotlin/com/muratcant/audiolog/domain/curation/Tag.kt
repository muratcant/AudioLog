package com.muratcant.audiolog.domain.curation

import com.muratcant.audiolog.domain.common.TagId
import com.muratcant.audiolog.domain.common.TrackId

data class Tag(
    val id: TagId,
    val key: String,
    val value: String? = null
) {
    init {
        require(key.isNotBlank()) { "Tag key cannot be blank" }
    }
}

data class TrackTag(
    val trackId: TrackId,
    val tagId: TagId
)

