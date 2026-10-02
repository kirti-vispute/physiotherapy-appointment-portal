package com.kirtivispute.physio.patient;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {
    private final RegistrationService registration;

    public RegistrationController(RegistrationService registration) { this.registration = registration; }

    @GetMapping("/register")
    public String form(Model model) {
        model.addAttribute("registration", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registration") RegistrationRequest request,
                           BindingResult errors, RedirectAttributes redirect) {
        if (!errors.hasErrors()) {
            try {
                registration.register(request);
                redirect.addFlashAttribute("registered", true);
                return "redirect:/register";
            } catch (DuplicateEmailException failure) {
                errors.rejectValue("email", "duplicate", failure.getMessage());
            }
        }
        request.setPassword(null);
        return "register";
    }
}
