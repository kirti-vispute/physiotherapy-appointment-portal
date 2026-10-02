package com.kirtivispute.physio.appointment;

import com.kirtivispute.physio.patient.Patient;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "appointments")
public class Appointment {
    public enum Status { CONFIRMED, CANCELLED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false) private Patient patient;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false) private Slot slot;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Status status = Status.CONFIRMED;
    @Column(nullable = false) private Instant bookedAt;
    private Instant cancelledAt;

    protected Appointment() { }
    public Appointment(Patient patient, Slot slot, Instant bookedAt) {
        this.patient = patient; this.slot = slot; this.bookedAt = bookedAt;
    }
    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public Slot getSlot() { return slot; }
    public Status getStatus() { return status; }
    public Instant getBookedAt() { return bookedAt; }
    public Instant getCancelledAt() { return cancelledAt; }
    public void cancel(Instant at) { status = Status.CANCELLED; cancelledAt = at; }
}
