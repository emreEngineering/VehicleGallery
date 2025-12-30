package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Sale;
import com.vehiclegallery.service.SaleService;
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
@RequestMapping("/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ListingService listingService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String list(Model model, HttpSession session) {
        model.addAttribute("sales", saleService.findAll());

        // Galerici kontrolü
        String userType = (String) session.getAttribute("userType");
        boolean isDealer = "DEALER".equals(userType);
        model.addAttribute("isDealer", isDealer);

        // Bekleyen talep sayısı
        long pendingCount = saleService.findByStatus("PENDING").size();
        model.addAttribute("pendingCount", pendingCount);

        return "sales/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("sale", new Sale());
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("listings", listingService.findByType("SALE"));
        return "sales/form";
    }

    @PostMapping
    public String create(@ModelAttribute Sale sale, RedirectAttributes redirectAttributes) {
        if (sale.getDate() == null) {
            sale.setDate(LocalDate.now());
        }
        if (sale.getStatus() == null) {
            sale.setStatus("PENDING");
        }
        saleService.save(sale);
        redirectAttributes.addFlashAttribute("success", "Satış başarıyla oluşturuldu!");
        return "redirect:/sales";
    }

    // Müşteri satın alma talebi
    @GetMapping("/purchase/{listingId}")
    public String purchaseForm(@PathVariable Long listingId, Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");

        if (user == null || !"CUSTOMER".equals(userType)) {
            return "redirect:/login";
        }

        return listingService.findById(listingId)
                .map(listing -> {
                    if (!"SALE".equals(listing.getListingType())) {
                        return "redirect:/listings";
                    }
                    Sale sale = new Sale();
                    sale.setListing(listing);
                    sale.setCustomer((Customer) user);
                    sale.setAmount(listing.getPrice());
                    model.addAttribute("sale", sale);
                    model.addAttribute("listing", listing);
                    return "sales/purchase";
                })
                .orElse("redirect:/listings");
    }

    @PostMapping("/purchase")
    public String purchase(@RequestParam Long listingId, HttpSession session, RedirectAttributes redirectAttributes) {
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");

        if (user == null || !"CUSTOMER".equals(userType)) {
            return "redirect:/login";
        }

        return listingService.findById(listingId)
                .map(listing -> {
                    Sale sale = new Sale();
                    sale.setListing(listing);
                    sale.setCustomer((Customer) user);
                    sale.setAmount(listing.getPrice());
                    sale.setDate(LocalDate.now());
                    sale.setStatus("PENDING");
                    saleService.save(sale);

                    redirectAttributes.addFlashAttribute("success", "Satın alma talebiniz oluşturuldu!");
                    return "redirect:/sales";
                })
                .orElse("redirect:/listings");
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        return saleService.findById(id)
                .map(sale -> {
                    model.addAttribute("sale", sale);
                    return "sales/detail";
                })
                .orElse("redirect:/sales");
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return saleService.findById(id)
                .map(sale -> {
                    model.addAttribute("sale", sale);
                    model.addAttribute("customers", customerService.findAll());
                    model.addAttribute("listings", listingService.findByType("SALE"));
                    return "sales/form";
                })
                .orElse("redirect:/sales");
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Sale sale, RedirectAttributes redirectAttributes) {
        sale.setId(id);
        saleService.save(sale);
        redirectAttributes.addFlashAttribute("success", "Satış başarıyla güncellendi!");
        return "redirect:/sales";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        saleService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Satış başarıyla silindi!");
        return "redirect:/sales";
    }

    // Galerici: Satış talebini onayla
    @GetMapping("/{id}/approve")
    public String approve(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String userType = (String) session.getAttribute("userType");
        if (!"DEALER".equals(userType)) {
            return "redirect:/login";
        }

        return saleService.findById(id)
                .map(sale -> {
                    sale.setStatus("COMPLETED");
                    saleService.save(sale);

                    // İlanı pasif yap
                    if (sale.getListing() != null) {
                        sale.getListing().setIsActive(false);
                        listingService.save(sale.getListing());
                    }

                    // Müşteriye bildirim gönder
                    if (sale.getCustomer() != null && sale.getListing() != null) {
                        String vehicleInfo = sale.getListing().getVehicle().getBrand() + " " +
                                sale.getListing().getVehicle().getModel();
                        notificationService.sendSaleApprovedNotification(sale.getCustomer(), sale.getId(), vehicleInfo);
                    }

                    redirectAttributes.addFlashAttribute("success", "Satış onaylandı!");
                    return "redirect:/sales";
                })
                .orElse("redirect:/sales");
    }

    // Galerici: Satış talebini reddet
    @GetMapping("/{id}/reject")
    public String reject(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String userType = (String) session.getAttribute("userType");
        if (!"DEALER".equals(userType)) {
            return "redirect:/login";
        }

        return saleService.findById(id)
                .map(sale -> {
                    sale.setStatus("CANCELLED");
                    saleService.save(sale);

                    // Müşteriye bildirim gönder
                    if (sale.getCustomer() != null && sale.getListing() != null) {
                        String vehicleInfo = sale.getListing().getVehicle().getBrand() + " " +
                                sale.getListing().getVehicle().getModel();
                        notificationService.sendSaleRejectedNotification(sale.getCustomer(), sale.getId(), vehicleInfo);
                    }

                    redirectAttributes.addFlashAttribute("success", "Satış reddedildi!");
                    return "redirect:/sales";
                })
                .orElse("redirect:/sales");
    }
}
