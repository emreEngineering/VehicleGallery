# Kursor (Cursor) Değerlendirmesi
## Araç Galerisi Yönetim Sistemi

**Değerlendirme Tarihi:** 26 Aralık 2024  
**Dosya:** `database_init.sql`  
**Durum:** ✅ Eklendi ve Çalışıyor

---

## Eklenen Kursorlar (3 Adet)

### 1. fn_apply_discount_to_sales
| Özellik | Değer |
|---------|-------|
| **Cursor Adı** | `sale_cursor` |
| **Parametre** | `discount_percent` (indirim yüzdesi) |
| **İşlev** | Tüm satılık ilanlara toplu indirim uygular |

**Kullanım:**
```sql
SELECT * FROM fn_apply_discount_to_sales(10);  -- %10 indirim
```

---

### 2. fn_check_expired_insurances
| Özellik | Değer |
|---------|-------|
| **Cursor Adı** | `insurance_cursor` |
| **Parametre** | `days_ahead` (varsayılan 30 gün) |
| **İşlev** | Vadesi geçmiş/yaklaşan sigortaları listeler |

**Kullanım:**
```sql
SELECT * FROM fn_check_expired_insurances(60);  -- 60 gün içinde
```

---

### 3. fn_monthly_sales_detail
| Özellik | Değer |
|---------|-------|
| **Cursor Adı** | `sales_cursor` |
| **Parametreler** | `year`, `month` |
| **İşlev** | Aylık satış detay raporu oluşturur |

**Kullanım:**
```sql
SELECT * FROM fn_monthly_sales_detail(2024, 12);
```

---

## Sonuç

**Cursor Değerlendirmesi: BAŞARILI ✅**

3 anlamlı ve işlevsel cursor başarıyla oluşturulmuştur.
