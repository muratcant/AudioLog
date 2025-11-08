package com.muratcant.audiolog.infrastructure.persistence.curation

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RatingJpaRepository : JpaRepository<RatingEntity, UUID> {
    fun findByUserIdAndTrackId(userId: UUID, trackId: UUID): RatingEntity?
}

