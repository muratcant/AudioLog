package com.muratcant.audiolog.infrastructure.persistence

import com.muratcant.audiolog.domain.common.AnnotationId
import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.common.TrackId
import com.muratcant.audiolog.domain.curation.Annotation
import com.muratcant.audiolog.domain.ports.AnnotationRepository
import com.muratcant.audiolog.infrastructure.persistence.curation.AnnotationJpaRepository
import org.springframework.stereotype.Component

@Component
class JpaAnnotationRepository(
    private val jpaRepository: AnnotationJpaRepository
) : AnnotationRepository {
    
    override fun findById(id: AnnotationId): Annotation? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }
    
    override fun findByUserIdAndTrackId(userId: UserId, trackId: TrackId): List<Annotation> {
        return jpaRepository.findByUserIdAndTrackId(userId.value, trackId.value).map { it.toDomain() }
    }
    
    override fun save(annotation: Annotation): Annotation {
        return jpaRepository.save(annotation.toEntity()).toDomain()
    }
    
    override fun delete(id: AnnotationId) {
        jpaRepository.deleteById(id.value)
    }
}

