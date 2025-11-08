package com.muratcant.audiolog.application.curation

import com.muratcant.audiolog.domain.common.*
import com.muratcant.audiolog.domain.curation.Rating
import com.muratcant.audiolog.domain.curation.RatingDimension
import com.muratcant.audiolog.domain.ports.RatingRepository
import com.muratcant.audiolog.domain.ports.TrackRepository
import com.muratcant.audiolog.domain.ports.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RateTrack(
    private val ratingRepository: RatingRepository,
    private val trackRepository: TrackRepository,
    private val userRepository: UserRepository
) {
    
    @Transactional
    fun execute(
        userId: UserId,
        trackId: TrackId,
        overallScore: Int? = null,
        vocal: Int? = null,
        mood: Int? = null,
        lyrics: Int? = null
    ): Rating {
        val user = userRepository.findById(userId)
            ?: throw DomainError.NotFound("User not found: ${userId.value}")
        
        val track = trackRepository.findById(trackId)
            ?: throw DomainError.NotFound("Track not found: ${trackId.value}")
        
        // Check if rating already exists
        val existingRating = ratingRepository.findByUserIdAndTrackId(userId, trackId)
        
        val dimensions = if (vocal != null || mood != null || lyrics != null) {
            RatingDimension(vocal = vocal, mood = mood, lyrics = lyrics)
        } else null
        
        val rating = existingRating?.copy(
            overallScore = overallScore,
            dimensions = dimensions,
            updatedAt = Timestamp.now()
        ) ?: Rating(
            id = RatingId(),
            userId = userId,
            trackId = trackId,
            overallScore = overallScore,
            dimensions = dimensions
        )
        
        return ratingRepository.save(rating)
    }
}

