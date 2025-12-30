package com.vehiclegallery.repository;

import com.vehiclegallery.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByOwnerId(Long ownerId);

    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
