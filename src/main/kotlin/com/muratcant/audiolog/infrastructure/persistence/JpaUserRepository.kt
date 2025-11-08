package com.muratcant.audiolog.infrastructure.persistence

import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.common.SpotifyId
import com.muratcant.audiolog.domain.ports.UserRepository
import com.muratcant.audiolog.domain.user.User
import org.springframework.stereotype.Component

@Component
class JpaUserRepository(
    private val jpaRepository: UserJpaRepository
) : UserRepository {
    
    override fun findById(id: UserId): User? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }
    
    override fun findBySpotifyId(spotifyId: SpotifyId): User? {
        return jpaRepository.findBySpotifyId(spotifyId.value)?.toDomain()
    }
    
    override fun save(user: User): User {
        return jpaRepository.save(user.toEntity()).toDomain()
    }
}

