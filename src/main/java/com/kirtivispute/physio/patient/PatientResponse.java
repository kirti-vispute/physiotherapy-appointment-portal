package com.kirtivispute.physio.patient;

public record PatientResponse(Long id, String fullName, String email) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(patient.getId(), patient.getFullName(), patient.getEmail());
    }
}
