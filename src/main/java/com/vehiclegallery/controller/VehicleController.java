package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Vehicle;
import com.vehiclegallery.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("vehicles", vehicleService.findAll());
        return "vehicles/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        return "vehicles/form";
    }

    @PostMapping
    public String save(@ModelAttribute Vehicle vehicle, RedirectAttributes redirectAttributes) {
        vehicleService.save(vehicle);
        redirectAttributes.addFlashAttribute("success", "Araç başarıyla kaydedildi!");
        return "redirect:/vehicles";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return vehicleService.findById(id)
                .map(vehicle -> {
                    model.addAttribute("vehicle", vehicle);
                    return "vehicles/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Araç bulunamadı!");
                    return "redirect:/vehicles";
                });
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehicleService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Araç başarıyla silindi!");
        return "redirect:/vehicles";
    }
}
