package com.muratcant.audiolog.application.curation

import com.muratcant.audiolog.domain.common.*
import com.muratcant.audiolog.domain.curation.Annotation
import com.muratcant.audiolog.domain.ports.AnnotationRepository
import com.muratcant.audiolog.domain.ports.TrackRepository
import com.muratcant.audiolog.domain.ports.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AddAnnotation(
    private val annotationRepository: AnnotationRepository,
    private val trackRepository: TrackRepository,
    private val userRepository: UserRepository
) {
    
    @Transactional
    fun execute(userId: UserId, trackId: TrackId, content: String): Annotation {
        val user = userRepository.findById(userId)
            ?: throw DomainError.NotFound("User not found: ${userId.value}")
        
        val track = trackRepository.findById(trackId)
            ?: throw DomainError.NotFound("Track not found: ${trackId.value}")
        
        val annotation = Annotation(
            id = AnnotationId(),
            userId = userId,
            trackId = trackId,
            content = content
        )
        
        return annotationRepository.save(annotation)
    }
}

