package com.vehiclegallery.repository;

import com.vehiclegallery.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByBrand(String brand);

    List<Vehicle> findByVehicleType(String vehicleType);

    Optional<Vehicle> findByPlateNumber(String plateNumber);

    List<Vehicle> findByYearBetween(Integer startYear, Integer endYear);
}
