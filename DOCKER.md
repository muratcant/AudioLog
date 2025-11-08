# Docker ile Çalıştırma Kılavuzu

## Hızlı Başlangıç

### 1. Tüm Servisleri Başlat

```bash
docker-compose up -d
```

Bu komut:
- PostgreSQL container'ını başlatır (port 5432)
- Uygulama container'ını build eder ve başlatır (port 8080)
- Flyway migration'ları otomatik çalışır
- Veriler `postgres_data` volume'unda kalıcı olarak saklanır

### 2. Logları İzle

```bash
# Tüm servislerin logları
docker-compose logs -f

# Sadece uygulama logları
docker-compose logs -f app

# Sadece PostgreSQL logları
docker-compose logs -f postgres
```

### 3. Servis Durumunu Kontrol Et

```bash
# Container'ların durumunu gör
docker-compose ps

# Uygulama sağlık kontrolü
curl http://localhost:8080/actuator/health
```

### 4. Servisleri Durdur

```bash
# Container'ları durdur (veriler korunur)
docker-compose down

# Container'ları durdur VE verileri sil
docker-compose down -v
```

## Geliştirme İçin İpuçları

### Sadece PostgreSQL'i Çalıştır (Yerel Geliştirme)

```bash
# Sadece PostgreSQL'i başlat
docker-compose up -d postgres

# Sonra uygulamayı yerel olarak çalıştır
./gradlew bootRun
```

### Container'ları Yeniden Başlat

```bash
# Tüm servisleri yeniden başlat
docker-compose restart

# Sadece uygulamayı yeniden başlat
docker-compose restart app
```

### Uygulamayı Yeniden Build Et

```bash
# Uygulamayı yeniden build et ve başlat
docker-compose up -d --build app
```

### Veritabanına Bağlan

```bash
# PostgreSQL container'ına bağlan
docker-compose exec postgres psql -U postgres -d audiolog

# Veya dışarıdan bağlan (port 5432 açık)
psql -h localhost -U postgres -d audiolog
```

## Sorun Giderme

### Logları Kontrol Et

```bash
# Son 100 satır log
docker-compose logs --tail=100 app

# Hata mesajlarını filtrele
docker-compose logs app | grep -i error
```

### Container'ı Yeniden Oluştur

```bash
# Container'ı durdur, sil ve yeniden oluştur
docker-compose down
docker-compose up -d --build
```

### Volume'ları Temizle

```bash
# Tüm volume'ları listele
docker volume ls

# AudioLog volume'unu sil
docker volume rm audiolog_postgres_data
```

## Portlar

- **8080**: Uygulama (http://localhost:8080)
- **5432**: PostgreSQL
- **9090**: Prometheus metrics (eğer expose edilirse)

## Environment Variables

Docker Compose'da şu environment variable'lar kullanılır:

- `SPRING_DATASOURCE_URL`: PostgreSQL bağlantı URL'i
- `SPRING_DATASOURCE_USERNAME`: Veritabanı kullanıcı adı
- `SPRING_DATASOURCE_PASSWORD`: Veritabanı şifresi
- `SPRING_PROFILES_ACTIVE`: Aktif Spring profile (docker)

Bu değerleri `docker-compose.yml` dosyasında değiştirebilirsiniz.

