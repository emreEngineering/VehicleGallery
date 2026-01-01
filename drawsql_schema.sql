-- =====================================================
-- ARAÇ GALERİSİ YÖNETİM SİSTEMİ - DrawSQL ER Diyagramı
-- Vehicle Gallery Management System - ER Diagram
-- https://drawsql.app için optimize edilmiş SQL
-- =====================================================

-- 1. Personnel (Personel - Ana Tablo)
CREATE TABLE personnel (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

-- 2. Customers (Müşteriler)
CREATE TABLE customers (
    id BIGINT PRIMARY KEY REFERENCES personnel(id),
    phone VARCHAR(255),
    customer_type VARCHAR(50) NOT NULL
);

-- 3. Individual Customers (Bireysel Müşteriler)
CREATE TABLE individual_customers (
    customer_id BIGINT PRIMARY KEY REFERENCES customers(id),
    national_id VARCHAR(255) UNIQUE,
    birth_date VARCHAR(255)
);

-- 4. Corporate Customers (Kurumsal Müşteriler)
CREATE TABLE corporate_customers (
    customer_id BIGINT PRIMARY KEY REFERENCES customers(id),
    company_name VARCHAR(255),
    tax_number VARCHAR(255)
);

-- 5. Dealers (Galericiler)
CREATE TABLE dealers (
    id BIGINT PRIMARY KEY REFERENCES personnel(id),
    company_name VARCHAR(255),
    tax_number VARCHAR(255) UNIQUE
);

-- 6. Vehicles (Araçlar)
CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    production_year INTEGER,
    color VARCHAR(255),
    plate_number VARCHAR(255) UNIQUE,
    mileage INTEGER,
    chassis_number VARCHAR(255) UNIQUE,
    vehicle_type VARCHAR(50) NOT NULL
);

-- 7. Fuel Vehicles (Yakıtlı Araçlar)
CREATE TABLE fuel_vehicles (
    vehicle_id BIGINT PRIMARY KEY REFERENCES vehicles(id),
    fuel_type VARCHAR(50)
);

-- 8. Electric Vehicles (Elektrikli Araçlar)
CREATE TABLE electric_vehicles (
    vehicle_id BIGINT PRIMARY KEY REFERENCES vehicles(id),
    battery_capacity DOUBLE PRECISION,
    range_km INTEGER
);

-- 9. Listings (İlanlar)
CREATE TABLE listings (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
    dealer_id BIGINT NOT NULL REFERENCES personnel(id),
    publish_date DATE,
    description TEXT,
    listing_type VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    price DOUBLE PRECISION,
    trade_in BOOLEAN,
    daily_rate DOUBLE PRECISION,
    min_days INTEGER,
    max_days INTEGER
);

-- 10. Sales (Satışlar)
CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES personnel(id),
    listing_id BIGINT NOT NULL REFERENCES listings(id),
    date DATE,
    amount DOUBLE PRECISION,
    status VARCHAR(50)
);

-- 11. Rentals (Kiralamalar)
CREATE TABLE rentals (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES personnel(id),
    listing_id BIGINT NOT NULL REFERENCES listings(id),
    start_date DATE,
    end_date DATE,
    total_cost DOUBLE PRECISION,
    status VARCHAR(50)
);

-- 12. Personnel Addresses (Adresler)
CREATE TABLE personnel_addresses (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES personnel(id),
    city VARCHAR(255),
    district VARCHAR(255),
    neighborhood VARCHAR(255),
    street VARCHAR(255),
    building_no VARCHAR(50),
    postal_code VARCHAR(20)
);

-- 13. Payments (Ödemeler)
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    rental_id BIGINT REFERENCES rentals(id),
    sale_id BIGINT REFERENCES sales(id),
    date DATE,
    amount DOUBLE PRECISION,
    payment_type VARCHAR(50)
);

-- 14. Offers (Teklifler)
CREATE TABLE offers (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES personnel(id),
    listing_id BIGINT NOT NULL REFERENCES listings(id),
    offer_amount DOUBLE PRECISION,
    offer_date DATE,
    status VARCHAR(50)
);

-- 15. Service Records (Servis Kayıtları)
CREATE TABLE service_records (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
    date DATE,
    description TEXT,
    cost DOUBLE PRECISION
);

-- 16. Insurances (Sigortalar)
CREATE TABLE insurances (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
    insurance_type VARCHAR(50),
    start_date DATE,
    end_date DATE
);

-- 17. Bank Accounts (Banka Hesapları)
CREATE TABLE bank_accounts (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL REFERENCES personnel(id),
    bank_name VARCHAR(100),
    account_number VARCHAR(100) UNIQUE,
    balance DOUBLE PRECISION DEFAULT 0
);

-- 18. Notifications (Bildirimler)
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES personnel(id),
    title VARCHAR(255),
    message TEXT,
    type VARCHAR(50),
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP,
    related_sale_id BIGINT REFERENCES sales(id),
    related_rental_id BIGINT REFERENCES rentals(id)
);

-- 19. Audit Log (Denetim Günlüğü)
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    operation VARCHAR(20) NOT NULL,
    record_id BIGINT,
    old_data JSONB,
    new_data JSONB,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(255)
);
