# Spotify OAuth2 Kurulum Kılavuzu

## 1. Spotify Developer Dashboard'dan Uygulama Oluşturma

1. **Spotify Developer Dashboard'a Git:**
   - https://developer.spotify.com/dashboard adresine git
   - Spotify hesabınla giriş yap

2. **Yeni Uygulama Oluştur:**
   - "Create an app" butonuna tıkla
   - App name: `AudioLog` (veya istediğin isim)
   - App description: `Dinleme Günlüğü & Araştırma Defteri`
   - Developer: Kendi hesabını seç
   - "Create" butonuna tıkla

3. **Client ID ve Client Secret'i Al:**
   - Oluşturduğun uygulamanın sayfasında:
     - **Client ID**: Sayfanın üstünde görünür
     - **Client Secret**: "View client secret" butonuna tıklayarak görebilirsin

4. **Redirect URI Ekle:**
   - "Edit Settings" butonuna tıkla
   - "Redirect URIs" bölümüne ekle:
     - `http://localhost:8080/auth/callback` (development için)
     - Production için: `https://yourdomain.com/auth/callback`
   - "Add" ve "Save" butonlarına tıkla

## 2. Environment Variables Ayarlama

### Docker ile Çalıştırma

`docker-compose.yml` dosyasına environment variable'ları ekle:

```yaml
app:
  environment:
    # ... mevcut değişkenler ...
    SPOTIFY_CLIENT_ID=your_client_id_here
    SPOTIFY_CLIENT_SECRET=your_client_secret_here
    SPOTIFY_REDIRECT_URI=http://localhost:8080/auth/callback
```

### Yerel Geliştirme

`.env` dosyası oluştur (veya IDE'de environment variables ayarla):

```bash
SPOTIFY_CLIENT_ID=your_client_id_here
SPOTIFY_CLIENT_SECRET=your_client_secret_here
SPOTIFY_REDIRECT_URI=http://localhost:8080/auth/callback
```

## 3. Kullanım

### Yöntem 1: Access Token ile Kullanıcı Oluşturma (Manuel)

Eğer zaten bir Spotify access token'ın varsa:

```bash
POST /auth/token?accessToken=YOUR_ACCESS_TOKEN
```

Bu endpoint:
- Token ile kullanıcı bilgilerini Spotify'dan çeker
- Kullanıcıyı veritabanına kaydeder (varsa günceller)
- Kullanıcı UUID'sini döner

**Response:**
```json
{
  "id": "user-uuid",
  "spotifyId": "spotify-user-id",
  "displayName": "Kullanıcı Adı",
  "email": "user@example.com"
}
```

### Yöntem 2: OAuth2 Flow (Önerilen)

1. **Login URL'ini Al:**
   ```bash
   GET /auth/login
   ```

2. **Kullanıcıyı Yönlendir:**
   - Response'daki `authUrl`'i kullanıcıya göster veya yönlendir
   - Kullanıcı Spotify'da giriş yapar ve izin verir

3. **Callback'i İşle:**
   - Spotify kullanıcıyı `redirect_uri`'ye yönlendirir
   - `code` parametresi ile token exchange yapılır (şu an manuel)

## 4. Access Token Nasıl Alınır?

### Spotify Developer Dashboard'dan (Test İçin)

1. Spotify Developer Dashboard'da uygulamanı aç
2. "Get Token" butonuna tıkla
3. Gerekli scope'ları seç:
   - `user-read-recently-played`
   - `user-read-email`
4. "Get Token" butonuna tıkla
5. Token'ı kopyala ve `/auth/token` endpoint'ine gönder

### OAuth2 Flow ile (Production)

Tam OAuth2 flow'u için callback endpoint'i implement edilmeli (şu an manuel token ile çalışıyor).

## 5. Kullanıcı Oluşturduktan Sonra

Kullanıcı oluşturulduktan sonra dönen `id` (UUID) ile `/ingest/recently-played` endpoint'ini kullanabilirsin:

```bash
POST /ingest/recently-played?userId=USER_UUID&accessToken=ACCESS_TOKEN
```

## Notlar

- Access token'lar 1 saat geçerlidir
- Token yenileme (refresh token) şu an implement edilmemiş
- Production için tam OAuth2 callback flow'u eklenmeli
- Client Secret'i asla public repository'ye commit etme!

