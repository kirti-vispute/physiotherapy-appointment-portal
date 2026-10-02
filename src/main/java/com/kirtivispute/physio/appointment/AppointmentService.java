package com.kirtivispute.physio.appointment;

import com.kirtivispute.physio.patient.PatientRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.util.List;
import static com.kirtivispute.physio.appointment.PortalViews.*;
import static org.springframework.http.HttpStatus.*;

@Service
@Transactional(readOnly = true)
public class AppointmentService {
    private final PhysiotherapistRepository providers;
    private final SlotRepository slots;
    private final AppointmentRepository appointments;
    private final PatientRepository patients;
    private final EntityManager entities;
    private final Clock clock;

    public AppointmentService(PhysiotherapistRepository providers, SlotRepository slots, AppointmentRepository appointments,
                              PatientRepository patients, EntityManager entities, Clock clock) {
        this.providers = providers; this.slots = slots; this.appointments = appointments;
        this.patients = patients; this.entities = entities; this.clock = clock;
    }
    public List<ProviderResponse> providers() {
        return providers.findAll(Sort.by("fullName")).stream().map(ProviderResponse::from).toList();
    }
    public ProviderResponse provider(Long id) {
        return ProviderResponse.from(providers.findById(id).orElseThrow(() -> new PortalException(NOT_FOUND, "PROVIDER_NOT_FOUND", "Physiotherapist not found.")));
    }
    public List<SlotResponse> openSlots(Long providerId) {
        provider(providerId);
        return slots.findOpen(providerId, clock.instant()).stream().map(SlotResponse::from).toList();
    }
    public List<AppointmentResponse> owned(String email) {
        return appointments.findOwned(email).stream().map(a -> AppointmentResponse.from(a, clock.instant())).toList();
    }
    public AppointmentResponse ownedById(Long id, String email) { return AppointmentResponse.from(ownedEntity(id, email), clock.instant()); }
    private Appointment ownedEntity(Long id, String email) {
        return appointments.findOwnedById(id, email).orElseThrow(() -> new PortalException(NOT_FOUND, "APPOINTMENT_NOT_FOUND", "Appointment not found."));
    }
    private Slot lockSlot(Long id) {
        return slots.lockById(id).orElseThrow(() -> new PortalException(NOT_FOUND, "SLOT_NOT_FOUND", "Appointment slot not found."));
    }

    @Transactional
    public AppointmentResponse book(Long slotId, String email) {
        if (slotId == null || slotId <= 0) throw new PortalException(BAD_REQUEST, "INVALID_SLOT", "Select an appointment slot.");
        Slot slot = lockSlot(slotId);
        if (!slot.isAvailable() || !slot.getStartAt().isAfter(clock.instant()))
            throw new PortalException(CONFLICT, "SLOT_UNAVAILABLE", "This slot is no longer available. Please choose another.");
        var patient = patients.findByEmail(email).orElseThrow(() -> new PortalException(UNAUTHORIZED, "SIGN_IN_REQUIRED", "Please sign in again."));
        slot.setAvailable(false);
        Appointment appointment = appointments.save(new Appointment(patient, slot, clock.instant()));
        return AppointmentResponse.from(appointment, clock.instant());
    }

    @Transactional
    public AppointmentResponse cancel(Long id, String email) {
        Appointment appointment = ownedEntity(id, email);
        Slot slot = lockSlot(appointment.getSlot().getId());
        // Re-read after the shared slot lock: another cancellation may have committed while we waited.
        entities.refresh(appointment);
        if (appointment.getStatus() == Appointment.Status.CANCELLED) return AppointmentResponse.from(appointment, clock.instant());
        if (!slot.getStartAt().isAfter(clock.instant()))
            throw new PortalException(CONFLICT, "CANCELLATION_CLOSED", "This appointment has started and can no longer be cancelled.");
        appointment.cancel(clock.instant());
        slot.setAvailable(true);
        return AppointmentResponse.from(appointment, clock.instant());
    }
}
