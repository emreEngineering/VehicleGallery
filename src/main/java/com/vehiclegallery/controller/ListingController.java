package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Listing;
import com.vehiclegallery.service.ListingService;
import com.vehiclegallery.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/listings")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("listings", listingService.findAll());
        return "listings/list";
    }

    @GetMapping("/sales")
    public String salesList(Model model) {
        model.addAttribute("listings", listingService.findByListingType("SALE"));
        model.addAttribute("listingType", "SALE");
        return "listings/list";
    }

    @GetMapping("/rentals")
    public String rentalsListings(Model model) {
        model.addAttribute("listings", listingService.findByListingType("RENTAL"));
        model.addAttribute("listingType", "RENTAL");
        return "listings/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("listing", new Listing());
        model.addAttribute("vehicles", vehicleService.findAll());
        return "listings/form";
    }

    @PostMapping
    public String save(@ModelAttribute Listing listing, jakarta.servlet.http.HttpSession session,
            RedirectAttributes redirectAttributes) {
        com.vehiclegallery.entity.Personnel user = (com.vehiclegallery.entity.Personnel) session.getAttribute("user");

        // Check if user is logged in and is a Dealer
        if (user == null || !(user instanceof com.vehiclegallery.entity.Dealer)) {
            // Ideally show an error or redirect to a specific page
            // For now, redirect to login if not proper user
            return "redirect:/login";
        }

        // Safe cast
        listing.setDealer((com.vehiclegallery.entity.Dealer) user);

        if (listing.getPublishDate() == null) {
            listing.setPublishDate(java.time.LocalDate.now());
        }

        // Disable fields not relevant to the selected type to avoid validation errors
        // or data pollution
        if ("SALE".equals(listing.getListingType())) {
            listing.setDailyRate(null);
            listing.setMinDays(null);
            listing.setMaxDays(null);
        } else if ("RENTAL".equals(listing.getListingType())) {
            listing.setPrice(null);
            listing.setTradeIn(null);
        }

        listingService.save(listing);
        redirectAttributes.addFlashAttribute("success", "İlan başarıyla kaydedildi!");
        return "redirect:/listings";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        java.util.Optional<Listing> listing = listingService.findById(id);
        if (listing.isPresent()) {
            model.addAttribute("listing", listing.get());
            model.addAttribute("vehicles", vehicleService.findAll());
            return "listings/form";
        }
        return "redirect:/listings";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        listingService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "İlan başarıyla silindi!");
        return "redirect:/listings";
    }

}
