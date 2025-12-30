package com.vehiclegallery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "bank_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Personnel owner;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number", unique = true)
    private String accountNumber;

    @Column(columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private Double balance;
}
