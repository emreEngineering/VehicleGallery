package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Sale;
import com.vehiclegallery.service.SaleService;
import com.vehiclegallery.service.CustomerService;
import com.vehiclegallery.service.ListingService;
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

    @GetMapping
    public String list(Model model) {
        model.addAttribute("sales", saleService.findAll());
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

                    // İlanı pasif yap
                    listing.setIsActive(false);
                    listingService.save(listing);

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
}
