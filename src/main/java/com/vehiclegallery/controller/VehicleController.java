package com.vehiclegallery.controller;

import com.vehiclegallery.entity.Vehicle;
import com.vehiclegallery.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String save(@ModelAttribute Vehicle vehicle) {
        vehicleService.save(vehicle);
        return "redirect:/vehicles";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        vehicleService.findById(id).ifPresent(vehicle -> model.addAttribute("vehicle", vehicle));
        return "vehicles/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        vehicleService.deleteById(id);
        return "redirect:/vehicles";
    }
}
