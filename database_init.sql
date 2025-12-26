-- =====================================================
-- ARAÇ GALERİSİ YÖNETİM SİSTEMİ - PostgreSQL Veritabanı
-- Vehicle Gallery Management System Database Script
-- =====================================================

-- Veritabanı oluştur (eğer yoksa)
-- CREATE DATABASE vehiclegallery;
-- \c vehiclegallery

-- =====================================================
-- TABLOLARI OLUŞTURMA (CREATE TABLES)
-- =====================================================

-- 1. Personnel (Üst sınıf - Kalıtım için)
CREATE TABLE IF NOT EXISTS personnel (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

-- 2. Customers (Personnel'den kalıtım alır - JOINED strategy)
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT PRIMARY KEY REFERENCES personnel(id) ON DELETE CASCADE,
    national_id VARCHAR(255) UNIQUE,
    phone VARCHAR(255),
    customer_type VARCHAR(50) NOT NULL,  -- 'INDIVIDUAL' veya 'CORPORATE'
    birth_date VARCHAR(255),             -- Bireysel müşteriler için
    company_name VARCHAR(255),           -- Kurumsal müşteriler için
    tax_number VARCHAR(255)              -- Kurumsal müşteriler için
);

-- 3. Dealers (Personnel'den kalıtım alır - JOINED strategy)
CREATE TABLE IF NOT EXISTS dealers (
    id BIGINT PRIMARY KEY REFERENCES personnel(id) ON DELETE CASCADE,
    company_name VARCHAR(255),
    tax_number VARCHAR(255) UNIQUE
);

-- 4. Vehicles (Araçlar)
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    production_year INTEGER,
    color VARCHAR(255),
    plate_number VARCHAR(255) UNIQUE,
    mileage INTEGER,
    chassis_number VARCHAR(255) UNIQUE,
    vehicle_type VARCHAR(50) NOT NULL,   -- 'FUEL', 'ELECTRIC', 'HYBRID'
    fuel_type VARCHAR(50),               -- 'GASOLINE', 'DIESEL', 'LPG' (yakıtlı araçlar için)
    battery_capacity DOUBLE PRECISION,   -- Elektrikli araçlar için
    range_km INTEGER                     -- Elektrikli araçlar için
);

-- 5. Listings (İlanlar)
CREATE TABLE IF NOT EXISTS listings (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    dealer_id BIGINT NOT NULL REFERENCES personnel(id) ON DELETE CASCADE,
    publish_date DATE,
    description TEXT,
    listing_type VARCHAR(50) NOT NULL,   -- 'SALE' veya 'RENTAL'
    is_active BOOLEAN DEFAULT true,
    price DOUBLE PRECISION,              -- Satılık ilanlar için
    trade_in BOOLEAN,                    -- Satılık ilanlar için (takas durumu)
    daily_rate DOUBLE PRECISION,         -- Kiralık ilanlar için
    min_days INTEGER,                    -- Kiralık ilanlar için
    max_days INTEGER                     -- Kiralık ilanlar için
);

-- 6. Sales (Satışlar)
CREATE TABLE IF NOT EXISTS sales (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES personnel(id) ON DELETE CASCADE,
    listing_id BIGINT NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    date DATE,
    amount DOUBLE PRECISION,
    status VARCHAR(50)                   -- 'COMPLETED', 'PENDING', 'CANCELLED'
);

-- 7. Rentals (Kiralamalar)
CREATE TABLE IF NOT EXISTS rentals (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES personnel(id) ON DELETE CASCADE,
    listing_id BIGINT NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    start_date DATE,
    end_date DATE,
    total_cost DOUBLE PRECISION,
    status VARCHAR(50)                   -- 'ACTIVE', 'COMPLETED', 'CANCELLED'
);

-- 8. Addresses (Adresler)
CREATE TABLE IF NOT EXISTS addresses (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES personnel(id) ON DELETE CASCADE,
    city VARCHAR(255),
    district VARCHAR(255),
    neighborhood VARCHAR(255),
    street VARCHAR(255),
    building_no VARCHAR(50),
    postal_code VARCHAR(20)
);

-- 9. Payments (Ödemeler)
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    rental_id BIGINT REFERENCES rentals(id) ON DELETE SET NULL,
    sale_id BIGINT REFERENCES sales(id) ON DELETE SET NULL,
    date DATE,
    amount DOUBLE PRECISION,
    payment_type VARCHAR(50)             -- 'CASH', 'CREDIT_CARD', 'TRANSFER'
);

-- 10. Offers (Teklifler)
CREATE TABLE IF NOT EXISTS offers (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES personnel(id) ON DELETE CASCADE,
    listing_id BIGINT NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    offer_amount DOUBLE PRECISION,
    offer_date DATE,
    status VARCHAR(50)                   -- 'PENDING', 'ACCEPTED', 'REJECTED'
);

-- 11. Service Records (Servis Kayıtları)
CREATE TABLE IF NOT EXISTS service_records (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    date DATE,
    description TEXT,
    cost DOUBLE PRECISION
);

-- 12. Insurances (Sigortalar)
CREATE TABLE IF NOT EXISTS insurances (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    insurance_type VARCHAR(50),          -- 'TRAFFIC', 'COMPREHENSIVE'
    start_date DATE,
    end_date DATE
);

-- 13. Audit Log Tablosu (Trigger için)
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    operation VARCHAR(20) NOT NULL,      -- 'INSERT', 'UPDATE', 'DELETE'
    record_id BIGINT,
    old_data JSONB,
    new_data JSONB,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(255)
);

-- =====================================================
-- CHECK KISITLAYICILARI (CHECK CONSTRAINTS)
-- =====================================================

-- Customers: Müşteri tipi kontrolü
ALTER TABLE customers DROP CONSTRAINT IF EXISTS chk_customer_type;
ALTER TABLE customers ADD CONSTRAINT chk_customer_type 
    CHECK (customer_type IN ('INDIVIDUAL', 'CORPORATE'));

-- Vehicles: Araç tipi kontrolü
ALTER TABLE vehicles DROP CONSTRAINT IF EXISTS chk_vehicle_type;
ALTER TABLE vehicles ADD CONSTRAINT chk_vehicle_type 
    CHECK (vehicle_type IN ('FUEL', 'ELECTRIC', 'HYBRID'));

-- Vehicles: Yakıt tipi kontrolü
ALTER TABLE vehicles DROP CONSTRAINT IF EXISTS chk_fuel_type;
ALTER TABLE vehicles ADD CONSTRAINT chk_fuel_type 
    CHECK (fuel_type IS NULL OR fuel_type IN ('GASOLINE', 'DIESEL', 'LPG'));

-- Vehicles: Üretim yılı kontrolü
ALTER TABLE vehicles DROP CONSTRAINT IF EXISTS chk_production_year;
ALTER TABLE vehicles ADD CONSTRAINT chk_production_year 
    CHECK (production_year IS NULL OR (production_year >= 1900 AND production_year <= 2030));

-- Vehicles: Kilometre pozitif olmalı
ALTER TABLE vehicles DROP CONSTRAINT IF EXISTS chk_mileage;
ALTER TABLE vehicles ADD CONSTRAINT chk_mileage 
    CHECK (mileage IS NULL OR mileage >= 0);

-- Vehicles: Batarya kapasitesi pozitif olmalı
ALTER TABLE vehicles DROP CONSTRAINT IF EXISTS chk_battery_capacity;
ALTER TABLE vehicles ADD CONSTRAINT chk_battery_capacity 
    CHECK (battery_capacity IS NULL OR battery_capacity > 0);

-- Listings: İlan tipi kontrolü
ALTER TABLE listings DROP CONSTRAINT IF EXISTS chk_listing_type;
ALTER TABLE listings ADD CONSTRAINT chk_listing_type 
    CHECK (listing_type IN ('SALE', 'RENTAL'));

-- Listings: Fiyat pozitif olmalı
ALTER TABLE listings DROP CONSTRAINT IF EXISTS chk_price;
ALTER TABLE listings ADD CONSTRAINT chk_price 
    CHECK (price IS NULL OR price > 0);

-- Listings: Günlük ücret pozitif olmalı
ALTER TABLE listings DROP CONSTRAINT IF EXISTS chk_daily_rate;
ALTER TABLE listings ADD CONSTRAINT chk_daily_rate 
    CHECK (daily_rate IS NULL OR daily_rate > 0);

-- Listings: Min/Max gün kontrolü
ALTER TABLE listings DROP CONSTRAINT IF EXISTS chk_min_max_days;
ALTER TABLE listings ADD CONSTRAINT chk_min_max_days 
    CHECK (min_days IS NULL OR max_days IS NULL OR min_days <= max_days);

-- Sales: Satış durumu kontrolü
ALTER TABLE sales DROP CONSTRAINT IF EXISTS chk_sale_status;
ALTER TABLE sales ADD CONSTRAINT chk_sale_status 
    CHECK (status IS NULL OR status IN ('COMPLETED', 'PENDING', 'CANCELLED'));

