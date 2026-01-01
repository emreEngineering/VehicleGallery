package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Customer;
import com.vehiclegallery.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String list(Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");

        if (user == null || !"CUSTOMER".equals(userType)) {
            return "redirect:/login";
        }

        Customer customer = (Customer) user;
        model.addAttribute("notifications", notificationService.findByCustomerId(customer.getId()));
        model.addAttribute("unreadCount", notificationService.countUnread(customer.getId()));

        return "notifications/list";
    }

    @GetMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @GetMapping("/mark-all-read")
    public String markAllAsRead(HttpSession session, RedirectAttributes redirectAttributes) {
        Object user = session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");

        if (user == null || !"CUSTOMER".equals(userType)) {
            return "redirect:/login";
        }

        Customer customer = (Customer) user;
        notificationService.markAllAsRead(customer.getId());
        redirectAttributes.addFlashAttribute("success", "Tüm bildirimler okundu olarak işaretlendi!");
        return "redirect:/notifications";
    }
}
