package com.vehiclegallery.controller;

import com.vehiclegallery.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

/**
 * Raporlama Controller
 * Web tabanlı raporlama sayfalarını yönetir
 */
@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Rapor Ana Sayfası - Tüm raporların özeti
     */
    @GetMapping
    public String reportDashboard(Model model) {
        model.addAttribute("salesSummary", reportService.getSalesSummaryReport());
        model.addAttribute("vehicleInventory", reportService.getVehicleInventoryReport());
        model.addAttribute("listingStatus", reportService.getListingStatusReport());
        model.addAttribute("customerAnalysis", reportService.getCustomerAnalysisReport());
        model.addAttribute("rentalReport", reportService.getRentalReport());
        return "reports/dashboard";
    }

    /**
     * Satış Raporu Detay
     */
    @GetMapping("/sales")
    public String salesReport(Model model) {
        model.addAttribute("report", reportService.getSalesSummaryReport());
        return "reports/sales";
    }

    /**
     * Aylık Satış Raporu
     */
    @GetMapping("/sales/monthly")
    public String monthlySalesReport(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month,
            Model model) {

        if (year == 0)
            year = LocalDate.now().getYear();
        if (month == 0)
            month = LocalDate.now().getMonthValue();

        model.addAttribute("report", reportService.getMonthlySalesReport(year, month));
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);
        return "reports/monthly-sales";
    }

    /**
     * Araç Envanteri Raporu
     */
    @GetMapping("/vehicles")
    public String vehicleReport(Model model) {
        model.addAttribute("report", reportService.getVehicleInventoryReport());
        return "reports/vehicles";
    }

    /**
     * İlan Durumu Raporu
     */
    @GetMapping("/listings")
    public String listingReport(Model model) {
        model.addAttribute("report", reportService.getListingStatusReport());
        return "reports/listings";
    }

    /**
     * Müşteri Analiz Raporu
     */
    @GetMapping("/customers")
    public String customerReport(Model model) {
        model.addAttribute("report", reportService.getCustomerAnalysisReport());
        return "reports/customers";
    }

    /**
     * Kiralama Raporu
     */
    @GetMapping("/rentals")
    public String rentalReport(Model model) {
        model.addAttribute("report", reportService.getRentalReport());
        return "reports/rentals";
    }
}
