package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Personnel;
import com.vehiclegallery.repository.BankAccountRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BankController {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @GetMapping("/my-bank-accounts")
    public String myBankAccounts(Model model, HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        var bankAccounts = bankAccountRepository.findByOwnerId(user.getId());
        model.addAttribute("bankAccounts", bankAccounts);

        return "bank/my-bank-accounts";
    }

    @GetMapping("/my-bank-accounts/add")
    public String addBankAccountForm(Model model, HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("bankAccount", new com.vehiclegallery.entity.BankAccount());
        return "bank/add-account";
    }

    @org.springframework.web.bind.annotation.PostMapping("/my-bank-accounts/add")
    public String addBankAccount(
            @org.springframework.web.bind.annotation.ModelAttribute com.vehiclegallery.entity.BankAccount bankAccount,
            HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        bankAccount.setOwner(user);
        // Default balance 0 if null
        if (bankAccount.getBalance() == null) {
            bankAccount.setBalance(0.0);
        }

        bankAccountRepository.save(bankAccount);

        return "redirect:/my-bank-accounts";
    }

    @GetMapping("/my-bank-accounts/edit/{id}")
    public String editBankAccountForm(@org.springframework.web.bind.annotation.PathVariable Long id, Model model,
            HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        var accountOpt = bankAccountRepository.findById(id);
        if (accountOpt.isPresent() && accountOpt.get().getOwner().getId().equals(user.getId())) {
            model.addAttribute("bankAccount", accountOpt.get());
            return "bank/edit-account";
        }

        return "redirect:/my-bank-accounts";
    }

    @org.springframework.web.bind.annotation.PostMapping("/my-bank-accounts/edit")
    public String updateBankAccount(
            @org.springframework.web.bind.annotation.ModelAttribute com.vehiclegallery.entity.BankAccount bankAccount,
            HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Verify ownership before saving
        var existingOpt = bankAccountRepository.findById(bankAccount.getId());
        if (existingOpt.isPresent() && existingOpt.get().getOwner().getId().equals(user.getId())) {
            bankAccount.setOwner(user);
            bankAccountRepository.save(bankAccount);
        }

        return "redirect:/my-bank-accounts";
    }

    @GetMapping("/my-bank-accounts/delete/{id}")
    public String deleteBankAccount(@org.springframework.web.bind.annotation.PathVariable Long id,
            HttpSession session) {
        Personnel user = (Personnel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        var accountOpt = bankAccountRepository.findById(id);
        if (accountOpt.isPresent() && accountOpt.get().getOwner().getId().equals(user.getId())) {
            bankAccountRepository.deleteById(id);
        }

        return "redirect:/my-bank-accounts";
    }
}
