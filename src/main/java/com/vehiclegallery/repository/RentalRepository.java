package com.vehiclegallery.repository;

import com.vehiclegallery.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByCustomerId(Long customerId);

    List<Rental> findByStatus(String status);

    // Kiralama tarih çakışması kontrolü
    @Query("SELECT r FROM Rental r WHERE r.listing.vehicle.id = :vehicleId " +
            "AND r.status IN ('ACTIVE', 'PENDING') " +
            "AND ((r.startDate BETWEEN :startDate AND :endDate) " +
            "OR (r.endDate BETWEEN :startDate AND :endDate) " +
            "OR (r.startDate <= :startDate AND r.endDate >= :endDate))")
    List<Rental> findConflictingRentals(@Param("vehicleId") Long vehicleId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Belirli bir araç için aktif kiralamalar
    @Query("SELECT r FROM Rental r WHERE r.listing.vehicle.id = :vehicleId AND r.status = 'ACTIVE'")
    List<Rental> findActiveByVehicleId(@Param("vehicleId") Long vehicleId);
}
