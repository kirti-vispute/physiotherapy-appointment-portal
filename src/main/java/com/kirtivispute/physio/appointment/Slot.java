package com.kirtivispute.physio.appointment;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "slots", uniqueConstraints = @UniqueConstraint(name = "uk_provider_start", columnNames = {"physiotherapist_id", "start_at"}))
public class Slot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "physiotherapist_id", nullable = false) private Physiotherapist physiotherapist;
    @Column(name = "start_at", nullable = false) private Instant startAt;
    @Column(nullable = false) private Instant endAt;
    @Column(nullable = false) private boolean available = true;

    protected Slot() { }
    public Slot(Physiotherapist physiotherapist, Instant startAt, Instant endAt) {
        if (!startAt.isBefore(endAt)) throw new IllegalArgumentException("Slot must end after it starts.");
        this.physiotherapist = physiotherapist; this.startAt = startAt; this.endAt = endAt;
    }
    public Long getId() { return id; }
    public Physiotherapist getPhysiotherapist() { return physiotherapist; }
    public Instant getStartAt() { return startAt; }
    public Instant getEndAt() { return endAt; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
