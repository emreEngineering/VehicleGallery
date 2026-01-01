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

    public List<Sale> findByCustomerId(Long customerId) {
        return saleRepository.findByCustomerId(customerId);
    }

    public List<Sale> findByDealerId(Long dealerId) {
        // We need to implement this in Repository or filter here.
        // Repository is better. Let's assume we add it to Repo or use Java stream for
        // now if Repo update is harder (Repo update is easy though).
        // Let's check Repo again. It does NOT have dealer filter.
        // Wait, Sale -> Listing -> Dealer.
        // So we need List<Sale> findByListingDealerId(Long dealerId)
        return saleRepository.findByListingDealerId(dealerId);
    }

    public Optional<Sale> findById(Long id) {
        return saleRepository.findById(id);
    }

    @Autowired
    private com.vehiclegallery.repository.BankAccountRepository bankAccountRepository;

    public Sale save(Sale sale) {
        Sale savedSale = saleRepository.save(sale);

        // If sale is completed, transfer money to dealer
        if ("COMPLETED".equals(savedSale.getStatus())) {
            Long dealerId = savedSale.getListing().getDealer().getId();
            List<com.vehiclegallery.entity.BankAccount> accounts = bankAccountRepository.findByOwnerId(dealerId);

            if (!accounts.isEmpty()) {
                // Transfer to the first account found
                var account = accounts.get(0);
                account.setBalance(account.getBalance() + savedSale.getAmount());
                bankAccountRepository.save(account);
            }
        }

        return savedSale;
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
