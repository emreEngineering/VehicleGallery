package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Personnel;
import com.vehiclegallery.repository.AddressRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    @GetMapping("/my-addresses")
    public String myAddresses(Model model, HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        var addresses = addressRepository.findByPersonnelId(user.getId());
        model.addAttribute("addresses", addresses);

        return "customer/my-addresses";
    }

    @GetMapping("/my-addresses/add")
    public String addAddressForm(Model model, HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("address", new com.vehiclegallery.entity.Address());
        return "customer/add-address";
    }

    @PostMapping("/my-addresses/add")
    public String addAddress(
            @ModelAttribute com.vehiclegallery.entity.Address address,
            HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Now we can set personnel directly for any user type (Customer or Dealer)
        address.setPersonnel(user);
        addressRepository.save(address);

        return "redirect:/my-addresses";
    }

    @GetMapping("/my-addresses/edit/{id}")
    public String editAddressForm(@org.springframework.web.bind.annotation.PathVariable Long id, Model model,
            HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        var addressOpt = addressRepository.findById(id);
        if (addressOpt.isPresent() && addressOpt.get().getPersonnel().getId().equals(user.getId())) {
            model.addAttribute("address", addressOpt.get());
            return "customer/edit-address";
        }

        return "redirect:/my-addresses";
    }

    @PostMapping("/my-addresses/edit")
    public String updateAddress(
            @ModelAttribute com.vehiclegallery.entity.Address address,
            HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        var existingOpt = addressRepository.findById(address.getId());
        if (existingOpt.isPresent() && existingOpt.get().getPersonnel().getId().equals(user.getId())) {
            address.setPersonnel(user);
            addressRepository.save(address);
        }

        return "redirect:/my-addresses";
    }

    @GetMapping("/my-addresses/delete/{id}")
    public String deleteAddress(@org.springframework.web.bind.annotation.PathVariable Long id, HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        var addressOpt = addressRepository.findById(id);
        if (addressOpt.isPresent() && addressOpt.get().getPersonnel().getId().equals(user.getId())) {
            addressRepository.deleteById(id);
        }

        return "redirect:/my-addresses";
    }
}
