package com.vehiclegallery.service;

import com.vehiclegallery.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataResetService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void resetTransactions() {
        // Disable constraints temporarily to avoid ordering issues, or delete in
        // correct order
        // Correct order: Payments -> Sales/Rentals/Offers

        // Payments references Sales and Rentals
        jdbcTemplate.execute("TRUNCATE TABLE payments CASCADE");

        // Sales/Rentals/Offers reference Listings/Customers
        jdbcTemplate.execute("TRUNCATE TABLE sales CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE rentals CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE offers CASCADE");

        // Audit log
        jdbcTemplate.execute("TRUNCATE TABLE audit_log CASCADE");

        System.out.println("Transaction data reset completed.");
    }

    @Transactional
    public void resetAll() {
        // Delete everything including Master Data
        resetTransactions();

        jdbcTemplate.execute("TRUNCATE TABLE service_records CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE insurances CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE listings CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE vehicles CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE bank_accounts CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE personnel_addresses CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE addresses CASCADE");

        // Customers/Dealers are in hierarchy
        jdbcTemplate.execute("TRUNCATE TABLE corporate_customers CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE individual_customers CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE customers CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE dealers CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE personnel CASCADE");

        System.out.println("Full database reset completed.");
        seedData();
    }

    public void addSampleVehicles() {
        // Sample vehicles requested by user
        String[][] samples = {
                { "Fiat", "Egea", "2023", "White", "FUEL", "GASOLINE" },
                { "Renault", "Clio", "2023", "Red", "FUEL", "GASOLINE" },
                { "Toyota", "Corolla", "2023", "Silver", "HYBRID", null },
                { "Honda", "Civic", "2022", "Blue", "FUEL", "GASOLINE" },
                { "Volkswagen", "Passat", "2022", "Black", "FUEL", "DIESEL" },
                { "Ford", "Focus", "2023", "Grey", "FUEL", "DIESEL" },
                { "Hyundai", "i20", "2024", "Blue", "FUEL", "GASOLINE" },
                { "Peugeot", "3008", "2023", "White", "FUEL", "DIESEL" },
                { "Nissan", "Qashqai", "2023", "Black", "HYBRID", null },
                { "BMW", "520i", "2024", "Black", "FUEL", "GASOLINE" }
        };

        for (String[] data : samples) {
            try {
                // Determine IDs assuming serial or random won't clash often or strictly relies
                // on sequence
                // For simplicity, we just insert.
                // We use raw SQL for speed and to avoid Entity dependencies if desired,
                // but since we are in Spring, let's use Entity or SQL.
                // Using SQL to ensure inheritance tables are filled if needed.
                // Note: The user's system likely uses 'vehicles' table for main data.

                String brand = data[0];
                String model = data[1];
                int year = Integer.parseInt(data[2]);
                String color = data[3];
                String type = data[4];
                String fuel = data[5];

                // Generate a random plate to avoid unique constraint error
                String plate = "34 " + brand.substring(0, 3).toUpperCase() + " " + (100 + (int) (Math.random() * 900));

                // Insert into vehicles
                String sql = "INSERT INTO vehicles (brand, model, production_year, color, plate_number, vehicle_type, mileage) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, 0) RETURNING id";

                Long vehicleId = jdbcTemplate.queryForObject(sql, Long.class, brand, model, year, color, plate, type);

                if ("FUEL".equals(type)) {
                    jdbcTemplate.update("INSERT INTO fuel_vehicles (vehicle_id, fuel_type) VALUES (?, ?)", vehicleId,
                            fuel);
                } else if ("HYBRID".equals(type)) {
                    // Assuming hybrid table or electric table structure?
                    // database_init says hybrid is just vehicle_type='HYBRID' but no specific
                    // sub-table?
                    // Wait, Step 83 shows NO hybrid_vehicles table. Fuel and Electric only.
                    // But 'HYBRID' is a valid vehicle_type.
                    // So we do nothing extra for hybrid if no table exists, implicit inheritance or
                    // single table?
                    // Actually, let's check if there is a hybrid table.
                    // Step 83: NO hybrid_vehicles table definition found.
                    // So Hybrids might be stored as FUEL if they burn fuel, or just base vehicles.
                    // Let's assume just base vehicles for now or check if they need to be in
                    // fuel_vehicles?
                    // Sample data (lines 1196+) inserts HYBRID vehicles but DOES NOT insert into
                    // any sub-table in the script!
                    // So Hybrids stay in `vehicles` table only.
                }

            } catch (Exception e) {
                System.err.println("Failed to insert " + data[0] + ": " + e.getMessage());
            }
        }
        System.out.println("Sample vehicles added.");
    }

    private void seedData() {
        try {
            org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource(
                    "database_init.sql");
            if (resource.exists()) {
                org.springframework.jdbc.datasource.init.ResourceDatabasePopulator databasePopulator = new org.springframework.jdbc.datasource.init.ResourceDatabasePopulator(
                        resource);
                databasePopulator.execute(jdbcTemplate.getDataSource());
                System.out.println("Data seeding completed successfully.");
            } else {
                System.err.println("database_init.sql not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error seeding data: " + e.getMessage());
        }
    }
}
