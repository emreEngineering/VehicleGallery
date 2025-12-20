package com.vehiclegallery.repository;

import com.vehiclegallery.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNationalId(String nationalId);

    Optional<Customer> findByEmail(String email);

    List<Customer> findByCustomerType(String customerType);
}
