package com.muratcant.audiolog.domain.session

import com.muratcant.audiolog.domain.common.ListeningSessionId
import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.common.TrackId
import com.muratcant.audiolog.domain.common.Timestamp

data class ListeningSession(
    val id: ListeningSessionId,
    val userId: UserId,
    val trackId: TrackId,
    val playedAt: Timestamp,
    val contextType: String? = null,
    val contextUri: String? = null
)

