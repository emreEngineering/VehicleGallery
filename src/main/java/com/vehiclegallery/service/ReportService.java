package com.vehiclegallery.service;

import com.vehiclegallery.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

/**
 * Raporlama Servisi
 * Satış, kiralama, araç ve müşteri raporları oluşturur
 */
@Service
public class ReportService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ListingRepository listingRepository;

    /**
     * Satış Özet Raporu
     */
    public Map<String, Object> getSalesSummaryReport() {
        Map<String, Object> report = new LinkedHashMap<>();

        // Toplam satış sayıları (durum bazlı)
        report.put("completedCount", saleRepository.countByStatus("COMPLETED"));
        report.put("pendingCount", saleRepository.countByStatus("PENDING"));
        report.put("cancelledCount", saleRepository.countByStatus("CANCELLED"));

        // Toplam tutarlar
        report.put("completedTotal", saleRepository.sumAmountByStatus("COMPLETED"));
        report.put("pendingTotal", saleRepository.sumAmountByStatus("PENDING"));

        // Ortalamalar
        report.put("avgCompleted", saleRepository.avgAmountByStatus("COMPLETED"));
        report.put("avgPending", saleRepository.avgAmountByStatus("PENDING"));

        // Min/Max
        report.put("minSale", saleRepository.minAmountByStatus("COMPLETED"));
        report.put("maxSale", saleRepository.maxAmountByStatus("COMPLETED"));

        return report;
    }

    /**
     * Aylık Satış Raporu
     */
    public Map<String, Object> getMonthlySalesReport(int year, int month) {
        Map<String, Object> report = new LinkedHashMap<>();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        report.put("year", year);
        report.put("month", month);
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalAmount", saleRepository.sumAmountByDateRange(startDate, endDate));

        return report;
    }

    /**
     * Araç Envanteri Raporu
     */
    public Map<String, Object> getVehicleInventoryReport() {
        Map<String, Object> report = new LinkedHashMap<>();

        var allVehicles = vehicleRepository.findAll();

        // Toplam araç sayısı
        report.put("totalVehicles", (long) allVehicles.size());

        // Araç tipi bazlı dağılım
        long electricCount = allVehicles.stream()
                .filter(v -> "ELECTRIC".equals(v.getVehicleType())).count();
        long fuelCount = allVehicles.stream()
                .filter(v -> "FUEL".equals(v.getVehicleType())).count();
        long hybridCount = allVehicles.stream()
                .filter(v -> "HYBRID".equals(v.getVehicleType())).count();

        report.put("electricCount", electricCount);
        report.put("fuelCount", fuelCount);
        report.put("hybridCount", hybridCount);

        // Marka bazlı dağılım
        Map<String, Long> brandCounts = new LinkedHashMap<>();
        allVehicles.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        v -> v.getBrand(),
                        java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> brandCounts.put(e.getKey(), e.getValue()));
        report.put("brandDistribution", brandCounts);

        // Ortalama kilometre
        double avgMileage = allVehicles.stream()
                .filter(v -> v.getMileage() != null)
                .mapToInt(v -> v.getMileage())
                .average().orElse(0);
        report.put("avgMileage", avgMileage);

        return report;
    }

    /**
     * İlan Durumu Raporu
     */
    public Map<String, Object> getListingStatusReport() {
        Map<String, Object> report = new LinkedHashMap<>();

        var allListings = listingRepository.findAll();
        var activeListings = listingRepository.findByIsActiveTrue();

        report.put("totalListings", (long) allListings.size());
        report.put("activeListings", (long) activeListings.size());
        report.put("inactiveListings", (long) (allListings.size() - activeListings.size()));

        // Tip bazlı dağılım
        long saleListings = allListings.stream()
                .filter(l -> "SALE".equals(l.getListingType())).count();
        long rentalListings = allListings.stream()
                .filter(l -> "RENTAL".equals(l.getListingType())).count();

        report.put("saleListings", saleListings);
        report.put("rentalListings", rentalListings);

        // Ortalama fiyatlar
        double avgSalePrice = allListings.stream()
                .filter(l -> "SALE".equals(l.getListingType()) && l.getPrice() != null)
                .mapToDouble(l -> l.getPrice())
                .average().orElse(0);

        double avgDailyRate = allListings.stream()
                .filter(l -> "RENTAL".equals(l.getListingType()) && l.getDailyRate() != null)
                .mapToDouble(l -> l.getDailyRate())
                .average().orElse(0);

        report.put("avgSalePrice", avgSalePrice);
        report.put("avgDailyRate", avgDailyRate);

        return report;
    }

    /**
     * Müşteri Analiz Raporu
     */
    public Map<String, Object> getCustomerAnalysisReport() {
        Map<String, Object> report = new LinkedHashMap<>();

        var allCustomers = customerRepository.findAll();

        report.put("totalCustomers", (long) allCustomers.size());

        // Tip bazlı dağılım
        long individualCount = allCustomers.stream()
                .filter(c -> "INDIVIDUAL".equals(c.getCustomerType())).count();
        long corporateCount = allCustomers.stream()
                .filter(c -> "CORPORATE".equals(c.getCustomerType())).count();

        report.put("individualCustomers", individualCount);
        report.put("corporateCustomers", corporateCount);

        return report;
    }

    /**
     * Kiralama Raporu
     */
    public Map<String, Object> getRentalReport() {
        Map<String, Object> report = new LinkedHashMap<>();

        var allRentals = rentalRepository.findAll();

        report.put("totalRentals", (long) allRentals.size());

        // Durum bazlı
        long activeRentals = allRentals.stream()
                .filter(r -> "ACTIVE".equals(r.getStatus())).count();
        long completedRentals = allRentals.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus())).count();

        report.put("activeRentals", activeRentals);
        report.put("completedRentals", completedRentals);

        // Toplam gelir
        double totalRentalRevenue = allRentals.stream()
                .filter(r -> r.getTotalCost() != null)
                .mapToDouble(r -> r.getTotalCost())
                .sum();
        report.put("totalRentalRevenue", totalRentalRevenue);

        // Ortalama kiralama süresi
        double avgDuration = allRentals.stream()
                .filter(r -> r.getStartDate() != null && r.getEndDate() != null)
                .mapToLong(r -> java.time.temporal.ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate()))
                .average().orElse(0);
        report.put("avgRentalDuration", avgDuration);

        return report;
    }
}
