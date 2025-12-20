package com.vehiclegallery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    private LocalDate publishDate;

    private String description;

    @Column(nullable = false)
    private String listingType; // "SALE" or "RENTAL"

    private Boolean isActive = true;

    // For Sale Listings
    private Double price;

    private Boolean tradeIn;

    // For Rental Listings
    private Double dailyRate;

    private Integer minDays;

    private Integer maxDays;

    @OneToMany(mappedBy = "listing")
    private List<Offer> offers;

    @OneToMany(mappedBy = "listing")
    private List<Rental> rentals;

    @OneToMany(mappedBy = "listing")
    private List<Sale> sales;
}
