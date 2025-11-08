package com.muratcant.audiolog.infrastructure.persistence.curation

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TagJpaRepository : JpaRepository<TagEntity, UUID> {
    fun findByKey(key: String): TagEntity?
}

