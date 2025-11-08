package com.muratcant.audiolog.infrastructure.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.muratcant.audiolog.infrastructure.persistence"])
@EntityScan(basePackages = ["com.muratcant.audiolog.infrastructure.persistence"])
class JpaConfig

