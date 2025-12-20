package com.vehiclegallery.repository;

import com.vehiclegallery.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {
    Optional<Dealer> findByTaxNumber(String taxNumber);

    Optional<Dealer> findByEmail(String email);
}
