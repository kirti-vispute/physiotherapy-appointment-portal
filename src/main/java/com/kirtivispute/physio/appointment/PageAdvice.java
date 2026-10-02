package com.kirtivispute.physio.appointment;

import com.kirtivispute.physio.HomeController;
import com.kirtivispute.physio.patient.*;
import com.kirtivispute.physio.security.LoginPageController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import java.security.Principal;

@Order(1)
@ControllerAdvice(assignableTypes = {HomeController.class, RegistrationController.class, LoginPageController.class, PortalController.class})
public class PageAdvice {
    private final PatientRepository patients;
    public PageAdvice(PatientRepository patients) { this.patients = patients; }
    @ModelAttribute("currentPatient")
    public PatientResponse patient(Principal principal) {
        return principal == null ? null : patients.findByEmail(principal.getName()).map(PatientResponse::from).orElse(null);
    }
    @ExceptionHandler(PortalException.class)
    public String expected(PortalException failure, HttpServletResponse response, Model model) {
        response.setStatus(failure.getStatus().value()); model.addAttribute("message", failure.getMessage()); return "error";
    }
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public String invalid(HttpServletResponse response, Model model) {
        response.setStatus(400); model.addAttribute("message", "Please select a valid physiotherapist, slot, or appointment."); return "error";
    }
}
