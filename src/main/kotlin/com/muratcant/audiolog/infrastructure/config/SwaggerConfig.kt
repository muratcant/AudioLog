package com.muratcant.audiolog.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("AudioLog API")
                    .description("Dinleme Günlüğü & Araştırma Defteri - Spotify dinleme geçmişini toplayan ve kişisel müzik günlüğü haline getiren API")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("AudioLog Team")
                            .email("support@audiolog.com")
                    )
                    .license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
            .servers(
                listOf(
                    Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server"),
                    Server()
                        .url("https://api.audiolog.com")
                        .description("Production Server")
                )
            )
    }
}

