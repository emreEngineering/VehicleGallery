package com.vehiclegallery.controller;

import com.vehiclegallery.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ListingService listingService;

    @Autowired
    private SaleService saleService;

    @Autowired
    private RentalService rentalService;

    @GetMapping("/")
    public String dashboard(Model model, HttpSession session) {
        model.addAttribute("vehicleCount", vehicleService.count());
        model.addAttribute("customerCount", customerService.count());
        model.addAttribute("listingCount", listingService.count());
        model.addAttribute("saleCount", saleService.count());
        model.addAttribute("rentalCount", rentalService.count());
        model.addAttribute("saleListingCount", listingService.countByType("SALE"));
        model.addAttribute("rentalListingCount", listingService.countByType("RENTAL"));
        model.addAttribute("totalRevenue", saleService.getTotalRevenue());

        model.addAttribute("recentVehicles", vehicleService.findAll().stream().limit(5).toList());
        model.addAttribute("recentListings", listingService.findActiveListings().stream().limit(5).toList());

        // Kullanıcı tipi kontrolü - Müşteriler için menü filtreleme
        String userType = (String) session.getAttribute("userType");
        model.addAttribute("isDealer", "DEALER".equals(userType));
        model.addAttribute("currentUser", session.getAttribute("user"));

        return "index";
    }

}
