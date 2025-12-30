package com.vehiclegallery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @Pattern(regexp = "^[0-9]{11}$", message = "TC Kimlik numarası 11 haneli olmalıdır")
    private String nationalId;

    @Pattern(regexp = "^\\+?[0-9\\s-]{10,15}$", message = "Geçersiz telefon numarası formatı")
    private String phone;

    @Column(nullable = false)
    @NotBlank(message = "Müşteri tipi boş olamaz")
    private String customerType; // "INDIVIDUAL" or "CORPORATE"

    // For Individual Customers
    private String birthDate;

    // For Corporate Customers
    private String companyName;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "Vergi numarası 10-11 haneli olmalıdır")
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
