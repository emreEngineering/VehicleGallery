package com.vehiclegallery.service;

import com.vehiclegallery.entity.Rental;
import com.vehiclegallery.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;

    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }

    public Optional<Rental> findById(Long id) {
        return rentalRepository.findById(id);
    }

    public Rental save(Rental rental) {
        return rentalRepository.save(rental);
    }

    public void deleteById(Long id) {
        rentalRepository.deleteById(id);
    }

    public List<Rental> findByStatus(String status) {
        return rentalRepository.findByStatus(status);
    }

    public long count() {
        return rentalRepository.count();
    }

    /**
     * Belirtilen araç ve tarih aralığında çakışan kiralama var mı kontrol eder
     */
    public boolean hasConflictingRentals(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        List<Rental> conflicts = rentalRepository.findConflictingRentals(vehicleId, startDate, endDate);
        return !conflicts.isEmpty();
    }

    /**
     * Belirli bir araç için aktif kiralamalar
     */
    public List<Rental> findActiveByVehicleId(Long vehicleId) {
        return rentalRepository.findActiveByVehicleId(vehicleId);
    }
}