-- Sales: Tutar pozitif olmalı
ALTER TABLE sales DROP CONSTRAINT IF EXISTS chk_sale_amount;
ALTER TABLE sales ADD CONSTRAINT chk_sale_amount 
    CHECK (amount IS NULL OR amount > 0);

-- Rentals: Kiralama durumu kontrolü
ALTER TABLE rentals DROP CONSTRAINT IF EXISTS chk_rental_status;
ALTER TABLE rentals ADD CONSTRAINT chk_rental_status 
    CHECK (status IS NULL OR status IN ('ACTIVE', 'COMPLETED', 'CANCELLED'));

-- Rentals: Tarih kontrolü (bitiş >= başlangıç)
ALTER TABLE rentals DROP CONSTRAINT IF EXISTS chk_rental_dates;
ALTER TABLE rentals ADD CONSTRAINT chk_rental_dates 
    CHECK (start_date IS NULL OR end_date IS NULL OR end_date >= start_date);

-- Rentals: Toplam maliyet pozitif olmalı
ALTER TABLE rentals DROP CONSTRAINT IF EXISTS chk_rental_cost;
ALTER TABLE rentals ADD CONSTRAINT chk_rental_cost 
    CHECK (total_cost IS NULL OR total_cost >= 0);

-- Payments: Ödeme tipi kontrolü
ALTER TABLE payments DROP CONSTRAINT IF EXISTS chk_payment_type;
ALTER TABLE payments ADD CONSTRAINT chk_payment_type 
    CHECK (payment_type IS NULL OR payment_type IN ('CASH', 'CREDIT_CARD', 'TRANSFER'));

-- Payments: Tutar pozitif olmalı
ALTER TABLE payments DROP CONSTRAINT IF EXISTS chk_payment_amount;
ALTER TABLE payments ADD CONSTRAINT chk_payment_amount 
    CHECK (amount IS NULL OR amount > 0);

-- Offers: Teklif durumu kontrolü
ALTER TABLE offers DROP CONSTRAINT IF EXISTS chk_offer_status;
ALTER TABLE offers ADD CONSTRAINT chk_offer_status 
    CHECK (status IS NULL OR status IN ('PENDING', 'ACCEPTED', 'REJECTED'));

-- Offers: Teklif tutarı pozitif olmalı
ALTER TABLE offers DROP CONSTRAINT IF EXISTS chk_offer_amount;
ALTER TABLE offers ADD CONSTRAINT chk_offer_amount 
    CHECK (offer_amount IS NULL OR offer_amount > 0);

-- Service Records: Maliyet pozitif olmalı
ALTER TABLE service_records DROP CONSTRAINT IF EXISTS chk_service_cost;
ALTER TABLE service_records ADD CONSTRAINT chk_service_cost 
    CHECK (cost IS NULL OR cost >= 0);

-- Insurances: Sigorta tipi kontrolü
ALTER TABLE insurances DROP CONSTRAINT IF EXISTS chk_insurance_type;
ALTER TABLE insurances ADD CONSTRAINT chk_insurance_type 
    CHECK (insurance_type IS NULL OR insurance_type IN ('TRAFFIC', 'COMPREHENSIVE'));

-- Insurances: Tarih kontrolü (bitiş >= başlangıç)
ALTER TABLE insurances DROP CONSTRAINT IF EXISTS chk_insurance_dates;
ALTER TABLE insurances ADD CONSTRAINT chk_insurance_dates 
    CHECK (start_date IS NULL OR end_date IS NULL OR end_date >= start_date);

-- Audit Log: İşlem tipi kontrolü
ALTER TABLE audit_log DROP CONSTRAINT IF EXISTS chk_audit_operation;
ALTER TABLE audit_log ADD CONSTRAINT chk_audit_operation 
    CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE'));

-- =====================================================
-- TETİKLEYİCİLER (TRIGGERS)
-- =====================================================

-- -------------------------------------------------------
-- TRIGGER 1: Satış tamamlandığında ilanı deaktif yap
-- Açıklama: Bir satış 'COMPLETED' durumuna geçtiğinde,
-- ilgili ilanın is_active değerini false yapar.
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION deactivate_listing_on_sale()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'COMPLETED' AND (OLD.status IS NULL OR OLD.status != 'COMPLETED') THEN
        UPDATE listings 
        SET is_active = false 
        WHERE id = NEW.listing_id;
        
        RAISE NOTICE 'Ilan #% deaktif edildi (Satis #% tamamlandi)', NEW.listing_id, NEW.id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_deactivate_listing_on_sale ON sales;
CREATE TRIGGER trg_deactivate_listing_on_sale
    AFTER INSERT OR UPDATE ON sales
    FOR EACH ROW
    EXECUTE FUNCTION deactivate_listing_on_sale();

-- -------------------------------------------------------
-- TRIGGER 2: Kiralama toplam maliyetini otomatik hesapla
-- Açıklama: Kiralama eklendiğinde veya güncellendiğinde,
-- günlük ücret x gün sayısı ile toplam maliyeti hesaplar.
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION calculate_rental_cost()
RETURNS TRIGGER AS $$
DECLARE
    daily_rate_val DOUBLE PRECISION;
    days_count INTEGER;
BEGIN
    -- İlandan günlük ücreti al
    SELECT daily_rate INTO daily_rate_val 
    FROM listings 
    WHERE id = NEW.listing_id;
    
    -- Gün sayısını hesapla
    days_count := NEW.end_date - NEW.start_date;
    
    -- Eğer gün sayısı 0 veya negatifse, en az 1 gün say
    IF days_count < 1 THEN
        days_count := 1;
    END IF;
    
    -- Toplam maliyeti hesapla (eğer null ise)
    IF NEW.total_cost IS NULL AND daily_rate_val IS NOT NULL THEN
        NEW.total_cost := daily_rate_val * days_count;
        RAISE NOTICE 'Kiralama maliyeti hesaplandi: % gun x $% = $%', 
                     days_count, daily_rate_val, NEW.total_cost;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_calculate_rental_cost ON rentals;
CREATE TRIGGER trg_calculate_rental_cost
    BEFORE INSERT OR UPDATE ON rentals
    FOR EACH ROW
    EXECUTE FUNCTION calculate_rental_cost();

-- -------------------------------------------------------
-- TRIGGER 3: Servis kaydı eklendiğinde araç km güncelle
-- Açıklama: Servis kaydı eklenirken açıklamada km bilgisi
-- varsa, araç kilometresini günceller.
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION update_vehicle_after_service()
RETURNS TRIGGER AS $$
BEGIN
    -- Servis kaydı eklendiğinde bildirim
    RAISE NOTICE 'Arac #% icin yeni servis kaydi eklendi: %', 
                 NEW.vehicle_id, NEW.description;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_vehicle_after_service ON service_records;
CREATE TRIGGER trg_update_vehicle_after_service
    AFTER INSERT ON service_records
    FOR EACH ROW
    EXECUTE FUNCTION update_vehicle_after_service();

-- -------------------------------------------------------
-- TRIGGER 4: Teklif kabul edildiğinde otomatik satış oluştur
-- Açıklama: Bir teklif 'ACCEPTED' durumuna geçtiğinde,
-- otomatik olarak bir satış kaydı oluşturur.
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION create_sale_on_offer_accepted()
RETURNS TRIGGER AS $$
DECLARE
    listing_type_val VARCHAR(50);
BEGIN
    IF NEW.status = 'ACCEPTED' AND (OLD.status IS NULL OR OLD.status != 'ACCEPTED') THEN
        -- İlan tipini kontrol et (sadece SALE ilanları için)
        SELECT listing_type INTO listing_type_val 
        FROM listings 
        WHERE id = NEW.listing_id;
        
        IF listing_type_val = 'SALE' THEN
            -- Satış kaydı oluştur
            INSERT INTO sales (customer_id, listing_id, date, amount, status)
            VALUES (NEW.customer_id, NEW.listing_id, CURRENT_DATE, NEW.offer_amount, 'PENDING');
            
            RAISE NOTICE 'Teklif #% kabul edildi, satis kaydi olusturuldu', NEW.id;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_create_sale_on_offer_accepted ON offers;
CREATE TRIGGER trg_create_sale_on_offer_accepted
    AFTER UPDATE ON offers
    FOR EACH ROW
    EXECUTE FUNCTION create_sale_on_offer_accepted();

-- -------------------------------------------------------
-- TRIGGER 5: Araç işlemlerini audit log'a kaydet
-- Açıklama: Araç tablosundaki tüm değişiklikleri
-- audit_log tablosuna kaydeder.
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION log_vehicle_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (table_name, operation, record_id, new_data)
        VALUES ('vehicles', 'INSERT', NEW.id, row_to_json(NEW)::jsonb);
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_log (table_name, operation, record_id, old_data, new_data)
        VALUES ('vehicles', 'UPDATE', NEW.id, row_to_json(OLD)::jsonb, row_to_json(NEW)::jsonb);
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit_log (table_name, operation, record_id, old_data)
        VALUES ('vehicles', 'DELETE', OLD.id, row_to_json(OLD)::jsonb);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_log_vehicle_changes ON vehicles;
