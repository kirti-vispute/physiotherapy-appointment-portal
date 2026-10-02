package com.kirtivispute.physio.patient;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final PatientRepository patients;
    private final PasswordEncoder passwords;

    public RegistrationService(PatientRepository patients, PasswordEncoder passwords) {
        this.patients = patients;
        this.passwords = passwords;
    }

    public PatientResponse register(RegistrationRequest request) {
        if (patients.existsByEmail(request.getEmail())) throw new DuplicateEmailException();
        Patient patient = new Patient(request.getFullName(), request.getEmail(), passwords.encode(request.getPassword()));
        try {
            // The database constraint also guards two requests that pass the initial check together.
            return PatientResponse.from(patients.saveAndFlush(patient));
        } catch (DataIntegrityViolationException failure) {
            if (patients.existsByEmail(request.getEmail())) throw new DuplicateEmailException();
            throw failure;
        }
    }
}
