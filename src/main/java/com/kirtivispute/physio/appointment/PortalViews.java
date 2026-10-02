package com.kirtivispute.physio.appointment;

import jakarta.validation.constraints.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class PortalViews {
    private PortalViews() { }
    private static final DateTimeFormatter CLINIC_TIME = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy · HH:mm z", Locale.ENGLISH)
            .withZone(ZoneId.of("Asia/Kolkata"));
    public record BookingRequest(@NotNull(message = "Select an appointment slot.") @Positive Long slotId) { }
    public record ProviderResponse(Long id, String fullName, String specialty, String description) {
        static ProviderResponse from(Physiotherapist p) { return new ProviderResponse(p.getId(), p.getFullName(), p.getSpecialty(), p.getDescription()); }
    }
    public record SlotResponse(Long id, Long physiotherapistId, Instant startAt, Instant endAt, String startLabel, String endLabel) {
        static SlotResponse from(Slot s) { return new SlotResponse(s.getId(), s.getPhysiotherapist().getId(), s.getStartAt(), s.getEndAt(),
                CLINIC_TIME.format(s.getStartAt()), CLINIC_TIME.format(s.getEndAt())); }
    }
    public record AppointmentResponse(Long id, Long slotId, Long physiotherapistId, String physiotherapistName,
                                      Instant startAt, Instant endAt, String startLabel, String endLabel,
                                      Appointment.Status status, Instant bookedAt, Instant cancelledAt, boolean cancellable) {
        static AppointmentResponse from(Appointment a, Instant now) {
            Slot s = a.getSlot();
            return new AppointmentResponse(a.getId(), s.getId(), s.getPhysiotherapist().getId(), s.getPhysiotherapist().getFullName(),
                    s.getStartAt(), s.getEndAt(), CLINIC_TIME.format(s.getStartAt()), CLINIC_TIME.format(s.getEndAt()),
                    a.getStatus(), a.getBookedAt(), a.getCancelledAt(), a.getStatus() == Appointment.Status.CONFIRMED && s.getStartAt().isAfter(now));
        }
    }
}
