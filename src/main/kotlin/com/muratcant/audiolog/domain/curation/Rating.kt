package com.muratcant.audiolog.domain.curation

import com.muratcant.audiolog.domain.common.RatingId
import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.common.TrackId
import com.muratcant.audiolog.domain.common.Timestamp

data class RatingDimension(
    val vocal: Int? = null,
    val mood: Int? = null,
    val lyrics: Int? = null
) {
    init {
        fun validateScore(score: Int?) {
            score?.let {
                require(it in 1..5) { "Rating score must be between 1 and 5" }
            }
        }
        validateScore(vocal)
        validateScore(mood)
        validateScore(lyrics)
    }
}

data class Rating(
    val id: RatingId,
    val userId: UserId,
    val trackId: TrackId,
    val overallScore: Int? = null,
    val dimensions: RatingDimension? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    init {
        overallScore?.let {
            require(it in 1..5) { "Overall score must be between 1 and 5" }
        }
        require(overallScore != null || dimensions != null) {
            "Rating must have either overall score or dimensions"
        }
    }
}

