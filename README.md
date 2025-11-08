# AudioLog - Dinleme Günlüğü & Araştırma Defteri

Spotify dinleme geçmişini toplayan ve kişisel müzik günlüğü haline getiren uygulama.

## Teknolojiler

- Kotlin 2.2.20
- Spring Boot 3.3.5
- PostgreSQL 15
- Flyway (Database migrations)
- Docker & Docker Compose

## Hızlı Başlangıç

### Docker ile Çalıştırma

```bash
# Tüm servisleri başlat (PostgreSQL + Uygulama)
docker-compose up -d

# Logları izle
docker-compose logs -f app

# Servisleri durdur (veriler korunur - volume'lar silinmez)
docker-compose down

# Servisleri durdur VE verileri de silmek için (volume'ları da siler)
docker-compose down -v

# Sadece container'ları yeniden başlat (veriler korunur)
docker-compose restart
```

### Yerel Geliştirme

1. PostgreSQL'i başlatın (port 5432):
```bash
# Docker ile PostgreSQL
docker run -d \
  --name audiolog-postgres \
  -e POSTGRES_DB=audiolog \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

2. Uygulamayı çalıştırın:
```bash
./gradlew bootRun
```

Uygulama `http://localhost:8080` adresinde çalışacaktır.

## API Dokümantasyonu

Swagger UI üzerinden tüm API endpoint'lerini görüntüleyebilir ve test edebilirsiniz:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

API dokümantasyonu OpenAPI 3.0+ standardına uygundur ve tüm endpoint'ler, parametreler, request/response şemaları dokümante edilmiştir.

## API Endpoints

### Ingest
- `POST /ingest/recently-played?userId={uuid}&accessToken={token}&since={epochSeconds}` - Spotify dinleme geçmişini çek

### Curation
- `POST /tracks/{trackId}/annotations?userId={uuid}` - Şarkıya not ekle
- `POST /tracks/{trackId}/ratings?userId={uuid}` - Şarkıyı değerlendir

## Veritabanı

- Flyway migration'ları otomatik olarak çalışır. İlk başlatmada tüm tablolar oluşturulur.
- Veritabanı verileri Docker volume'unda (`postgres_data`) kalıcı olarak saklanır.
- `docker-compose down` ile container'ları durdursanız bile veriler korunur.
- Verileri silmek için `docker-compose down -v` komutunu kullanın.

## Yapılandırma

### Spotify OAuth2 Kurulumu (İlk Adım - Zorunlu!)

OAuth2 flow'u kullanmak için önce Spotify Developer Dashboard'da uygulama oluşturman gerekiyor:

1. **Spotify Developer Dashboard:** https://developer.spotify.com/dashboard
2. **"Create an app"** → Uygulama oluştur
3. **Redirect URI ekle:** `http://localhost:8080/auth/callback`
4. **Client ID ve Client Secret'i kopyala**


## Yapı

Proje Clean Architecture prensiplerine göre organize edilmiştir:

- `domain/` - Pure Kotlin domain modelleri ve port'lar
- `application/` - Use-case'ler ve business logic
- `infrastructure/` - Spring Boot, JPA, Web adapters

