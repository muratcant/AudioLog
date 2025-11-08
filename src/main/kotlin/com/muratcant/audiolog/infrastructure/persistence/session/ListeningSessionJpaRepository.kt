package com.muratcant.audiolog.infrastructure.persistence.session

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.*

interface ListeningSessionJpaRepository : JpaRepository<ListeningSessionEntity, UUID> {
    @Query(value = "SELECT * FROM listening_sessions WHERE user_id = :userId ORDER BY played_at DESC LIMIT :limit", nativeQuery = true)
    fun findByUserIdOrderByPlayedAtDesc(@Param("userId") userId: UUID, @Param("limit") limit: Int): List<ListeningSessionEntity>
    
    @Query("SELECT s FROM ListeningSessionEntity s WHERE s.userId = :userId AND s.playedAt > :after ORDER BY s.playedAt DESC")
    fun findByUserIdAndPlayedAtAfter(@Param("userId") userId: UUID, @Param("after") after: Instant): List<ListeningSessionEntity>
    
    @Query("SELECT s FROM ListeningSessionEntity s WHERE s.userId = :userId AND s.playedAt >= :start AND s.playedAt < :end ORDER BY s.playedAt DESC")
    fun findByUserIdAndPlayedAtBetween(@Param("userId") userId: UUID, @Param("start") start: Instant, @Param("end") end: Instant): List<ListeningSessionEntity>
    
    fun existsByUserIdAndTrackIdAndPlayedAt(userId: UUID, trackId: UUID, playedAt: Instant): Boolean
}

