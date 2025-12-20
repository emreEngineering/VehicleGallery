package com.vehiclegallery.repository;

import com.vehiclegallery.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRentalId(Long rentalId);

    List<Payment> findBySaleId(Long saleId);
}
