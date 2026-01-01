# Araç Galerisi Yönetim Sistemi (Vehicle Gallery Management System)

Bu proje, araç galerisi yönetimi, müşteri takibi, satış/kiralama işlemleri ve temel muhasebe (banka/kasa) takibi için geliştirilmiş bir Spring Boot uygulamasıdır.

## Kurulum ve Çalıştırma (How to Run)

### 1. Veritabanı Hazırlığı (Database Setup)
Bu proje **PostgreSQL** veritabanı kullanmaktadır.

1.  PostgreSQL'in kurulu ve çalışır durumda olduğundan emin olun.
2.  `database_init.sql` dosyasını bir SQL editörü (pgAdmin, DBeaver vb.) ile açın.
3.  Dosyadaki script'i çalıştırarak veritabanı tablolarını, trigger'ları ve örnek verileri oluşturun.
    *   **Not:** `database_init.sql` dosyası projenin ana dizininde bulunmaktadır.

### 2. Uygulamayı Çalıştırma (Run Application)
Proje bir Maven projesidir.

1.  Proje klasöründe terminal/komut satırını açın.
2.  Aşağıdaki komutu çalıştırın:
    ```bash
    mvn spring-boot:run
    ```
3.  Uygulama başladığında tarayıcınızdan `http://localhost:8080` adresine gidin.

## Özellikler (Features)
*   **Araç Yönetimi:** Yakıtlı, Elektrikli ve Hibrit araç desteği.
*   **Müşteri Yönetimi:** Bireysel ve Kurumsal müşteri ayrımı.
*   **İlan & Satış:** Satılık ve Kiralık ilanlar, teklif sistemi.
*   **Banka Modülü:** Müşteri-Galerici arası otomatik para transferi.
*   **Splash Screen:** Açılışta yükleme ekranı.
*   **Master-Detail Görünümü:** Araç detay sayfasında servis kayıtları listeleme.

## Gereksinimler (Requirements)
*   Java 17 veya üzeri
*   Maven
*   PostgreSQL
