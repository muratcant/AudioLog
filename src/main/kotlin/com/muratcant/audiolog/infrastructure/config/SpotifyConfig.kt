package com.muratcant.audiolog.infrastructure.config

import com.muratcant.audiolog.domain.ports.SpotifyPort
import com.muratcant.audiolog.infrastructure.spotify.SpotifyWebClient
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Configuration
class SpotifyConfig(
    @param:Value("\${spotify.api.base-url:https://api.spotify.com}")
    private val baseUrl: String
) {
    
    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build()
    }
    
    @Bean
    fun spotifyRetry(retryRegistry: RetryRegistry): Retry {
        val config = RetryConfig.custom<Any>()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(1))
            .retryOnException { ex ->
                ex is org.springframework.web.reactive.function.client.WebClientResponseException &&
                        (ex.statusCode == org.springframework.http.HttpStatus.TOO_MANY_REQUESTS ||
                         ex.statusCode.is5xxServerError)
            }
            .build()
        
        return retryRegistry.retry("spotify", config)
    }
    
    @Bean
    fun retryRegistry(): RetryRegistry {
        return RetryRegistry.ofDefaults()
    }
    
    @Bean
    fun spotifyPort(webClient: WebClient, spotifyRetry: Retry): SpotifyPort {
        return SpotifyWebClient(webClient, spotifyRetry)
    }
}

