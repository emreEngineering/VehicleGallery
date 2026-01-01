# Saklı Prosedür (Stored Procedure) Değerlendirmesi
## Araç Galerisi Yönetim Sistemi

**Değerlendirme Tarihi:** 26 Aralık 2024  
**Dosya:** `database_init.sql`  
**Durum:** ✅ Eklendi ve Çalışıyor

---

## Eklenen Saklı Prosedürler (5 Adet)

### 1. sp_create_sale
| Özellik | Değer |
|---------|-------|
| **Parametreler** | customer_id, listing_id, amount, payment_type |
| **İşlev** | Yeni satış ve ödeme kaydı oluşturur, ilanı deaktif eder |

**Kullanım:**
```sql
CALL sp_create_sale(4, 5, 52000.0, 'TRANSFER');
```

---

### 2. sp_create_rental
| Özellik | Değer |
|---------|-------|
| **Parametreler** | customer_id, listing_id, start_date, end_date, payment_type |
| **İşlev** | Yeni kiralama, maliyet hesaplama ve ödeme kaydı oluşturur |

**Kullanım:**
```sql
CALL sp_create_rental(4, 11, CURRENT_DATE, CURRENT_DATE + 7, 'CREDIT_CARD');
```

---

### 3. sp_process_payment
| Özellik | Değer |
|---------|-------|
| **Parametreler** | sale_id, rental_id, amount, payment_type |
| **İşlev** | Kısmi veya tam ödeme işler, satış durumunu günceller |

**Kullanım:**
```sql
CALL sp_process_payment(p_sale_id := 2, p_amount := 15000.0, p_payment_type := 'CASH');
```

---

### 4. sp_monthly_report
| Özellik | Değer |
|---------|-------|
| **Parametreler** | year, month |
| **İşlev** | Aylık satış ve kiralama özet raporu oluşturur |

**Kullanım:**
```sql
CALL sp_monthly_report(2024, 12);
```

---

### 5. sp_add_service_record
| Özellik | Değer |
|---------|-------|
| **Parametreler** | vehicle_id, description, cost, new_mileage |
| **İşlev** | Servis kaydı ekler ve araç km'sini günceller |

**Kullanım:**
```sql
CALL sp_add_service_record(1, 'Lastik değişimi ve balans', 800.0, 16000);
```

---

## Sonuç

**Saklı Prosedür Değerlendirmesi: BAŞARILI ✅**

5 anlamlı ve işlevsel stored procedure başarıyla oluşturulmuştur.
