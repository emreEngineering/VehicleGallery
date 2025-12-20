package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Listing;
import com.vehiclegallery.service.ListingService;
import com.vehiclegallery.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String save(@ModelAttribute Listing listing) {
        listingService.save(listing);
        return "redirect:/listings";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        listingService.deleteById(id);
        return "redirect:/listings";
    }
}
