package com.vehiclegallery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends Personnel {

    @Column(unique = true)
    private String nationalId;

    private String phone;

    @Column(nullable = false)
    private String customerType; // "INDIVIDUAL" or "CORPORATE"

    // For Individual Customers
    private String birthDate;

    // For Corporate Customers
    private String companyName;

    private String taxNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @OneToMany(mappedBy = "customer")
    private List<Rental> rentals;

    @OneToMany(mappedBy = "customer")
    private List<Sale> sales;

    @OneToMany(mappedBy = "customer")
    private List<Offer> offers;
}
