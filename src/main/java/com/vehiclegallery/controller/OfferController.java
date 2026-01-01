package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Personnel;
import com.vehiclegallery.repository.OfferRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OfferController {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private com.vehiclegallery.service.ListingService listingService;

    @GetMapping("/offers/new/{listingId}")
    public String showCreateForm(@org.springframework.web.bind.annotation.PathVariable Long listingId, Model model,
            HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        return listingService.findById(listingId).map(listing -> {
            com.vehiclegallery.entity.Offer offer = new com.vehiclegallery.entity.Offer();
            offer.setListing(listing);
            offer.setCustomer((com.vehiclegallery.entity.Customer) user);
            model.addAttribute("offer", offer);
            model.addAttribute("listing", listing);
            return "customer/offer-form";
        }).orElse("redirect:/listings");
    }

    @org.springframework.web.bind.annotation.PostMapping("/offers")
    public String create(@org.springframework.web.bind.annotation.ModelAttribute com.vehiclegallery.entity.Offer offer,
            @org.springframework.web.bind.annotation.RequestParam Long listingId,
            HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        return listingService.findById(listingId).map(listing -> {
            offer.setCustomer((com.vehiclegallery.entity.Customer) user);
            offer.setListing(listing);
            offer.setOfferDate(java.time.LocalDate.now());
            offer.setStatus("PENDING");

            offerRepository.save(offer);
            redirectAttributes.addFlashAttribute("success", "Teklifiniz başarıyla iletildi!");
            return "redirect:/my-offers";
        }).orElse("redirect:/listings");
    }

    @GetMapping("/my-offers")
    public String myOffers(Model model, HttpSession session) {
        return "redirect:/sales";
    }

    @GetMapping("/dealer-offers")
    public String dealerOffers(Model model, HttpSession session) {
        return "redirect:/sales";
    }

    @GetMapping("/offers/accept/{id}")
    public String acceptOffer(@org.springframework.web.bind.annotation.PathVariable Long id, HttpSession session) {
        // Implementation to accept offer
        // Ideally should check permissions
        return updateOfferStatus(id, "ACCEPTED", session);
    }

    @GetMapping("/offers/reject/{id}")
    public String rejectOffer(@org.springframework.web.bind.annotation.PathVariable Long id, HttpSession session) {
        return updateOfferStatus(id, "REJECTED", session);
    }

    private String updateOfferStatus(Long offerId, String status, HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        var offerOpt = offerRepository.findById(offerId);
        if (offerOpt.isPresent()) {
            var offer = offerOpt.get();
            // simple check: if user is the dealer of the listing
            if (offer.getListing().getDealer().getId().equals(user.getId())) {
                offer.setStatus(status);
                offerRepository.save(offer);
            }
        }
        return "redirect:/dealer-offers";
    }
}
