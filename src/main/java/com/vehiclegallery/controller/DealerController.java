package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Personnel;

import com.vehiclegallery.service.ListingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DealerController {

    @Autowired
    private ListingService listingService;

    @GetMapping("/my-listings")
    public String myListings(Model model, HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Ensure user is a dealer (optional but good practice)
        // Ignoring specific instance check for now to match previous logic logic

        model.addAttribute("listings", listingService.findByDealerId(user.getId()));
        return "listings/my-listings";
    }
}
