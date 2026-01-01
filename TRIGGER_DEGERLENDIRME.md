# Tetikleyici (Trigger) Değerlendirmesi
## Araç Galerisi Yönetim Sistemi

**Değerlendirme Tarihi:** 26 Aralık 2024  
**Dosya:** `database_init.sql`  
**Durum:** ✅ Eklendi ve Çalışıyor

---

## Eklenen Tetikleyiciler (5 Adet)

### 1. trg_deactivate_listing_on_sale
| Özellik | Değer |
|---------|-------|
| **Tablo** | `sales` |
| **Olay** | `AFTER INSERT OR UPDATE` |
| **İşlev** | Satış tamamlandığında ilanı otomatik deaktif eder |
| **Fonksiyon** | `deactivate_listing_on_sale()` |

**Açıklama:** Bir satış kaydı `COMPLETED` durumuna geçtiğinde, ilgili ilanın `is_active` değerini `false` yapar. Bu sayede satılmış araçların ilanları otomatik olarak pasif hale gelir.

---

### 2. trg_calculate_rental_cost
| Özellik | Değer |
|---------|-------|
| **Tablo** | `rentals` |
| **Olay** | `BEFORE INSERT OR UPDATE` |
| **İşlev** | Kiralama maliyetini otomatik hesaplar |
| **Fonksiyon** | `calculate_rental_cost()` |

**Açıklama:** Kiralama kaydı eklendiğinde veya güncellendiğinde, günlük ücret × gün sayısı formülü ile toplam maliyeti otomatik hesaplar. Eğer `total_cost` değeri `NULL` ise hesaplama yapılır.

---

### 3. trg_update_vehicle_after_service
| Özellik | Değer |
|---------|-------|
| **Tablo** | `service_records` |
| **Olay** | `AFTER INSERT` |
| **İşlev** | Servis kaydı eklendiğinde bildirim oluşturur |
| **Fonksiyon** | `update_vehicle_after_service()` |

**Açıklama:** Her yeni servis kaydı eklendiğinde, araç ID'si ve servis açıklamasını içeren bir bildirim (NOTICE) oluşturur. Genişletilebilir yapıda tasarlanmıştır.

---

### 4. trg_create_sale_on_offer_accepted
| Özellik | Değer |
|---------|-------|
| **Tablo** | `offers` |
| **Olay** | `AFTER UPDATE` |
| **İşlev** | Teklif kabul edildiğinde otomatik satış oluşturur |
| **Fonksiyon** | `create_sale_on_offer_accepted()` |

**Açıklama:** Bir teklif `ACCEPTED` durumuna güncellendiğinde, eğer ilan tipi `SALE` ise otomatik olarak `PENDING` durumunda bir satış kaydı oluşturur. Bu sayede satış süreci otomatik başlatılır.

---

### 5. trg_log_vehicle_changes
| Özellik | Değer |
|---------|-------|
| **Tablo** | `vehicles` |
| **Olay** | `AFTER INSERT OR UPDATE OR DELETE` |
| **İşlev** | Araç değişikliklerini audit log'a kaydeder |
| **Fonksiyon** | `log_vehicle_changes()` |

**Açıklama:** Araç tablosundaki tüm değişiklikleri (ekleme, güncelleme, silme) `audit_log` tablosuna JSONB formatında kaydeder. Eski ve yeni veri değerleri saklanır.

---

## Ek Tablo: audit_log

| Kolon | Tip | Açıklama |
|-------|-----|----------|
| `id` | BIGSERIAL | Primary Key |
| `table_name` | VARCHAR(100) | Değişen tablo adı |
| `operation` | VARCHAR(20) | INSERT, UPDATE, DELETE |
| `record_id` | BIGINT | Kayıt ID'si |
| `old_data` | JSONB | Eski veri (UPDATE/DELETE için) |
| `new_data` | JSONB | Yeni veri (INSERT/UPDATE için) |
| `changed_at` | TIMESTAMP | Değişiklik zamanı |
| `changed_by` | VARCHAR(255) | Değiştiren kullanıcı (opsiyonel) |

---

## Test Senaryoları

### Trigger 1 Testi:
```sql
-- Satış tamamla ve ilanın deaktif olduğunu kontrol et
UPDATE sales SET status = 'COMPLETED' WHERE id = 1;
SELECT is_active FROM listings WHERE id = 1; -- false olmalı
```

### Trigger 2 Testi:
```sql
-- Kiralama ekle, maliyet otomatik hesaplansın
INSERT INTO rentals (customer_id, listing_id, start_date, end_date, status)
VALUES (4, 11, CURRENT_DATE, CURRENT_DATE + 5, 'ACTIVE');
-- total_cost = 5 * 75.0 = 375.0
```

### Trigger 4 Testi:
```sql
-- Teklifi kabul et, satış kaydı oluşsun
UPDATE offers SET status = 'ACCEPTED' WHERE id = 1;
SELECT * FROM sales WHERE listing_id = (SELECT listing_id FROM offers WHERE id = 1);
```

### Trigger 5 Testi:
```sql
-- Araç güncelle ve audit log kontrol et
UPDATE vehicles SET mileage = 20000 WHERE id = 1;
SELECT * FROM audit_log WHERE table_name = 'vehicles' ORDER BY id DESC LIMIT 1;
```

---

## Sonuç

**Tetikleyici Değerlendirmesi: BAŞARILI ✅**

5 anlamlı ve işlevsel tetikleyici başarıyla oluşturulmuştur. Tüm trigger'lar proje iş mantığına uygun şekilde tasarlanmış ve çalışır durumda test edilebilir.
