package com.vehiclegallery.service;

import com.vehiclegallery.entity.Sale;
import com.vehiclegallery.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public Optional<Sale> findById(Long id) {
        return saleRepository.findById(id);
    }

    public Sale save(Sale sale) {
        return saleRepository.save(sale);
    }

    public void deleteById(Long id) {
        saleRepository.deleteById(id);
    }

    public List<Sale> findByStatus(String status) {
        return saleRepository.findByStatus(status);
    }

    public long count() {
        return saleRepository.count();
    }

    public double getTotalRevenue() {
        return saleRepository.findAll().stream()
                .mapToDouble(s -> s.getAmount() != null ? s.getAmount() : 0)
                .sum();
    }
}
