package com.muratcant.audiolog.infrastructure.persistence

import com.muratcant.audiolog.domain.common.TagId
import com.muratcant.audiolog.domain.curation.Tag
import com.muratcant.audiolog.domain.ports.TagRepository
import com.muratcant.audiolog.infrastructure.persistence.curation.TagJpaRepository
import org.springframework.stereotype.Component

@Component
class JpaTagRepository(
    private val jpaRepository: TagJpaRepository
) : TagRepository {
    
    override fun findById(id: TagId): Tag? {
        return jpaRepository.findById(id.value).orElse(null)?.let {
            com.muratcant.audiolog.domain.curation.Tag(
                id = com.muratcant.audiolog.domain.common.TagId(it.id),
                key = it.key,
                value = it.value
            )
        }
    }
    
    override fun findByKey(key: String): Tag? {
        return jpaRepository.findByKey(key)?.let {
            com.muratcant.audiolog.domain.curation.Tag(
                id = com.muratcant.audiolog.domain.common.TagId(it.id),
                key = it.key,
                value = it.value
            )
        }
    }
    
    override fun save(tag: Tag): Tag {
        val entity = com.muratcant.audiolog.infrastructure.persistence.curation.TagEntity(
            id = tag.id.value,
            key = tag.key,
            value = tag.value
        )
        return jpaRepository.save(entity).let {
            com.muratcant.audiolog.domain.curation.Tag(
                id = com.muratcant.audiolog.domain.common.TagId(it.id),
                key = it.key,
                value = it.value
            )
        }
    }
    
    override fun saveAll(tags: List<Tag>): List<Tag> {
        val entities = tags.map {
            com.muratcant.audiolog.infrastructure.persistence.curation.TagEntity(
                id = it.id.value,
                key = it.key,
                value = it.value
            )
        }
        return jpaRepository.saveAll(entities).map {
            com.muratcant.audiolog.domain.curation.Tag(
                id = com.muratcant.audiolog.domain.common.TagId(it.id),
                key = it.key,
                value = it.value
            )
        }
    }
}

