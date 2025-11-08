package com.muratcant.audiolog.domain.curation

import com.muratcant.audiolog.domain.common.AnnotationId
import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.common.TrackId
import com.muratcant.audiolog.domain.common.Timestamp

data class Annotation(
    val id: AnnotationId,
    val userId: UserId,
    val trackId: TrackId,
    val content: String,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    init {
        require(content.isNotBlank()) { "Annotation content cannot be blank" }
    }
}

