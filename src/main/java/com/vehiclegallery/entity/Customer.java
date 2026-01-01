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
@SecondaryTables({
        @SecondaryTable(name = "individual_customers", pkJoinColumns = @PrimaryKeyJoinColumn(name = "customer_id")),
        @SecondaryTable(name = "corporate_customers", pkJoinColumns = @PrimaryKeyJoinColumn(name = "customer_id"))
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends Personnel {

    @Column(unique = true, table = "individual_customers")
    @Pattern(regexp = "^[0-9]{11}$", message = "TC Kimlik numarası 11 haneli olmalıdır")
    private String nationalId;

    @Pattern(regexp = "^\\+?[0-9\\s-]{10,15}$", message = "Geçersiz telefon numarası formatı")
    private String phone; // This is in the base "customers" table

    @Column(nullable = false)
    @NotBlank(message = "Müşteri tipi boş olamaz")
    private String customerType; // "INDIVIDUAL" or "CORPORATE" in "customers" table

    // For Individual Customers
    @Column(table = "individual_customers")
    private String birthDate;

    // For Corporate Customers
    @Column(table = "corporate_customers")
    private String companyName;

    @Column(table = "corporate_customers")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Vergi numarası 10-11 haneli olmalıdır")
    private String taxNumber;

    @OneToMany(mappedBy = "customer")
    private List<Rental> rentals;

    @OneToMany(mappedBy = "customer")
    private List<Sale> sales;

    @OneToMany(mappedBy = "customer")
    private List<Offer> offers;
}
