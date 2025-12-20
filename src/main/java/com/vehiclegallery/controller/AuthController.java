package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Customer;
import com.vehiclegallery.entity.Dealer;
import com.vehiclegallery.repository.CustomerRepository;
import com.vehiclegallery.repository.DealerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DealerRepository dealerRepository;

    // ==================== LOGIN PAGES ====================

    @GetMapping("/login")
    public String loginChoice() {
        return "auth/login";
    }

    @GetMapping("/login/individual")
    public String individualLoginForm() {
        return "auth/individual-login";
    }

    @GetMapping("/login/corporate")
    public String corporateLoginForm() {
        return "auth/corporate-login";
    }

    @GetMapping("/login/dealer")
    public String dealerLoginForm() {
        return "auth/dealer-login";
    }

    // ==================== LOGIN PROCESSING ====================

    @PostMapping("/login/customer")
    public String customerLogin(@RequestParam String email,
            @RequestParam String password,
            @RequestParam String customerType,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        var customerOpt = customerRepository.findByEmail(email);

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (customer.getPassword() != null && customer.getPassword().equals(password)) {
                if (customer.getCustomerType().equals(customerType)) {
                    session.setAttribute("user", customer);
                    session.setAttribute("userType", "CUSTOMER");
                    session.setAttribute("customerType", customerType);
                    return "redirect:/";
                }
            }
        }

        redirectAttributes.addFlashAttribute("error", "Geçersiz e-posta veya şifre!");
        String loginPage = customerType.equals("INDIVIDUAL") ? "/login/individual" : "/login/corporate";
        return "redirect:" + loginPage;
    }

    @PostMapping("/login/dealer")
    public String dealerLogin(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        var dealerOpt = dealerRepository.findByEmail(email);

        if (dealerOpt.isPresent()) {
            Dealer dealer = dealerOpt.get();
            if (dealer.getPassword() != null && dealer.getPassword().equals(password)) {
                session.setAttribute("user", dealer);
                session.setAttribute("userType", "DEALER");
                return "redirect:/";
            }
        }

        redirectAttributes.addFlashAttribute("error", "Geçersiz e-posta veya şifre!");
        return "redirect:/login/dealer";
    }

    // ==================== REGISTER PAGES ====================

    @GetMapping("/register/individual")
    public String individualRegisterForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "auth/individual-register";
    }

    @GetMapping("/register/corporate")
    public String corporateRegisterForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "auth/corporate-register";
    }

    @GetMapping("/register/dealer")
    public String dealerRegisterForm(Model model) {
        model.addAttribute("dealer", new Dealer());
        return "auth/dealer-register";
    }

    // ==================== REGISTER PROCESSING ====================

    @PostMapping("/register/individual")
    public String registerIndividual(@ModelAttribute Customer customer,
            RedirectAttributes redirectAttributes) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Bu e-posta adresi zaten kayıtlı!");
            return "redirect:/register/individual";
        }

        if (customer.getNationalId() != null
                && customerRepository.findByNationalId(customer.getNationalId()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Bu TC Kimlik numarası zaten kayıtlı!");
            return "redirect:/register/individual";
        }

        customer.setCustomerType("INDIVIDUAL");
        customerRepository.save(customer);
        redirectAttributes.addFlashAttribute("success", "Kayıt başarılı! Giriş yapabilirsiniz.");
        return "redirect:/login/individual";
    }

    @PostMapping("/register/corporate")
    public String registerCorporate(@ModelAttribute Customer customer,
            RedirectAttributes redirectAttributes) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Bu e-posta adresi zaten kayıtlı!");
            return "redirect:/register/corporate";
        }

        if (customer.getTaxNumber() != null && !customer.getTaxNumber().isEmpty()) {
            var existingByTax = customerRepository.findAll().stream()
                    .filter(c -> customer.getTaxNumber().equals(c.getTaxNumber()))
                    .findFirst();
            if (existingByTax.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Bu vergi numarası zaten kayıtlı!");
                return "redirect:/register/corporate";
            }
        }

        customer.setCustomerType("CORPORATE");
        customerRepository.save(customer);
        redirectAttributes.addFlashAttribute("success", "Kayıt başarılı! Giriş yapabilirsiniz.");
        return "redirect:/login/corporate";
    }

    @PostMapping("/register/dealer")
    public String registerDealer(@ModelAttribute Dealer dealer,
            RedirectAttributes redirectAttributes) {
        if (dealerRepository.findByEmail(dealer.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Bu e-posta adresi zaten kayıtlı!");
            return "redirect:/register/dealer";
        }

        if (dealer.getTaxNumber() != null && dealerRepository.findByTaxNumber(dealer.getTaxNumber()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Bu vergi numarası zaten kayıtlı!");
            return "redirect:/register/dealer";
        }

        dealerRepository.save(dealer);
        redirectAttributes.addFlashAttribute("success", "Kayıt başarılı! Giriş yapabilirsiniz.");
        return "redirect:/login/dealer";
    }

    // ==================== LOGOUT ====================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
