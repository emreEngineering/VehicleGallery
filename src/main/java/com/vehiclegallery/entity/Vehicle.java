package com.vehiclegallery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(name = "production_year")
    private Integer year;

    private String color;

    @Column(unique = true)
    private String plateNumber;

    private Integer mileage;

    @Column(unique = true)
    private String chassisNumber;

    @Column(nullable = false)
    private String vehicleType; // "FUEL", "ELECTRIC", "HYBRID"

    // For Fuel Vehicles
    private String fuelType; // "GASOLINE", "DIESEL", "LPG"

    // For Electric Vehicles
    private Double batteryCapacity;

    private Integer rangeKm;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<ServiceRecord> serviceRecords;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<Insurance> insurances;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<Listing> listings;
}
