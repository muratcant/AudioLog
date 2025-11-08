package com.muratcant.audiolog.infrastructure.web

import com.muratcant.audiolog.domain.common.SpotifyId
import com.muratcant.audiolog.domain.common.UserId
import com.muratcant.audiolog.domain.ports.SpotifyPort
import com.muratcant.audiolog.domain.ports.UserRepository
import com.muratcant.audiolog.domain.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.http.MediaType
import java.time.Instant
import java.util.*
import java.util.Base64
import com.fasterxml.jackson.annotation.JsonProperty

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Spotify OAuth2 authentication ve kullanıcı yönetimi")
class AuthController(
    private val userRepository: UserRepository,
    private val spotifyPort: SpotifyPort,
    @Value("\${spotify.oauth.client-id:}")
    private val clientId: String,
    @Value("\${spotify.oauth.client-secret:}")
    private val clientSecret: String,
    @Value("\${spotify.oauth.redirect-uri:http://localhost:8080/auth/callback}")
    private val redirectUri: String
) {
    
    @GetMapping("/login")
    @Operation(
        summary = "Spotify OAuth2 login",
        description = "Kullanıcıyı Spotify OAuth2 sayfasına yönlendirir. " +
                "Tarayıcıda açıldığında otomatik redirect yapar, API çağrısı olarak yapılırsa URL döner."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "302",
                description = "Redirect to Spotify authorization page"
            ),
            ApiResponse(
                responseCode = "200",
                description = "Login URL (if called as API)",
                content = [Content(schema = Schema(implementation = LoginUrlResponse::class))]
            )
        ]
    )
    fun login(
        @Parameter(description = "JSON response döndür (redirect yerine)", required = false)
        @RequestParam(required = false, defaultValue = "false") json: Boolean
    ): Any {
        val scopes = "user-read-recently-played user-read-email"
        val state = UUID.randomUUID().toString()
        val authUrl = "https://accounts.spotify.com/authorize?" +
                "client_id=$clientId&" +
                "response_type=code&" +
                "redirect_uri=${java.net.URLEncoder.encode(redirectUri, "UTF-8")}&" +
                "scope=${java.net.URLEncoder.encode(scopes, "UTF-8")}&" +
                "state=$state&" +
                "show_dialog=true"
        
        return if (json) {
            ResponseEntity.ok(LoginUrlResponse(authUrl, state))
        } else {
            RedirectView(authUrl)
        }
    }
    
    @GetMapping("/callback")
    @Operation(
        summary = "Spotify OAuth2 callback",
        description = "Spotify'dan dönen authorization code'u token'a çevirir ve kullanıcıyı kaydeder"
    )
    fun callback(
        @RequestParam code: String?,
        @RequestParam state: String?,
        @RequestParam(required = false) error: String?
    ): RedirectView {
        if (error != null) {
            // Kullanıcı reddetti
            return RedirectView("/auth/error?message=${java.net.URLEncoder.encode(error, "UTF-8")}")
        }
        
        if (code == null) {
            return RedirectView("/auth/error?message=missing_code")
        }
        
        val tokenResponse = runBlocking {
            exchangeCodeForToken(code)
        }
        
        val userInfo = runBlocking {
            getCurrentUser(tokenResponse.accessToken)
        }
        
        val existingUser = userRepository.findBySpotifyId(SpotifyId(userInfo.id))
        
        val expiresAt = Instant.now().plusSeconds(tokenResponse.expiresIn)
        
        val user = existingUser?.copy(
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken,
            tokenExpiresAt = expiresAt,
            displayName = userInfo.displayName,
            email = userInfo.email
        ) ?: User(
            id = UserId(),
            spotifyId = SpotifyId(userInfo.id),
            displayName = userInfo.displayName,
            email = userInfo.email,
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken,
            tokenExpiresAt = expiresAt
        )
        
        val savedUser = userRepository.save(user)
        
        // Başarı sayfasına yönlendir (kullanıcı ID'si ile)
        return RedirectView("/auth/success?userId=${savedUser.id.value}")
    }
    
    @GetMapping("/success")
    @Operation(summary = "OAuth2 başarı sayfası", hidden = true)
    fun success(@RequestParam userId: UUID): ResponseEntity<Map<String, Any>> {
        val user = userRepository.findById(UserId(userId))
            ?: throw com.muratcant.audiolog.domain.common.DomainError.NotFound("User not found")
        
        return ResponseEntity.ok(mapOf(
            "success" to true,
            "message" to "Spotify hesabınız başarıyla bağlandı!",
            "user" to mapOf(
                "id" to user.id.value.toString(),
                "spotifyId" to user.spotifyId.value,
                "displayName" to (user.displayName ?: ""),
                "email" to (user.email ?: "")
            ),
            "nextSteps" to listOf(
                "Artık /ingest/recently-played endpoint'ini kullanabilirsiniz",
                "userId: ${user.id.value}",
                "Swagger UI: /swagger-ui/index.html"
            )
        ))
    }
    
    @GetMapping("/error")
    @Operation(summary = "OAuth2 hata sayfası", hidden = true)
    fun error(@RequestParam(required = false) message: String?): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body(mapOf(
            "success" to false,
            "error" to (message ?: "OAuth2 authentication failed")
        ))
    }
    
    private suspend fun exchangeCodeForToken(code: String): TokenResponse {
        val webClient = WebClient.builder()
            .baseUrl("https://accounts.spotify.com")
            .build()
        
        val credentials = "$clientId:$clientSecret"
        val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray())
        
        val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
        formData.add("grant_type", "authorization_code")
        formData.add("code", code)
        formData.add("redirect_uri", redirectUri)
        
        val response = webClient.post()
            .uri("/api/token")
            .header("Authorization", "Basic $encodedCredentials")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(formData)
            .retrieve()
            .bodyToMono<TokenExchangeResponse>()
            .awaitSingle()
        
        return TokenResponse(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            expiresIn = response.expiresIn
        )
    }
    
    @PostMapping("/token")
    @Operation(
        summary = "Access token ile kullanıcı oluştur/güncelle",
        description = "Spotify access token'ı ile kullanıcı bilgilerini çeker ve veritabanına kaydeder. " +
                "Eğer kullanıcı varsa token'ları günceller."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Kullanıcı başarıyla oluşturuldu/güncellendi",
                content = [Content(schema = Schema(implementation = UserResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Geçersiz token",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    fun createOrUpdateUser(
        @Parameter(description = "Spotify access token", required = true)
        @RequestParam accessToken: String
    ): ResponseEntity<UserResponse> {
        val userInfo = runBlocking {
            getCurrentUser(accessToken)
        }
        
        val existingUser = userRepository.findBySpotifyId(SpotifyId(userInfo.id))
        
        val expiresAt = Instant.now().plusSeconds(3600) // 1 saat
        
        val user = existingUser?.copy(
            accessToken = accessToken,
            tokenExpiresAt = expiresAt,
            displayName = userInfo.displayName,
            email = userInfo.email
        ) ?: User(
            id = UserId(),
            spotifyId = SpotifyId(userInfo.id),
            displayName = userInfo.displayName,
            email = userInfo.email,
            accessToken = accessToken,
            tokenExpiresAt = expiresAt,
            refreshToken = null // Manuel token ile refresh token yok
        )
        
        val savedUser = userRepository.save(user)
        
        return ResponseEntity.ok(UserResponse(
            id = savedUser.id.value,
            spotifyId = savedUser.spotifyId.value,
            displayName = savedUser.displayName,
            email = savedUser.email
        ))
    }
    
    private suspend fun getCurrentUser(accessToken: String): SpotifyUserInfo {
        val webClient = WebClient.builder()
            .baseUrl("https://api.spotify.com")
            .build()
        
        val response = webClient.get()
            .uri("/v1/me")
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .bodyToMono<SpotifyUserInfoResponse>()
            .awaitSingle()
        
        return SpotifyUserInfo(
            id = response.id,
            displayName = response.displayName,
            email = response.email
        )
    }
}

data class LoginUrlResponse(
    val authUrl: String,
    val state: String
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String?,
    val expiresIn: Long
)

data class TokenExchangeResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("expires_in")
    val expiresIn: Long,
    @JsonProperty("refresh_token")
    val refreshToken: String?,
    @JsonProperty("scope")
    val scope: String?
)

data class UserResponse(
    val id: UUID,
    val spotifyId: String,
    val displayName: String?,
    val email: String?
)

data class SpotifyUserInfo(
    val id: String,
    val displayName: String?,
    val email: String?
)

data class SpotifyUserInfoResponse(
    val id: String,
    val displayName: String?,
    val email: String?
)

