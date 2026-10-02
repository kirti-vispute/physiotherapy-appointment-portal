package com.kirtivispute.physio.appointment;

import jakarta.persistence.*;

@Entity
@Table(name = "physiotherapists")
public class Physiotherapist {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 100) private String fullName;
    @Column(nullable = false, length = 100) private String specialty;
    @Column(nullable = false, length = 500) private String description;

    protected Physiotherapist() { }
    public Physiotherapist(String fullName, String specialty, String description) {
        this.fullName = fullName; this.specialty = specialty; this.description = description;
    }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getSpecialty() { return specialty; }
    public String getDescription() { return description; }
}
