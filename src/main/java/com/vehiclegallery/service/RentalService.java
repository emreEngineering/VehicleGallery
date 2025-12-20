package com.vehiclegallery.service;

import com.vehiclegallery.entity.Rental;
import com.vehiclegallery.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

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
}
