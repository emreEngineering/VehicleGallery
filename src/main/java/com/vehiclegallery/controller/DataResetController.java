package com.vehiclegallery.controller;

import com.vehiclegallery.service.DataResetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/system")
public class DataResetController {

    @Autowired
    private DataResetService dataResetService;

    @GetMapping("/reset")
    public String resetPage(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "system/reset";
    }

    @PostMapping("/reset-transactions")
    public String resetTransactions(RedirectAttributes redirectAttributes) {
        try {
            dataResetService.resetTransactions();
            redirectAttributes.addFlashAttribute("success", "Tüm satış, kiralama ve teklif verileri silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "İşlem başarısız: " + e.getMessage());
        }
        return "redirect:/system/reset";
    }

    @PostMapping("/reset-all")
    public String resetAll(RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            dataResetService.resetAll();
            session.invalidate(); // Logout as users are gone
            return "redirect:/login?reset=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "İşlem başarısız: " + e.getMessage());
            return "redirect:/system/reset";
        }
    }

    @PostMapping("/add-samples")
    public String addSamples(RedirectAttributes redirectAttributes) {
        try {
            dataResetService.addSampleVehicles();
            redirectAttributes.addFlashAttribute("success", "10 adet örnek araç başarıyla eklendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "İşlem başarısız: " + e.getMessage());
        }
        return "redirect:/system/reset";
    }
}