CREATE TRIGGER trg_log_vehicle_changes
    AFTER INSERT OR UPDATE OR DELETE ON vehicles
    FOR EACH ROW
    EXECUTE FUNCTION log_vehicle_changes();

-- =====================================================
-- SAKLI PROSEDÜRLER (STORED PROCEDURES)
-- =====================================================

-- -------------------------------------------------------
-- PROCEDURE 1: Yeni Satış Oluştur
-- Açıklama: Müşteri, ilan ve tutar bilgisiyle yeni satış
-- kaydı oluşturur ve ödeme kaydı ekler.
-- -------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_create_sale(
    p_customer_id BIGINT,
    p_listing_id BIGINT,
    p_amount DOUBLE PRECISION,
    p_payment_type VARCHAR(50) DEFAULT 'CASH'
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_sale_id BIGINT;
    v_listing_type VARCHAR(50);
BEGIN
    -- İlan tipini kontrol et
    SELECT listing_type INTO v_listing_type 
    FROM listings 
    WHERE id = p_listing_id AND is_active = true;
    
    IF v_listing_type IS NULL THEN
        RAISE EXCEPTION 'Ilan bulunamadi veya aktif degil: %', p_listing_id;
    END IF;
    
    IF v_listing_type != 'SALE' THEN
        RAISE EXCEPTION 'Bu ilan satilik degil: %', p_listing_id;
    END IF;
    
    -- Satış kaydı oluştur
    INSERT INTO sales (customer_id, listing_id, date, amount, status)
    VALUES (p_customer_id, p_listing_id, CURRENT_DATE, p_amount, 'COMPLETED')
    RETURNING id INTO v_sale_id;
    
    -- Ödeme kaydı oluştur
    INSERT INTO payments (sale_id, date, amount, payment_type)
    VALUES (v_sale_id, CURRENT_DATE, p_amount, p_payment_type);
    
    -- İlanı deaktif et
    UPDATE listings SET is_active = false WHERE id = p_listing_id;
    
    RAISE NOTICE 'Satis basariyla olusturuldu. Satis ID: %, Tutar: $%', v_sale_id, p_amount;
END;
$$;

-- -------------------------------------------------------
-- PROCEDURE 2: Yeni Kiralama Oluştur
-- Açıklama: Müşteri, ilan, başlangıç ve bitiş tarihleriyle
-- yeni kiralama kaydı oluşturur.
-- -------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_create_rental(
    p_customer_id BIGINT,
    p_listing_id BIGINT,
    p_start_date DATE,
    p_end_date DATE,
    p_payment_type VARCHAR(50) DEFAULT 'CREDIT_CARD'
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_rental_id BIGINT;
    v_daily_rate DOUBLE PRECISION;
    v_days INTEGER;
    v_total_cost DOUBLE PRECISION;
    v_listing_type VARCHAR(50);
BEGIN
    -- İlan tipini kontrol et
    SELECT listing_type, daily_rate INTO v_listing_type, v_daily_rate
    FROM listings 
    WHERE id = p_listing_id AND is_active = true;
    
    IF v_listing_type IS NULL THEN
        RAISE EXCEPTION 'Ilan bulunamadi veya aktif degil: %', p_listing_id;
    END IF;
    
    IF v_listing_type != 'RENTAL' THEN
        RAISE EXCEPTION 'Bu ilan kiralik degil: %', p_listing_id;
    END IF;
    
    -- Gün sayısı ve toplam maliyeti hesapla
    v_days := p_end_date - p_start_date;
    IF v_days < 1 THEN v_days := 1; END IF;
    v_total_cost := v_daily_rate * v_days;
    
    -- Kiralama kaydı oluştur
    INSERT INTO rentals (customer_id, listing_id, start_date, end_date, total_cost, status)
    VALUES (p_customer_id, p_listing_id, p_start_date, p_end_date, v_total_cost, 'ACTIVE')
    RETURNING id INTO v_rental_id;
    
    -- Ödeme kaydı oluştur
    INSERT INTO payments (rental_id, date, amount, payment_type)
    VALUES (v_rental_id, CURRENT_DATE, v_total_cost, p_payment_type);
    
    RAISE NOTICE 'Kiralama basariyla olusturuldu. Kiralama ID: %, % gun, Toplam: $%', 
                 v_rental_id, v_days, v_total_cost;
END;
$$;

-- -------------------------------------------------------
-- PROCEDURE 3: Ödeme İşle
-- Açıklama: Belirli bir satış veya kiralama için
-- kısmi veya tam ödeme işler.
-- -------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_process_payment(
    p_amount DOUBLE PRECISION,
    p_sale_id BIGINT DEFAULT NULL,
    p_rental_id BIGINT DEFAULT NULL,
    p_payment_type VARCHAR(50) DEFAULT 'CASH'
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_total_paid DOUBLE PRECISION;
    v_required_amount DOUBLE PRECISION;
BEGIN
    IF p_sale_id IS NULL AND p_rental_id IS NULL THEN
        RAISE EXCEPTION 'Satis veya kiralama ID girilmeli';
    END IF;
    
    IF p_sale_id IS NOT NULL AND p_rental_id IS NOT NULL THEN
        RAISE EXCEPTION 'Ayni anda hem satis hem kiralama ID girilemez';
    END IF;
    
    -- Ödeme kaydı ekle
    INSERT INTO payments (sale_id, rental_id, date, amount, payment_type)
    VALUES (p_sale_id, p_rental_id, CURRENT_DATE, p_amount, p_payment_type);
    
    -- Satış için toplam ödeme kontrolü
    IF p_sale_id IS NOT NULL THEN
        SELECT COALESCE(SUM(amount), 0) INTO v_total_paid
        FROM payments WHERE sale_id = p_sale_id;
        
        SELECT amount INTO v_required_amount FROM sales WHERE id = p_sale_id;
        
        IF v_total_paid >= v_required_amount THEN
            UPDATE sales SET status = 'COMPLETED' WHERE id = p_sale_id;
            RAISE NOTICE 'Satis #% tamamlandi. Toplam odenen: $%', p_sale_id, v_total_paid;
        ELSE
            RAISE NOTICE 'Satis #% icin odeme alindi. Toplam: $%, Kalan: $%', 
                         p_sale_id, v_total_paid, v_required_amount - v_total_paid;
        END IF;
    END IF;
    
    -- Kiralama için toplam ödeme kontrolü
    IF p_rental_id IS NOT NULL THEN
        SELECT COALESCE(SUM(amount), 0) INTO v_total_paid
        FROM payments WHERE rental_id = p_rental_id;
        
        RAISE NOTICE 'Kiralama #% icin odeme alindi. Toplam odenen: $%', p_rental_id, v_total_paid;
    END IF;
END;
$$;

-- -------------------------------------------------------
-- PROCEDURE 4: Aylık Satış Raporu Oluştur
-- Açıklama: Belirli ay için satış ve kiralama özet
-- raporunu oluşturur.
-- -------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_monthly_report(
    p_year INTEGER,
    p_month INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_total_sales DOUBLE PRECISION;
    v_sales_count INTEGER;
    v_total_rentals DOUBLE PRECISION;
    v_rentals_count INTEGER;
    v_start_date DATE;
    v_end_date DATE;
BEGIN
    v_start_date := make_date(p_year, p_month, 1);
    v_end_date := (v_start_date + INTERVAL '1 month' - INTERVAL '1 day')::DATE;
    
    -- Satış istatistikleri
    SELECT COUNT(*), COALESCE(SUM(amount), 0)
    INTO v_sales_count, v_total_sales
    FROM sales
    WHERE date BETWEEN v_start_date AND v_end_date
    AND status = 'COMPLETED';
    
    -- Kiralama istatistikleri
    SELECT COUNT(*), COALESCE(SUM(total_cost), 0)
    INTO v_rentals_count, v_total_rentals
    FROM rentals
    WHERE start_date BETWEEN v_start_date AND v_end_date;
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'AYLIK RAPOR: %/%', p_month, p_year;
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Tamamlanan Satis Sayisi: %', v_sales_count;
    RAISE NOTICE 'Toplam Satis Geliri: $%', v_total_sales;
    RAISE NOTICE 'Kiralama Sayisi: %', v_rentals_count;
    RAISE NOTICE 'Toplam Kiralama Geliri: $%', v_total_rentals;
    RAISE NOTICE 'TOPLAM GELIR: $%', v_total_sales + v_total_rentals;
    RAISE NOTICE '========================================';
END;
$$;

-- -------------------------------------------------------
-- PROCEDURE 5: Araç Bakım Durumunu Güncelle
-- Açıklama: Belirli bir araç için servis kaydı ekler
-- ve kilometre bilgisini günceller.
-- -------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_add_service_record(
    p_vehicle_id BIGINT,
    p_description TEXT,
    p_cost DOUBLE PRECISION,
    p_new_mileage INTEGER DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_current_mileage INTEGER;
BEGIN
    -- Aracı kontrol et
    SELECT mileage INTO v_current_mileage
    FROM vehicles
    WHERE id = p_vehicle_id;
    
    IF v_current_mileage IS NULL THEN
        RAISE EXCEPTION 'Arac bulunamadi: %', p_vehicle_id;
    END IF;
    
    -- Servis kaydı ekle
    INSERT INTO service_records (vehicle_id, date, description, cost)
    VALUES (p_vehicle_id, CURRENT_DATE, p_description, p_cost);
    
    -- Kilometre güncelle (eğer yeni değer verilmişse ve eskisinden büyükse)
    IF p_new_mileage IS NOT NULL AND p_new_mileage > v_current_mileage THEN
        UPDATE vehicles SET mileage = p_new_mileage WHERE id = p_vehicle_id;
        RAISE NOTICE 'Arac #% km guncellendi: % -> %', p_vehicle_id, v_current_mileage, p_new_mileage;
    END IF;
    
    RAISE NOTICE 'Servis kaydi eklendi. Arac #%, Maliyet: $%', p_vehicle_id, p_cost;
END;
$$;

-- =====================================================
-- KURSORLAR (CURSORS)
-- =====================================================

-- -------------------------------------------------------
-- CURSOR 1: Tüm Satılık İlanlara Toplu İndirim Uygula
-- Açıklama: Cursor ile tüm aktif satılık ilanları gezer
-- ve belirtilen oranda indirim uygular.
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_apply_discount_to_sales(
    p_discount_percent DOUBLE PRECISION
)
RETURNS TABLE (
    listing_id BIGINT,
    vehicle_info TEXT,
    old_price DOUBLE PRECISION,
    new_price DOUBLE PRECISION
)
LANGUAGE plpgsql
AS $$
DECLARE
    sale_cursor CURSOR FOR
        SELECT l.id, v.brand || ' ' || v.model as vehicle, l.price
        FROM listings l
        JOIN vehicles v ON l.vehicle_id = v.id
        WHERE l.listing_type = 'SALE' 
        AND l.is_active = true
        AND l.price IS NOT NULL;
    
    v_listing_id BIGINT;
    v_vehicle TEXT;
    v_old_price DOUBLE PRECISION;
    v_new_price DOUBLE PRECISION;
    v_count INTEGER := 0;
BEGIN
    -- Cursor'ı aç
    OPEN sale_cursor;
    
    LOOP
        -- Cursor'dan veri çek
        FETCH sale_cursor INTO v_listing_id, v_vehicle, v_old_price;
        EXIT WHEN NOT FOUND;
        
        -- Yeni fiyatı hesapla
        v_new_price := v_old_price * (1 - p_discount_percent / 100);
        
        -- Fiyatı güncelle
        UPDATE listings SET price = v_new_price WHERE id = v_listing_id;
        
        -- Sonucu döndür
        listing_id := v_listing_id;
        vehicle_info := v_vehicle;
        old_price := v_old_price;
        new_price := v_new_price;
        RETURN NEXT;
        
        v_count := v_count + 1;
    END LOOP;
    
    -- Cursor'ı kapat
    CLOSE sale_cursor;
    
    RAISE NOTICE '% adet ilana %%% indirim uygulandi', v_count, p_discount_percent;
END;
$$;

-- -------------------------------------------------------
-- CURSOR 2: Vadesi Geçmiş Sigortaları Listele
-- Açıklama: Cursor ile tüm araçları gezer ve vadesi
-- geçmiş veya yaklaşan sigortaları raporlar.
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_check_expired_insurances(
    p_days_ahead INTEGER DEFAULT 30
)
RETURNS TABLE (
    vehicle_id BIGINT,
    vehicle_info TEXT,
    insurance_type VARCHAR(50),
    end_date DATE,
    status TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    insurance_cursor CURSOR FOR
        SELECT i.vehicle_id, v.brand || ' ' || v.model || ' (' || v.plate_number || ')' as vehicle,
               i.insurance_type, i.end_date
        FROM insurances i
        JOIN vehicles v ON i.vehicle_id = v.id
        ORDER BY i.end_date;
    
    v_vehicle_id BIGINT;
    v_vehicle TEXT;
    v_type VARCHAR(50);
    v_end DATE;
    v_status TEXT;
    v_expired_count INTEGER := 0;
    v_expiring_count INTEGER := 0;
BEGIN
    OPEN insurance_cursor;
    
    LOOP
        FETCH insurance_cursor INTO v_vehicle_id, v_vehicle, v_type, v_end;
        EXIT WHEN NOT FOUND;
        
        -- Durumu belirle
        IF v_end < CURRENT_DATE THEN
            v_status := 'SURESI GECMIS';
            v_expired_count := v_expired_count + 1;
        ELSIF v_end <= CURRENT_DATE + p_days_ahead THEN
            v_status := 'YAKLASIYOR (' || (v_end - CURRENT_DATE) || ' gun)';
            v_expiring_count := v_expiring_count + 1;
        ELSE
            v_status := 'GECERLI';
        END IF;
        
        -- Sadece süresi geçmiş veya yaklaşanları döndür
        IF v_end <= CURRENT_DATE + p_days_ahead THEN
            vehicle_id := v_vehicle_id;
            vehicle_info := v_vehicle;
            insurance_type := v_type;
            end_date := v_end;
            status := v_status;
            RETURN NEXT;
        END IF;
    END LOOP;
    
    CLOSE insurance_cursor;
    
    RAISE NOTICE 'Sigorta Kontrolu: % suresi gecmis, % yaklasıyor', v_expired_count, v_expiring_count;
END;
$$;

-- -------------------------------------------------------
-- CURSOR 3: Aylık Satış Detay Raporu
-- Açıklama: Cursor ile belirli aydaki tüm satışları
-- gezerek detaylı rapor oluşturur.
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_monthly_sales_detail(
    p_year INTEGER,
    p_month INTEGER
)
RETURNS TABLE (
    sale_id BIGINT,
    sale_date DATE,
    customer_name TEXT,
    vehicle_info TEXT,
    amount DOUBLE PRECISION,
    status VARCHAR(50),
    payment_status TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    sales_cursor CURSOR FOR
        SELECT s.id, s.date, 
               p.first_name || ' ' || p.last_name as customer,
               v.brand || ' ' || v.model as vehicle,
               s.amount, s.status
        FROM sales s
        JOIN personnel p ON s.customer_id = p.id
        JOIN listings l ON s.listing_id = l.id
        JOIN vehicles v ON l.vehicle_id = v.id
        WHERE EXTRACT(YEAR FROM s.date) = p_year
        AND EXTRACT(MONTH FROM s.date) = p_month
        ORDER BY s.date;
    
    v_sale_id BIGINT;
    v_date DATE;
    v_customer TEXT;
    v_vehicle TEXT;
    v_amount DOUBLE PRECISION;
    v_status VARCHAR(50);
    v_total_paid DOUBLE PRECISION;
    v_payment_status TEXT;
    v_total_sales DOUBLE PRECISION := 0;
    v_count INTEGER := 0;
BEGIN
    OPEN sales_cursor;
    
    LOOP
        FETCH sales_cursor INTO v_sale_id, v_date, v_customer, v_vehicle, v_amount, v_status;
        EXIT WHEN NOT FOUND;
        
        -- Ödeme durumunu kontrol et
        SELECT COALESCE(SUM(pay.amount), 0) INTO v_total_paid
        FROM payments pay WHERE pay.sale_id = v_sale_id;
        
        IF v_total_paid >= v_amount THEN
            v_payment_status := 'TAM ODENDI';
        ELSIF v_total_paid > 0 THEN
            v_payment_status := 'KISMI ($' || v_total_paid || '/$' || v_amount || ')';
        ELSE
            v_payment_status := 'ODENMEDI';
        END IF;
        
        -- Sonucu döndür
        sale_id := v_sale_id;
        sale_date := v_date;
        customer_name := v_customer;
        vehicle_info := v_vehicle;
        amount := v_amount;
        status := v_status;
        payment_status := v_payment_status;
        RETURN NEXT;
        
        v_total_sales := v_total_sales + v_amount;
        v_count := v_count + 1;
    END LOOP;
    
    CLOSE sales_cursor;
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'AYLIK SATIS DETAY RAPORU: %/%', p_month, p_year;
    RAISE NOTICE 'Toplam Satis Sayisi: %', v_count;
    RAISE NOTICE 'Toplam Satis Tutari: $%', v_total_sales;
    RAISE NOTICE '========================================';
END;
$$;

-- =====================================================
-- AGGREGATE FONKSİYONLARI (Parametreli)
-- =====================================================

-- -------------------------------------------------------
-- FUNCTION 1: Duruma göre toplam satış tutarı (SUM)
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_total_sales_by_status(p_status VARCHAR)
RETURNS DOUBLE PRECISION
LANGUAGE plpgsql
AS $$
DECLARE
    v_total DOUBLE PRECISION;
BEGIN
    SELECT COALESCE(SUM(amount), 0) INTO v_total
    FROM sales
    WHERE status = p_status;
    
    RETURN v_total;
END;
$$;

-- -------------------------------------------------------
-- FUNCTION 2: Duruma göre ortalama satış tutarı (AVG)
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_avg_sales_by_status(p_status VARCHAR)
RETURNS DOUBLE PRECISION
LANGUAGE plpgsql
AS $$
DECLARE
    v_avg DOUBLE PRECISION;
BEGIN
    SELECT COALESCE(AVG(amount), 0) INTO v_avg
    FROM sales
    WHERE status = p_status;
    
    RETURN v_avg;
END;
$$;

-- -------------------------------------------------------
-- FUNCTION 3: Tarih aralığına göre satış özeti
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_sales_summary_by_date(
    p_start_date DATE,
    p_end_date DATE
)
RETURNS TABLE (
    total_count BIGINT,
    total_amount DOUBLE PRECISION,
    avg_amount DOUBLE PRECISION,
    min_amount DOUBLE PRECISION,
    max_amount DOUBLE PRECISION
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*)::BIGINT as total_count,
        COALESCE(SUM(amount), 0) as total_amount,
        COALESCE(AVG(amount), 0) as avg_amount,
        COALESCE(MIN(amount), 0) as min_amount,
        COALESCE(MAX(amount), 0) as max_amount
    FROM sales
    WHERE date BETWEEN p_start_date AND p_end_date;
END;
$$;

-- -------------------------------------------------------
-- FUNCTION 4: Müşteri bazlı istatistikler
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_customer_stats(p_customer_id BIGINT)
RETURNS TABLE (
    sale_count BIGINT,
    total_spent DOUBLE PRECISION,
    avg_purchase DOUBLE PRECISION,
    rental_count BIGINT,
    total_rental_spent DOUBLE PRECISION
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        (SELECT COUNT(*) FROM sales WHERE customer_id = p_customer_id)::BIGINT,
        (SELECT COALESCE(SUM(amount), 0) FROM sales WHERE customer_id = p_customer_id),
        (SELECT COALESCE(AVG(amount), 0) FROM sales WHERE customer_id = p_customer_id),
        (SELECT COUNT(*) FROM rentals WHERE customer_id = p_customer_id)::BIGINT,
        (SELECT COALESCE(SUM(total_cost), 0) FROM rentals WHERE customer_id = p_customer_id);
END;
$$;

-- -------------------------------------------------------
-- FUNCTION 5: Araç tipi bazlı ilan istatistikleri
-- -------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_listing_stats_by_vehicle_type(p_vehicle_type VARCHAR)
RETURNS TABLE (
    listing_count BIGINT,
    avg_price DOUBLE PRECISION,
    min_price DOUBLE PRECISION,
    max_price DOUBLE PRECISION,
    avg_daily_rate DOUBLE PRECISION
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(l.id)::BIGINT,
        COALESCE(AVG(l.price), 0),
        COALESCE(MIN(l.price), 0),
        COALESCE(MAX(l.price), 0),
        COALESCE(AVG(l.daily_rate), 0)
    FROM listings l
    JOIN vehicles v ON l.vehicle_id = v.id
    WHERE v.vehicle_type = p_vehicle_type AND l.is_active = true;
END;
$$;

-- =======================================================
-- ÖRNEK VERİLERİ EKLEME (INSERT DATA)
-- =====================================================

-- =====================================================
-- PERSONNEL / DEALERS / CUSTOMERS (15 kişi)
-- =====================================================

INSERT INTO personnel (id, first_name, last_name, email, password, title) VALUES
-- Dealers (Galericiler)
(1, 'Ali', 'Ozturk', 'ali@galeri.com', '123456', 'Sales Manager'),
(2, 'Fatma', 'Kaya', 'fatma@galeri.com', '123456', 'Sales Representative'),
(3, 'Hasan', 'Celik', 'hasan@galeri.com', '123456', 'Senior Dealer'),
-- Individual Customers (Bireysel Müşteriler)
(4, 'Ahmet', 'Yilmaz', 'ahmet@email.com', '123456', NULL),
(5, 'Mehmet', 'Demir', 'mehmet@email.com', '123456', NULL),
(6, 'Ayse', 'Sahin', 'ayse@email.com', '123456', NULL),
(7, 'Zeynep', 'Arslan', 'zeynep@email.com', '123456', NULL),
(8, 'Mustafa', 'Yildiz', 'mustafa@email.com', '123456', NULL),
(9, 'Elif', 'Ozdemir', 'elif@email.com', '123456', NULL),
(10, 'Emre', 'Koç', 'emre@email.com', '123456', NULL),
(11, 'Selin', 'Cetin', 'selin@email.com', '123456', NULL),
-- Corporate Customers (Kurumsal Müşteriler)
(12, 'Tech', 'Corp', 'fleet@techcorp.com', '123456', NULL),
(13, 'ABC', 'Holding', 'arac@abcholding.com', '123456', NULL),
(14, 'Logistik', 'AS', 'filo@logistik.com', '123456', NULL),
(15, 'Turizm', 'Ltd', 'rent@turizm.com', '123456', NULL)
ON CONFLICT (id) DO NOTHING;

SELECT setval('personnel_id_seq', (SELECT COALESCE(MAX(id), 1) FROM personnel));

-- Dealers
INSERT INTO dealers (id, company_name, tax_number) VALUES
(1, 'Premium Auto Gallery', '9876543210'),
(2, 'Kaya Otomotiv', '8765432109'),
(3, 'Celik Motors', '7654321098')
ON CONFLICT (id) DO NOTHING;

-- Customers (Bireysel)
INSERT INTO customers (id, national_id, phone, customer_type, birth_date, company_name, tax_number) VALUES
(4, '12345678901', '+90 532 123 4567', 'INDIVIDUAL', '1985-03-15', NULL, NULL),
(5, '98765432109', '+90 542 987 6543', 'INDIVIDUAL', '1990-07-22', NULL, NULL),
(6, '11122233344', '+90 505 111 2233', 'INDIVIDUAL', '1988-11-30', NULL, NULL),
(7, '55566677788', '+90 533 555 6677', 'INDIVIDUAL', '1995-05-18', NULL, NULL),
(8, '99988877766', '+90 544 999 8877', 'INDIVIDUAL', '1978-09-25', NULL, NULL),
(9, '22233344455', '+90 506 222 3344', 'INDIVIDUAL', '1992-01-12', NULL, NULL),
(10, '66677788899', '+90 535 666 7788', 'INDIVIDUAL', '1987-06-08', NULL, NULL),
(11, '33344455566', '+90 507 333 4455', 'INDIVIDUAL', '1993-12-03', NULL, NULL)
ON CONFLICT (id) DO NOTHING;

-- Customers (Kurumsal)
INSERT INTO customers (id, national_id, phone, customer_type, birth_date, company_name, tax_number) VALUES
(12, NULL, '+90 212 555 1234', 'CORPORATE', NULL, 'Tech Corp Ltd.', '1234567890'),
(13, NULL, '+90 216 444 5678', 'CORPORATE', NULL, 'ABC Holding A.S.', '2345678901'),
(14, NULL, '+90 312 333 9012', 'CORPORATE', NULL, 'Logistik Tasimacilik A.S.', '3456789012'),
(15, NULL, '+90 232 222 3456', 'CORPORATE', NULL, 'Ege Turizm Ltd. Sti.', '4567890123')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- VEHICLES (20 araç)
-- =====================================================

INSERT INTO vehicles (id, brand, model, production_year, color, plate_number, mileage, chassis_number, vehicle_type, fuel_type, battery_capacity, range_km) VALUES
-- Electric Vehicles (Elektrikli)
(1, 'Tesla', 'Model 3', 2023, 'White', '34 ABC 123', 15000, '5YJ3E1EA5NF000001', 'ELECTRIC', NULL, 82.0, 580),
(2, 'Tesla', 'Model Y', 2024, 'Red', '34 TES 001', 5000, '5YJ3E1EA5NF000002', 'ELECTRIC', NULL, 75.0, 533),
(3, 'Audi', 'Q4 e-tron', 2023, 'Blue', '07 MNO 345', 12000, 'WAUZZZF48NA012345', 'ELECTRIC', NULL, 77.0, 520),
(4, 'BMW', 'iX3', 2023, 'Black', '06 BMW 333', 18000, 'WBAXXXXXXX000003', 'ELECTRIC', NULL, 80.0, 460),
(5, 'Mercedes', 'EQC', 2022, 'Silver', '34 EQC 500', 25000, 'WDCXXXXXXX000004', 'ELECTRIC', NULL, 80.0, 437),

-- Fuel Vehicles (Yakıtlı)
(6, 'BMW', '320i', 2022, 'Black', '06 DEF 456', 32000, 'WBA5R1C50NAJ12345', 'FUEL', 'GASOLINE', NULL, NULL),
(7, 'Mercedes', 'E 200', 2021, 'Gray', '34 JKL 012', 48000, 'WDD2130401A123456', 'FUEL', 'DIESEL', NULL, NULL),
(8, 'Audi', 'A4', 2022, 'White', '35 AUD 100', 28000, 'WAUXXXXXXX000005', 'FUEL', 'GASOLINE', NULL, NULL),
(9, 'Volkswagen', 'Passat', 2021, 'Blue', '34 VW 2021', 55000, 'WVWXXXXXXX000006', 'FUEL', 'DIESEL', NULL, NULL),
(10, 'Ford', 'Focus', 2020, 'Red', '06 FRD 200', 65000, 'WFXXXXXXXX000007', 'FUEL', 'GASOLINE', NULL, NULL),
(11, 'Renault', 'Megane', 2021, 'White', '35 RNT 100', 42000, 'VFXXXXXXXX000008', 'FUEL', 'DIESEL', NULL, NULL),
(12, 'Opel', 'Astra', 2020, 'Gray', '34 OPL 999', 58000, 'WOXXXXXXXX000009', 'FUEL', 'GASOLINE', NULL, NULL),
(13, 'Fiat', 'Egea', 2022, 'White', '34 FIA 500', 35000, 'ZFXXXXXXXX000010', 'FUEL', 'LPG', NULL, NULL),

-- Hybrid Vehicles (Hibrit)
(14, 'Toyota', 'Corolla Hybrid', 2024, 'Silver', '35 GHI 789', 5000, 'JTDKN3DU2A0123456', 'HYBRID', 'GASOLINE', 1.3, 950),
(15, 'Toyota', 'RAV4 Hybrid', 2023, 'Green', '07 RAV 400', 15000, 'JTXXXXXXXX000011', 'HYBRID', 'GASOLINE', 2.5, 900),
(16, 'Honda', 'CR-V Hybrid', 2023, 'Blue', '34 HND 200', 12000, 'JHLXXXXXXX000012', 'HYBRID', 'GASOLINE', 2.0, 880),
(17, 'Lexus', 'ES 300h', 2022, 'Black', '34 LEX 300', 22000, 'JTXXXXXXXX000013', 'HYBRID', 'GASOLINE', 2.5, 920),
(18, 'Hyundai', 'Tucson Hybrid', 2024, 'White', '06 TUC 100', 8000, 'KMXXXXXXXX000014', 'HYBRID', 'GASOLINE', 1.6, 850),

-- Luxury Vehicles
(19, 'Porsche', 'Taycan', 2023, 'Black', '34 POR 911', 10000, 'WPXXXXXXXX000015', 'ELECTRIC', NULL, 93.4, 500),
(20, 'Range Rover', 'Sport', 2022, 'Green', '34 RR 2022', 30000, 'SALXXXXXXX000016', 'FUEL', 'DIESEL', NULL, NULL)
ON CONFLICT (id) DO NOTHING;

SELECT setval('vehicles_id_seq', (SELECT COALESCE(MAX(id), 1) FROM vehicles));

-- =====================================================
-- LISTINGS (20 ilan)
-- =====================================================

INSERT INTO listings (id, vehicle_id, dealer_id, listing_type, publish_date, description, price, trade_in, is_active, daily_rate, min_days, max_days) VALUES
-- Satılık İlanlar (SALE)
(1, 1, 1, 'SALE', CURRENT_DATE - INTERVAL '5 days', 'Like new Tesla Model 3, full autopilot package', 45000.0, true, true, NULL, NULL, NULL),
(2, 2, 1, 'SALE', CURRENT_DATE - INTERVAL '2 days', 'Tesla Model Y, panoramic roof, premium sound', 55000.0, true, true, NULL, NULL, NULL),
(3, 3, 2, 'SALE', CURRENT_DATE - INTERVAL '7 days', 'Audi Q4 e-tron, premium electric SUV, like new', 52000.0, true, true, NULL, NULL, NULL),
(4, 6, 1, 'SALE', CURRENT_DATE - INTERVAL '3 days', 'BMW 320i, excellent condition, full service history', 38000.0, false, true, NULL, NULL, NULL),
(5, 7, 2, 'SALE', CURRENT_DATE - INTERVAL '10 days', 'Mercedes E 200, executive sedan, low mileage', 42000.0, true, true, NULL, NULL, NULL),
(6, 8, 3, 'SALE', CURRENT_DATE - INTERVAL '1 day', 'Audi A4, sporty design, technology package', 35000.0, false, true, NULL, NULL, NULL),
(7, 10, 1, 'SALE', CURRENT_DATE - INTERVAL '15 days', 'Ford Focus, economical, city car', 18000.0, true, false, NULL, NULL, NULL),
(8, 13, 2, 'SALE', CURRENT_DATE - INTERVAL '4 days', 'Fiat Egea, LPG converted, fuel efficient', 22000.0, true, true, NULL, NULL, NULL),
(9, 19, 3, 'SALE', CURRENT_DATE - INTERVAL '1 day', 'Porsche Taycan, luxury electric sports car', 120000.0, false, true, NULL, NULL, NULL),
(10, 20, 1, 'SALE', CURRENT_DATE - INTERVAL '6 days', 'Range Rover Sport, off-road capable luxury', 95000.0, true, true, NULL, NULL, NULL),

-- Kiralık İlanlar (RENTAL)
(11, 14, 1, 'RENTAL', CURRENT_DATE, 'Toyota Corolla Hybrid, daily or weekly rental', NULL, NULL, true, 75.0, 1, 30),
(12, 15, 2, 'RENTAL', CURRENT_DATE - INTERVAL '3 days', 'Toyota RAV4 Hybrid, family SUV rental', NULL, NULL, true, 100.0, 1, 60),
(13, 16, 3, 'RENTAL', CURRENT_DATE - INTERVAL '5 days', 'Honda CR-V Hybrid, comfortable travel', NULL, NULL, true, 95.0, 2, 45),
(14, 4, 1, 'RENTAL', CURRENT_DATE - INTERVAL '8 days', 'BMW iX3, electric SUV for rent', NULL, NULL, true, 150.0, 3, 30),
(15, 5, 2, 'RENTAL', CURRENT_DATE - INTERVAL '2 days', 'Mercedes EQC, luxury electric rental', NULL, NULL, true, 175.0, 3, 60),
(16, 9, 3, 'RENTAL', CURRENT_DATE - INTERVAL '12 days', 'VW Passat, business class rental', NULL, NULL, true, 85.0, 1, 90),
(17, 11, 1, 'RENTAL', CURRENT_DATE - INTERVAL '1 day', 'Renault Megane, economical daily rental', NULL, NULL, true, 50.0, 1, 30),
(18, 12, 2, 'RENTAL', CURRENT_DATE - INTERVAL '4 days', 'Opel Astra, city car rental', NULL, NULL, true, 45.0, 1, 14),
(19, 17, 3, 'RENTAL', CURRENT_DATE, 'Lexus ES 300h, executive rental', NULL, NULL, true, 200.0, 3, 30),
(20, 18, 1, 'RENTAL', CURRENT_DATE - INTERVAL '2 days', 'Hyundai Tucson Hybrid, modern SUV rental', NULL, NULL, true, 90.0, 1, 45)
ON CONFLICT (id) DO NOTHING;

SELECT setval('listings_id_seq', (SELECT COALESCE(MAX(id), 1) FROM listings));

-- =====================================================
-- SALES (10 satış)
-- =====================================================

INSERT INTO sales (id, customer_id, listing_id, date, amount, status) VALUES
(1, 4, 1, CURRENT_DATE - INTERVAL '2 days', 44000.0, 'COMPLETED'),
(2, 5, 4, CURRENT_DATE, 37500.0, 'PENDING'),
(3, 6, 7, CURRENT_DATE - INTERVAL '14 days', 17500.0, 'COMPLETED'),
(4, 12, 5, CURRENT_DATE - INTERVAL '8 days', 41000.0, 'COMPLETED'),
(5, 8, 8, CURRENT_DATE - INTERVAL '3 days', 21500.0, 'PENDING'),
(6, 13, 10, CURRENT_DATE - INTERVAL '5 days', 93000.0, 'COMPLETED'),
(7, 7, 6, CURRENT_DATE - INTERVAL '1 day', 34000.0, 'PENDING'),
(8, 10, 3, CURRENT_DATE - INTERVAL '6 days', 50000.0, 'COMPLETED'),
(9, 9, 2, CURRENT_DATE - INTERVAL '1 day', 53000.0, 'PENDING'),
(10, 11, 9, CURRENT_DATE, 115000.0, 'PENDING')
ON CONFLICT (id) DO NOTHING;

SELECT setval('sales_id_seq', (SELECT COALESCE(MAX(id), 1) FROM sales));

-- =====================================================
-- RENTALS (12 kiralama)
-- =====================================================

INSERT INTO rentals (id, customer_id, listing_id, start_date, end_date, total_cost, status) VALUES
(1, 12, 11, CURRENT_DATE, CURRENT_DATE + INTERVAL '7 days', 525.0, 'ACTIVE'),
(2, 4, 16, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '2 days', 255.0, 'COMPLETED'),
(3, 14, 12, CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE + INTERVAL '4 days', 700.0, 'ACTIVE'),
(4, 15, 14, CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '5 days', 900.0, 'ACTIVE'),
(5, 6, 17, CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE - INTERVAL '5 days', 250.0, 'COMPLETED'),
(6, 13, 15, CURRENT_DATE, CURRENT_DATE + INTERVAL '14 days', 2450.0, 'ACTIVE'),
(7, 7, 18, CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE - INTERVAL '3 days', 180.0, 'COMPLETED'),
(8, 8, 13, CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE + INTERVAL '8 days', 950.0, 'ACTIVE'),
(9, 5, 20, CURRENT_DATE - INTERVAL '4 days', CURRENT_DATE + INTERVAL '3 days', 630.0, 'ACTIVE'),
(10, 9, 19, CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '2 days', 600.0, 'ACTIVE'),
(11, 10, 11, CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '15 days', 375.0, 'COMPLETED'),
(12, 11, 16, CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '10 days', 425.0, 'COMPLETED')
ON CONFLICT (id) DO NOTHING;

SELECT setval('rentals_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rentals));

-- =====================================================
-- SERVICE RECORDS (15 servis kaydı)
-- =====================================================

INSERT INTO service_records (id, vehicle_id, date, description, cost) VALUES
(1, 6, CURRENT_DATE - INTERVAL '1 month', 'Yag degisimi ve filtre degisimi', 250.0),
(2, 7, CURRENT_DATE - INTERVAL '2 weeks', 'Fren balatasi degisimi ve kontrol', 450.0),
(3, 1, CURRENT_DATE - INTERVAL '3 days', 'Batarya saglik kontrolu ve yazilim guncelleme', 150.0),
(4, 9, CURRENT_DATE - INTERVAL '3 months', 'Periyodik bakim 60.000 km', 850.0),
(5, 10, CURRENT_DATE - INTERVAL '2 months', 'Klima gazi dolumu ve kontrol', 180.0),
(6, 14, CURRENT_DATE - INTERVAL '1 week', 'Hibrit sistem kontrolu', 200.0),
(7, 15, CURRENT_DATE - INTERVAL '2 weeks', 'Lastik rotasyon ve balans', 120.0),
(8, 11, CURRENT_DATE - INTERVAL '6 weeks', 'Suspansiyon kontrolu ve onarim', 650.0),
(9, 12, CURRENT_DATE - INTERVAL '1 month', 'Egzoz sistemi tamiri', 380.0),
(10, 2, CURRENT_DATE - INTERVAL '5 days', 'Yazilim guncelleme ve kalibrasyon', 100.0),
(11, 3, CURRENT_DATE - INTERVAL '3 weeks', 'Sarj portu temizligi ve kontrol', 80.0),
(12, 8, CURRENT_DATE - INTERVAL '2 months', 'Triger kayis degisimi', 520.0),
(13, 13, CURRENT_DATE - INTERVAL '1 month', 'LPG sistemi bakim ve ayar', 300.0),
(14, 19, CURRENT_DATE - INTERVAL '1 week', 'Detayli ic dis temizlik', 500.0),
(15, 20, CURRENT_DATE - INTERVAL '10 days', 'Off-road paket kontrolu', 750.0)
ON CONFLICT (id) DO NOTHING;

SELECT setval('service_records_id_seq', (SELECT COALESCE(MAX(id), 1) FROM service_records));

-- =====================================================
-- ADDRESSES (15 adres)
-- =====================================================

INSERT INTO addresses (id, customer_id, city, district, neighborhood, street, building_no, postal_code) VALUES
(1, 4, 'Istanbul', 'Kadikoy', 'Caferaga', 'Moda Cd.', '15', '34710'),
(2, 5, 'Ankara', 'Cankaya', 'Kavaklidere', 'Ataturk Blv.', '42', '06690'),
(3, 6, 'Izmir', 'Karsiyaka', 'Bostanli', 'Cemal Gursel Cd.', '88', '35540'),
(4, 7, 'Istanbul', 'Besiktas', 'Levent', 'Buyukdere Cd.', '201', '34330'),
(5, 8, 'Bursa', 'Nilufer', 'Ozluce', 'Istanbul Cd.', '55', '16110'),
(6, 9, 'Antalya', 'Muratpasa', 'Sirinyal', 'Isiklar Cd.', '33', '07230'),
(7, 10, 'Istanbul', 'Uskudar', 'Altunizade', 'Kisikli Cd.', '77', '34662'),
(8, 11, 'Ankara', 'Yenimahalle', 'Batikent', 'Kardelen Sk.', '12', '06370'),
(9, 12, 'Istanbul', 'Sisli', 'Mecidiyekoy', 'Buyukdere Cd.', '128', '34394'),
(10, 13, 'Istanbul', 'Sariyer', 'Maslak', 'Ahi Evran Cd.', '6', '34398'),
(11, 14, 'Ankara', 'Etimesgut', 'Eryaman', 'Sanayi Blv.', '500', '06820'),
(12, 15, 'Izmir', 'Konak', 'Alsancak', 'Ataturk Cd.', '150', '35220'),
(13, 4, 'Istanbul', 'Bakirkoy', 'Yesilkoy', 'Sahil Yolu', '25', '34149'),
(14, 12, 'Ankara', 'Cankaya', 'Sogutozu', 'Dumlupinar Blv.', '280', '06530'),
(15, 13, 'Izmir', 'Bayrakli', 'Bayrakli', 'Ankara Cd.', '81', '35530')
ON CONFLICT (id) DO NOTHING;

SELECT setval('addresses_id_seq', (SELECT COALESCE(MAX(id), 1) FROM addresses));

-- =====================================================
-- INSURANCES (20 sigorta)
-- =====================================================

INSERT INTO insurances (id, vehicle_id, insurance_type, start_date, end_date) VALUES
(1, 1, 'COMPREHENSIVE', CURRENT_DATE - INTERVAL '6 months', CURRENT_DATE + INTERVAL '6 months'),
(2, 1, 'TRAFFIC', CURRENT_DATE - INTERVAL '6 months', CURRENT_DATE + INTERVAL '6 months'),
(3, 2, 'COMPREHENSIVE', CURRENT_DATE - INTERVAL '2 months', CURRENT_DATE + INTERVAL '10 months'),
(4, 3, 'COMPREHENSIVE', CURRENT_DATE - INTERVAL '4 months', CURRENT_DATE + INTERVAL '8 months'),
(5, 4, 'TRAFFIC', CURRENT_DATE - INTERVAL '3 months', CURRENT_DATE + INTERVAL '9 months'),
(6, 5, 'COMPREHENSIVE', CURRENT_DATE - INTERVAL '5 months', CURRENT_DATE + INTERVAL '7 months'),
(7, 6, 'TRAFFIC', CURRENT_DATE - INTERVAL '1 month', CURRENT_DATE + INTERVAL '11 months'),
(8, 7, 'COMPREHENSIVE', CURRENT_DATE - INTERVAL '8 months', CURRENT_DATE + INTERVAL '4 months'),
(9, 8, 'TRAFFIC', CURRENT_DATE - INTERVAL '2 months', CURRENT_DATE + INTERVAL '10 months'),
(10, 9, 'COMPREHENSIVE', CURRENT_DATE - INTERVAL '7 months', CURRENT_DATE + INTERVAL '5 months'),
(11, 10, 'TRAFFIC', CURRENT_DATE - INTERVAL '4 months', CURRENT_DATE + INTERVAL '8 months'),
(12, 11, 'COMPREHENSIVE', CURRENT_DATE - INTERVAL '3 months', CURRENT_DATE + INTERVAL '9 months'),
(13, 12, 'TRAFFIC', CURRENT_DATE - INTERVAL '6 months', CURRENT_DATE + INTERVAL '6 months'),
(14, 13, 'COMPREHENSIVE', CURRENT_DATE - INTERVAL '1 month', CURRENT_DATE + INTERVAL '11 months'),
(15, 14, 'TRAFFIC', CURRENT_DATE - INTERVAL '5 months', CURRENT_DATE + INTERVAL '7 months'),
(16, 15, 'COMPREHENSIVE', CURRENT_DATE - INTERVAL '2 months', CURRENT_DATE + INTERVAL '10 months'),
(17, 16, 'TRAFFIC', CURRENT_DATE - INTERVAL '4 months', CURRENT_DATE + INTERVAL '8 months'),
(18, 17, 'COMPREHENSIVE', CURRENT_DATE - INTERVAL '3 months', CURRENT_DATE + INTERVAL '9 months'),
(19, 18, 'TRAFFIC', CURRENT_DATE - INTERVAL '1 month', CURRENT_DATE + INTERVAL '11 months'),
(20, 19, 'COMPREHENSIVE', CURRENT_DATE - INTERVAL '2 months', CURRENT_DATE + INTERVAL '10 months')
ON CONFLICT (id) DO NOTHING;

SELECT setval('insurances_id_seq', (SELECT COALESCE(MAX(id), 1) FROM insurances));

-- =====================================================
-- OFFERS (15 teklif)
-- =====================================================

INSERT INTO offers (id, customer_id, listing_id, offer_amount, offer_date, status) VALUES
(1, 4, 2, 52000.0, CURRENT_DATE - INTERVAL '1 day', 'PENDING'),
(2, 5, 1, 42000.0, CURRENT_DATE - INTERVAL '4 days', 'REJECTED'),
(3, 6, 3, 48000.0, CURRENT_DATE - INTERVAL '6 days', 'PENDING'),
(4, 7, 4, 35000.0, CURRENT_DATE - INTERVAL '2 days', 'ACCEPTED'),
(5, 8, 5, 38000.0, CURRENT_DATE - INTERVAL '9 days', 'REJECTED'),
(6, 9, 6, 32000.0, CURRENT_DATE - INTERVAL '1 day', 'PENDING'),
(7, 10, 9, 110000.0, CURRENT_DATE - INTERVAL '1 day', 'PENDING'),
(8, 11, 10, 88000.0, CURRENT_DATE - INTERVAL '5 days', 'REJECTED'),
(9, 12, 2, 53000.0, CURRENT_DATE - INTERVAL '1 day', 'ACCEPTED'),
(10, 13, 9, 115000.0, CURRENT_DATE, 'PENDING'),
(11, 14, 8, 20000.0, CURRENT_DATE - INTERVAL '3 days', 'ACCEPTED'),
(12, 5, 3, 50000.0, CURRENT_DATE - INTERVAL '5 days', 'PENDING'),
(13, 4, 10, 90000.0, CURRENT_DATE - INTERVAL '4 days', 'REJECTED'),
(14, 6, 1, 43500.0, CURRENT_DATE - INTERVAL '3 days', 'ACCEPTED'),
(15, 8, 6, 33000.0, CURRENT_DATE - INTERVAL '1 day', 'PENDING')
ON CONFLICT (id) DO NOTHING;

SELECT setval('offers_id_seq', (SELECT COALESCE(MAX(id), 1) FROM offers));

-- =====================================================
-- PAYMENTS (20 ödeme)
-- =====================================================

INSERT INTO payments (id, rental_id, sale_id, date, amount, payment_type) VALUES
-- Satış ödemeleri
(1, NULL, 1, CURRENT_DATE - INTERVAL '2 days', 44000.0, 'TRANSFER'),
(2, NULL, 3, CURRENT_DATE - INTERVAL '14 days', 17500.0, 'CREDIT_CARD'),
(3, NULL, 4, CURRENT_DATE - INTERVAL '8 days', 20000.0, 'TRANSFER'),
(4, NULL, 4, CURRENT_DATE - INTERVAL '7 days', 21000.0, 'TRANSFER'),
(5, NULL, 6, CURRENT_DATE - INTERVAL '5 days', 93000.0, 'TRANSFER'),
(6, NULL, 8, CURRENT_DATE - INTERVAL '6 days', 25000.0, 'CASH'),
(7, NULL, 8, CURRENT_DATE - INTERVAL '5 days', 25000.0, 'TRANSFER'),
(8, NULL, 2, CURRENT_DATE, 10000.0, 'CASH'),
(9, NULL, 5, CURRENT_DATE - INTERVAL '2 days', 10000.0, 'CREDIT_CARD'),
(10, NULL, 7, CURRENT_DATE - INTERVAL '1 day', 17000.0, 'TRANSFER'),
-- Kiralama ödemeleri
(11, 2, NULL, CURRENT_DATE - INTERVAL '5 days', 255.0, 'CREDIT_CARD'),
(12, 5, NULL, CURRENT_DATE - INTERVAL '10 days', 250.0, 'CASH'),
(13, 7, NULL, CURRENT_DATE - INTERVAL '7 days', 180.0, 'CREDIT_CARD'),
(14, 11, NULL, CURRENT_DATE - INTERVAL '20 days', 375.0, 'TRANSFER'),
(15, 12, NULL, CURRENT_DATE - INTERVAL '15 days', 425.0, 'CREDIT_CARD'),
(16, 1, NULL, CURRENT_DATE, 262.5, 'CREDIT_CARD'),
(17, 3, NULL, CURRENT_DATE - INTERVAL '3 days', 350.0, 'TRANSFER'),
(18, 4, NULL, CURRENT_DATE - INTERVAL '1 day', 450.0, 'CREDIT_CARD'),
(19, 6, NULL, CURRENT_DATE, 1225.0, 'TRANSFER'),
(20, 8, NULL, CURRENT_DATE - INTERVAL '2 days', 475.0, 'CASH')
ON CONFLICT (id) DO NOTHING;

SELECT setval('payments_id_seq', (SELECT COALESCE(MAX(id), 1) FROM payments));

-- =====================================================
-- VERİ KONTROLÜ
-- =====================================================

SELECT '========================================' as info;
SELECT 'VERI OZETI (DATA SUMMARY)' as info;
SELECT '========================================' as info;
SELECT 'Personnel (Personel): ' || COUNT(*) as info FROM personnel;
SELECT 'Customers (Musteriler): ' || COUNT(*) as info FROM customers;
SELECT 'Dealers (Galericiler): ' || COUNT(*) as info FROM dealers;
SELECT 'Vehicles (Araclar): ' || COUNT(*) as info FROM vehicles;
SELECT 'Listings (Ilanlar): ' || COUNT(*) as info FROM listings;
SELECT 'Sales (Satislar): ' || COUNT(*) as info FROM sales;
SELECT 'Rentals (Kiralamalar): ' || COUNT(*) as info FROM rentals;
SELECT 'Service Records (Servis Kayitlari): ' || COUNT(*) as info FROM service_records;
SELECT 'Addresses (Adresler): ' || COUNT(*) as info FROM addresses;
SELECT 'Insurances (Sigortalar): ' || COUNT(*) as info FROM insurances;
SELECT 'Offers (Teklifler): ' || COUNT(*) as info FROM offers;
SELECT 'Payments (Odemeler): ' || COUNT(*) as info FROM payments;
SELECT '========================================' as info;

-- Araç tipi dağılımı
SELECT 'Arac Tipi Dagilimi:' as info;
SELECT vehicle_type, COUNT(*) as adet FROM vehicles GROUP BY vehicle_type ORDER BY adet DESC;

-- İlan tipi dağılımı
SELECT 'Ilan Tipi Dagilimi:' as info;
SELECT listing_type, COUNT(*) as adet FROM listings GROUP BY listing_type;

-- Toplam gelirler
SELECT 'Toplam Satis Geliri: $' || COALESCE(SUM(amount), 0) as info FROM sales WHERE status = 'COMPLETED';
SELECT 'Toplam Kiralama Geliri: $' || COALESCE(SUM(total_cost), 0) as info FROM rentals WHERE status = 'COMPLETED';


-- =====================================================
-- YARDIMCI SORGULAR
-- =====================================================

-- Tüm araçları listele
-- SELECT * FROM vehicles ORDER BY id;

-- Tüm aktif ilanları listele
-- SELECT l.id, v.brand, v.model, l.listing_type, l.price, l.daily_rate 
-- FROM listings l JOIN vehicles v ON l.vehicle_id = v.id 
-- WHERE l.is_active = true ORDER BY l.publish_date DESC;

-- Tüm satışları müşteri bilgisiyle listele
-- SELECT s.id, p.first_name || ' ' || p.last_name as musteri, s.amount, s.status, s.date 
-- FROM sales s JOIN personnel p ON s.customer_id = p.id ORDER BY s.date DESC;

-- Tüm kiralamaları listele
-- SELECT r.id, p.first_name || ' ' || p.last_name as musteri, r.start_date, r.end_date, r.total_cost, r.status 
-- FROM rentals r JOIN personnel p ON r.customer_id = p.id ORDER BY r.start_date DESC;

-- En çok satış yapan araç markaları
-- SELECT v.brand, COUNT(*) as satis_sayisi, SUM(s.amount) as toplam_gelir
-- FROM sales s 
-- JOIN listings l ON s.listing_id = l.id 
-- JOIN vehicles v ON l.vehicle_id = v.id 
-- GROUP BY v.brand ORDER BY satis_sayisi DESC;
