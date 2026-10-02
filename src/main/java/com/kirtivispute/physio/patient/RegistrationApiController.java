package com.kirtivispute.physio.patient;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegistrationApiController {
    private final RegistrationService registration;

    public RegistrationApiController(RegistrationService registration) { this.registration = registration; }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponse register(@Valid @RequestBody RegistrationRequest request) {
        return registration.register(request);
    }
}
