package com.vehiclegallery.service;

import com.vehiclegallery.entity.Vehicle;
import com.vehiclegallery.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void deleteById(Long id) {
        vehicleRepository.deleteById(id);
    }

    public List<Vehicle> findByBrand(String brand) {
        return vehicleRepository.findByBrand(brand);
    }

    public List<Vehicle> findByVehicleType(String vehicleType) {
        return vehicleRepository.findByVehicleType(vehicleType);
    }

    public long count() {
        return vehicleRepository.count();
    }
}
