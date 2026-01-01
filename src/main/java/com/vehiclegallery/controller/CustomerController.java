package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Customer;
import com.vehiclegallery.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "customers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers/form";
    }

    @PostMapping
    public String save(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        customerService.save(customer);
        redirectAttributes.addFlashAttribute("success", "Müşteri başarıyla kaydedildi!");
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return customerService.findById(id)
                .map(customer -> {
                    model.addAttribute("customer", customer);
                    return "customers/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Müşteri bulunamadı!");
                    return "redirect:/customers";
                });
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        customerService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Müşteri başarıyla silindi!");
        return "redirect:/customers";
    }
}
