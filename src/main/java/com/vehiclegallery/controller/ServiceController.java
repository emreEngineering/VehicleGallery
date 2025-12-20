package com.vehiclegallery.controller;

import com.vehiclegallery.entity.ServiceRecord;
import com.vehiclegallery.service.ServiceRecordService;
import com.vehiclegallery.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
@RequestMapping("/services")
public class ServiceController {

    @Autowired
    private ServiceRecordService serviceRecordService;

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("serviceRecords", serviceRecordService.findAll());
        return "services/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("serviceRecord", new ServiceRecord());
        model.addAttribute("vehicles", vehicleService.findAll());
        return "services/form";
    }

    @PostMapping
    public String create(@ModelAttribute ServiceRecord serviceRecord, RedirectAttributes redirectAttributes) {
        if (serviceRecord.getDate() == null) {
            serviceRecord.setDate(LocalDate.now());
        }
        serviceRecordService.save(serviceRecord);
        redirectAttributes.addFlashAttribute("success", "Servis kaydı başarıyla oluşturuldu!");
        return "redirect:/services";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return serviceRecordService.findById(id)
                .map(record -> {
                    model.addAttribute("serviceRecord", record);
                    model.addAttribute("vehicles", vehicleService.findAll());
                    return "services/form";
                })
                .orElse("redirect:/services");
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ServiceRecord serviceRecord,
            RedirectAttributes redirectAttributes) {
        serviceRecord.setId(id);
        serviceRecordService.save(serviceRecord);
        redirectAttributes.addFlashAttribute("success", "Servis kaydı başarıyla güncellendi!");
        return "redirect:/services";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        serviceRecordService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Servis kaydı başarıyla silindi!");
        return "redirect:/services";
    }
}
