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
    }
}
