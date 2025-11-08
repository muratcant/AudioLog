package com.muratcant.audiolog.infrastructure.persistence

import com.muratcant.audiolog.domain.common.RatingId
import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.common.TrackId
import com.muratcant.audiolog.domain.curation.Rating
import com.muratcant.audiolog.domain.ports.RatingRepository
import com.muratcant.audiolog.infrastructure.persistence.curation.RatingJpaRepository
import org.springframework.stereotype.Component

@Component
class JpaRatingRepository(
    private val jpaRepository: RatingJpaRepository
) : RatingRepository {
    
    override fun findById(id: RatingId): Rating? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }
    
    override fun findByUserIdAndTrackId(userId: UserId, trackId: TrackId): Rating? {
        return jpaRepository.findByUserIdAndTrackId(userId.value, trackId.value)?.toDomain()
    }
    
    override fun save(rating: Rating): Rating {
        return jpaRepository.save(rating.toEntity()).toDomain()
    }
}

