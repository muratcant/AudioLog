package com.muratcant.audiolog.infrastructure.persistence

import com.muratcant.audiolog.domain.common.ListeningSessionId
import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.common.TrackId
import com.muratcant.audiolog.domain.session.ListeningSession
import com.muratcant.audiolog.domain.ports.ListeningSessionRepository
import com.muratcant.audiolog.infrastructure.persistence.session.ListeningSessionJpaRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JpaListeningSessionRepository(
    private val jpaRepository: ListeningSessionJpaRepository
) : ListeningSessionRepository {
    
    override fun findById(id: ListeningSessionId): ListeningSession? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }
    
    override fun findByUserId(userId: UserId, limit: Int): List<ListeningSession> {
        return jpaRepository.findByUserIdOrderByPlayedAtDesc(userId.value, limit).map { it.toDomain() }
    }
    
    override fun findByUserIdAndPlayedAtAfter(userId: UserId, after: Instant): List<ListeningSession> {
        return jpaRepository.findByUserIdAndPlayedAtAfter(userId.value, after).map { it.toDomain() }
    }
    
    override fun findByUserIdAndPlayedAtBetween(userId: UserId, start: Instant, end: Instant): List<ListeningSession> {
        return jpaRepository.findByUserIdAndPlayedAtBetween(userId.value, start, end).map { it.toDomain() }
    }
    
    override fun save(session: ListeningSession): ListeningSession {
        return jpaRepository.save(session.toEntity()).toDomain()
    }
    
    override fun saveAll(sessions: List<ListeningSession>): List<ListeningSession> {
        return jpaRepository.saveAll(sessions.map { it.toEntity() }).map { it.toDomain() }
    }
    
    override fun existsByUserIdAndTrackIdAndPlayedAt(userId: UserId, trackId: TrackId, playedAt: Instant): Boolean {
        return jpaRepository.existsByUserIdAndTrackIdAndPlayedAt(userId.value, trackId.value, playedAt)
    }
}

