package com.kirtivispute.physio.appointment;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
public class PortalController {
    private final AppointmentService service;
    public PortalController(AppointmentService service) { this.service = service; }
    @GetMapping("/physiotherapists")
    public String providers(Model model) { model.addAttribute("providers", service.providers()); return "physiotherapists"; }
    @GetMapping("/physiotherapists/{id}/slots")
    public String slots(@PathVariable Long id, Model model) {
        model.addAttribute("provider", service.provider(id)); model.addAttribute("slots", service.openSlots(id)); return "slots";
    }
    @GetMapping("/appointments")
    public String owned(Principal patient, Model model) { model.addAttribute("appointments", service.owned(patient.getName())); return "appointments"; }
    @GetMapping("/appointments/{id}")
    public String ownedById(@PathVariable Long id, Principal patient, Model model) {
        model.addAttribute("appointment", service.ownedById(id, patient.getName())); return "appointment";
    }
    @PostMapping("/appointments")
    public String book(@RequestParam Long slotId, Principal patient, RedirectAttributes redirect) {
        var appointment = service.book(slotId, patient.getName());
        redirect.addFlashAttribute("notice", "Your appointment is confirmed."); return "redirect:/appointments/" + appointment.id();
    }
    @PostMapping("/appointments/{id}/cancel")
    public String cancel(@PathVariable Long id, Principal patient, RedirectAttributes redirect) {
        service.cancel(id, patient.getName()); redirect.addFlashAttribute("notice", "Your appointment is cancelled. The slot is available again.");
        return "redirect:/appointments/" + id;
    }
}
