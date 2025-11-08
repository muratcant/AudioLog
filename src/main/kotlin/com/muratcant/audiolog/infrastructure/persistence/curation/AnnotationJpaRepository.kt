package com.muratcant.audiolog.infrastructure.persistence.curation

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AnnotationJpaRepository : JpaRepository<AnnotationEntity, UUID> {
    fun findByUserIdAndTrackId(userId: UUID, trackId: UUID): List<AnnotationEntity>
}

