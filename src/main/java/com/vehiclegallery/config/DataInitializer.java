package com.vehiclegallery.config;

import com.vehiclegallery.entity.*;
import com.vehiclegallery.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private ServiceRecordRepository serviceRecordRepository;

    @Override
    public void run(String... args) {
        // Veritabanında zaten veri varsa tekrar ekleme
        if (vehicleRepository.count() > 0) {
            System.out.println("ℹ️ Database already contains data, skipping initialization.");
            return;
        }

        // Sample Vehicles
        Vehicle v1 = new Vehicle();
        v1.setBrand("Tesla");
        v1.setModel("Model 3");
        v1.setYear(2023);
        v1.setColor("White");
        v1.setPlateNumber("34 ABC 123");
        v1.setMileage(15000);
        v1.setChassisNumber("5YJ3E1EA5NF000001");
        v1.setVehicleType("ELECTRIC");
        v1.setBatteryCapacity(82.0);
        v1.setRangeKm(580);
        vehicleRepository.save(v1);

        Vehicle v2 = new Vehicle();
        v2.setBrand("BMW");
        v2.setModel("320i");
        v2.setYear(2022);
        v2.setColor("Black");
        v2.setPlateNumber("06 DEF 456");
        v2.setMileage(32000);
        v2.setChassisNumber("WBA5R1C50NAJ12345");
        v2.setVehicleType("FUEL");
        v2.setFuelType("GASOLINE");
        vehicleRepository.save(v2);

        Vehicle v3 = new Vehicle();
        v3.setBrand("Toyota");
        v3.setModel("Corolla Hybrid");
        v3.setYear(2024);
        v3.setColor("Silver");
        v3.setPlateNumber("35 GHI 789");
        v3.setMileage(5000);
        v3.setChassisNumber("JTDKN3DU2A0123456");
        v3.setVehicleType("HYBRID");
        v3.setFuelType("GASOLINE");
        v3.setBatteryCapacity(1.3);
        vehicleRepository.save(v3);

        Vehicle v4 = new Vehicle();
        v4.setBrand("Mercedes");
        v4.setModel("E 200");
        v4.setYear(2021);
        v4.setColor("Gray");
        v4.setPlateNumber("34 JKL 012");
        v4.setMileage(48000);
        v4.setChassisNumber("WDD2130401A123456");
        v4.setVehicleType("FUEL");
        v4.setFuelType("DIESEL");
        vehicleRepository.save(v4);

        Vehicle v5 = new Vehicle();
        v5.setBrand("Audi");
        v5.setModel("Q4 e-tron");
        v5.setYear(2023);
        v5.setColor("Blue");
        v5.setPlateNumber("07 MNO 345");
        v5.setMileage(12000);
        v5.setChassisNumber("WAUZZZF48NA012345");
        v5.setVehicleType("ELECTRIC");
        v5.setBatteryCapacity(77.0);
        v5.setRangeKm(520);
        vehicleRepository.save(v5);

        // Sample Customers
        Customer c1 = new Customer();
        c1.setFirstName("Ahmet");
        c1.setLastName("Yilmaz");
        c1.setNationalId("12345678901");
        c1.setEmail("ahmet@email.com");
        c1.setPassword("123456");
        c1.setPhone("+90 532 123 4567");
        c1.setCustomerType("INDIVIDUAL");
        c1.setBirthDate("1985-03-15");
        customerRepository.save(c1);

        Customer c2 = new Customer();
        c2.setFirstName("Tech");
        c2.setLastName("Corp");
        c2.setEmail("fleet@techcorp.com");
        c2.setPassword("123456");
        c2.setPhone("+90 212 555 1234");
        c2.setCustomerType("CORPORATE");
        c2.setCompanyName("Tech Corp Ltd.");
        c2.setTaxNumber("1234567890");
        customerRepository.save(c2);

        Customer c3 = new Customer();
        c3.setFirstName("Mehmet");
        c3.setLastName("Demir");
        c3.setNationalId("98765432109");
        c3.setEmail("mehmet@email.com");
        c3.setPhone("+90 542 987 6543");
        c3.setCustomerType("INDIVIDUAL");
        c3.setBirthDate("1990-07-22");
        customerRepository.save(c3);

        // Sample Dealer
        Dealer d1 = new Dealer();
        d1.setFirstName("Ali");
        d1.setLastName("Ozturk");
        d1.setEmail("ali@galeri.com");
        d1.setPassword("123456");
        d1.setTitle("Sales Manager");
        d1.setCompanyName("Premium Auto Gallery");
        d1.setTaxNumber("9876543210");
        dealerRepository.save(d1);

        // Sample Listings
        Listing l1 = new Listing();
        l1.setVehicle(v1);
        l1.setDealer(d1);
        l1.setListingType("SALE");
        l1.setPublishDate(LocalDate.now().minusDays(5));
        l1.setDescription("Like new Tesla Model 3, full autopilot package");
        l1.setPrice(45000.0);
        l1.setTradeIn(true);
        l1.setIsActive(true);
        listingRepository.save(l1);

        Listing l2 = new Listing();
        l2.setVehicle(v2);
        l2.setDealer(d1);
        l2.setListingType("SALE");
        l2.setPublishDate(LocalDate.now().minusDays(3));
        l2.setDescription("BMW 320i, excellent condition, full service history");
        l2.setPrice(38000.0);
        l2.setTradeIn(false);
        l2.setIsActive(true);
        listingRepository.save(l2);

        Listing l3 = new Listing();
        l3.setVehicle(v3);
        l3.setDealer(d1);
        l3.setListingType("RENTAL");
        l3.setPublishDate(LocalDate.now());
        l3.setDescription("New Toyota Corolla Hybrid for daily or weekly rental");
        l3.setDailyRate(75.0);
        l3.setMinDays(1);
        l3.setMaxDays(30);
        l3.setIsActive(true);
        listingRepository.save(l3);

        Listing l4 = new Listing();
        l4.setVehicle(v4);
        l4.setDealer(d1);
        l4.setListingType("RENTAL");
        l4.setPublishDate(LocalDate.now().minusDays(10));
        l4.setDescription("Mercedes E-Class for executive rental");
        l4.setDailyRate(150.0);
        l4.setMinDays(3);
        l4.setMaxDays(90);
        l4.setIsActive(true);
        listingRepository.save(l4);

        Listing l5 = new Listing();
        l5.setVehicle(v5);
        l5.setDealer(d1);
        l5.setListingType("SALE");
        l5.setPublishDate(LocalDate.now().minusDays(1));
        l5.setDescription("Audi Q4 e-tron, premium electric SUV");
        l5.setPrice(52000.0);
        l5.setTradeIn(true);
        l5.setIsActive(true);
        listingRepository.save(l5);

        // Sample Sales
        Sale sale1 = new Sale();
        sale1.setCustomer(c1);
        sale1.setListing(l1);
        sale1.setDate(LocalDate.now().minusDays(2));
        sale1.setAmount(44000.0);
        sale1.setStatus("COMPLETED");
        saleRepository.save(sale1);

        Sale sale2 = new Sale();
        sale2.setCustomer(c3);
        sale2.setListing(l2);
        sale2.setDate(LocalDate.now());
        sale2.setAmount(37500.0);
        sale2.setStatus("PENDING");
        saleRepository.save(sale2);

        // Sample Rentals
        Rental rental1 = new Rental();
        rental1.setCustomer(c2);
        rental1.setListing(l3);
        rental1.setStartDate(LocalDate.now());
        rental1.setEndDate(LocalDate.now().plusDays(7));
        rental1.setTotalCost(525.0);
        rental1.setStatus("ACTIVE");
        rentalRepository.save(rental1);

        Rental rental2 = new Rental();
        rental2.setCustomer(c1);
        rental2.setListing(l4);
        rental2.setStartDate(LocalDate.now().minusDays(5));
        rental2.setEndDate(LocalDate.now().minusDays(2));
        rental2.setTotalCost(450.0);
        rental2.setStatus("COMPLETED");
        rentalRepository.save(rental2);

        // Sample Service Records
        ServiceRecord sr1 = new ServiceRecord();
        sr1.setVehicle(v2);
        sr1.setDate(LocalDate.now().minusMonths(1));
        sr1.setDescription("Oil change and filter replacement");
        sr1.setCost(250.0);
        serviceRecordRepository.save(sr1);

        ServiceRecord sr2 = new ServiceRecord();
        sr2.setVehicle(v4);
        sr2.setDate(LocalDate.now().minusWeeks(2));
        sr2.setDescription("Brake pad replacement and inspection");
        sr2.setCost(450.0);
        serviceRecordRepository.save(sr2);

        ServiceRecord sr3 = new ServiceRecord();
        sr3.setVehicle(v1);
        sr3.setDate(LocalDate.now().minusDays(3));
        sr3.setDescription("Battery health check and software update");
        sr3.setCost(150.0);
        serviceRecordRepository.save(sr3);

        System.out.println("✅ Sample data loaded successfully!");
    }
}
