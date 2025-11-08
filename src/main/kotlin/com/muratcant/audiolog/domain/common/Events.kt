package com.muratcant.audiolog.domain.common

import java.time.Instant

interface DomainEvent {
    val occurredAt: Instant
        get() = Instant.now()
}

data class TrackListenedEvent(
    val sessionId: ListeningSessionId,
    val trackId: TrackId,
    val userId: UserId,
    override val occurredAt: Instant = Instant.now()
) : DomainEvent

data class TrackAnnotatedEvent(
    val annotationId: AnnotationId,
    val trackId: TrackId,
    val userId: UserId,
    override val occurredAt: Instant = Instant.now()
) : DomainEvent

data class TrackRatedEvent(
    val ratingId: RatingId,
    val trackId: TrackId,
    val userId: UserId,
    override val occurredAt: Instant = Instant.now()
) : DomainEvent

