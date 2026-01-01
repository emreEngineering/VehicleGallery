package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Rental;
import com.vehiclegallery.service.RentalService;
import com.vehiclegallery.service.CustomerService;
import com.vehiclegallery.service.ListingService;
import com.vehiclegallery.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import com.vehiclegallery.entity.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

@Controller
@RequestMapping("/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ListingService listingService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String list(Model model, HttpSession session) {
        model.addAttribute("rentals", rentalService.findAll());

        // Galerici kontrolü
        String userType = (String) session.getAttribute("userType");
        boolean isDealer = "DEALER".equals(userType);
        model.addAttribute("isDealer", isDealer);

        // Bekleyen talep sayısı
        long pendingCount = rentalService.findByStatus("PENDING").size();
        model.addAttribute("pendingCount", pendingCount);

        return "rentals/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("rental", new Rental());
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("listings", listingService.findByType("RENTAL"));
        return "rentals/form";
    }

    @PostMapping
    public String create(@ModelAttribute Rental rental, RedirectAttributes redirectAttributes) {
        if (rental.getStartDate() == null) {
            rental.setStartDate(LocalDate.now());
        }
        if (rental.getStatus() == null) {
            rental.setStatus("ACTIVE");
        }
        rentalService.save(rental);
        redirectAttributes.addFlashAttribute("success", "Kiralama başarıyla oluşturuldu!");
        return "redirect:/rentals";
    }

    // Müşteri kiralama talebi
    @GetMapping("/rent/{listingId}")
    public String rentForm(@PathVariable Long listingId, Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");

        if (user == null || !"CUSTOMER".equals(userType)) {
            return "redirect:/login";
        }

        return listingService.findById(listingId)
                .map(listing -> {
                    if (!"RENTAL".equals(listing.getListingType())) {
                        return "redirect:/listings";
                    }
                    Rental rental = new Rental();
                    rental.setListing(listing);
                    rental.setCustomer((Customer) user);
                    model.addAttribute("rental", rental);
                    model.addAttribute("listing", listing);
                    return "rentals/rent";
                })
                .orElse("redirect:/listings");
    }

    @PostMapping("/rent")
    public String rent(@RequestParam Long listingId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");

        if (user == null || !"CUSTOMER".equals(userType)) {
            return "redirect:/login";
        }

        // Tarih parsing kontrolü
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate);
            end = LocalDate.parse(endDate);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Geçersiz tarih formatı! Lütfen tarihleri doğru giriniz.");
            return "redirect:/listings";
        }

        // Tarih validasyonu
        if (end.isBefore(start)) {
            redirectAttributes.addFlashAttribute("error", "Bitiş tarihi başlangıç tarihinden önce olamaz!");
            return "redirect:/rentals/rent/" + listingId;
        }

        if (start.isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "Başlangıç tarihi bugünden önce olamaz!");
            return "redirect:/rentals/rent/" + listingId;
        }

        return listingService.findById(listingId)
                .map(listing -> {
                    // Çakışma kontrolü
                    Long vehicleId = listing.getVehicle().getId();
                    if (rentalService.hasConflictingRentals(vehicleId, start, end)) {
                        redirectAttributes.addFlashAttribute("error",
                                "Bu araç seçilen tarihler arasında başka bir kiralama için ayrılmış!");
                        return "redirect:/rentals/rent/" + listingId;
                    }

                    long days = java.time.temporal.ChronoUnit.DAYS.between(start, end);
                    if (days < 1)
                        days = 1; // Minimum 1 gün

                    Rental rental = new Rental();
                    rental.setListing(listing);
                    rental.setCustomer((Customer) user);
                    rental.setStartDate(start);
                    rental.setEndDate(end);
                    rental.setTotalCost(listing.getDailyRate() != null ? listing.getDailyRate() * days : 0.0);
                    rental.setStatus("PENDING");
                    rentalService.save(rental);

                    redirectAttributes.addFlashAttribute("success", "Kiralama talebiniz oluşturuldu!");
                    return "redirect:/rentals";
                })
                .orElse("redirect:/listings");
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        return rentalService.findById(id)
                .map(rental -> {
                    model.addAttribute("rental", rental);
                    return "rentals/detail";
                })
                .orElse("redirect:/rentals");
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return rentalService.findById(id)
                .map(rental -> {
                    model.addAttribute("rental", rental);
                    model.addAttribute("customers", customerService.findAll());
                    model.addAttribute("listings", listingService.findByType("RENTAL"));
                    return "rentals/form";
                })
                .orElse("redirect:/rentals");
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Rental rental, RedirectAttributes redirectAttributes) {
        rental.setId(id);
        rentalService.save(rental);
        redirectAttributes.addFlashAttribute("success", "Kiralama başarıyla güncellendi!");
        return "redirect:/rentals";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rentalService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Kiralama başarıyla silindi!");
        return "redirect:/rentals";
    }

    // Galerici: Kiralama talebini onayla
    @GetMapping("/{id}/approve")
    public String approve(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String userType = (String) session.getAttribute("userType");
        if (!"DEALER".equals(userType)) {
            return "redirect:/login";
        }

        return rentalService.findById(id)
                .map(rental -> {
                    rental.setStatus("ACTIVE");
                    rentalService.save(rental);

                    // Müşteriye bildirim gönder
                    if (rental.getCustomer() != null && rental.getListing() != null) {
                        String vehicleInfo = rental.getListing().getVehicle().getBrand() + " " +
                                rental.getListing().getVehicle().getModel();
                        notificationService.sendRentalApprovedNotification(rental.getCustomer(), rental.getId(),
                                vehicleInfo);
                    }

                    redirectAttributes.addFlashAttribute("success", "Kiralama onaylandı!");
                    return "redirect:/rentals";
                })
                .orElse("redirect:/rentals");
    }

    // Galerici: Kiralama talebini reddet
    @GetMapping("/{id}/reject")
    public String reject(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String userType = (String) session.getAttribute("userType");
        if (!"DEALER".equals(userType)) {
            return "redirect:/login";
        }

        return rentalService.findById(id)
                .map(rental -> {
                    rental.setStatus("CANCELLED");
                    rentalService.save(rental);

                    // Müşteriye bildirim gönder
                    if (rental.getCustomer() != null && rental.getListing() != null) {
                        String vehicleInfo = rental.getListing().getVehicle().getBrand() + " " +
                                rental.getListing().getVehicle().getModel();
                        notificationService.sendRentalRejectedNotification(rental.getCustomer(), rental.getId(),
                                vehicleInfo);
                    }

                    redirectAttributes.addFlashAttribute("success", "Kiralama reddedildi!");
                    return "redirect:/rentals";
                })
                .orElse("redirect:/rentals");
    }
}
