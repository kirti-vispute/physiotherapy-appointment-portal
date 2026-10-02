package com.kirtivispute.physio.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PhysiotherapistRepository extends JpaRepository<Physiotherapist, Long> {
    Optional<Physiotherapist> findByFullName(String fullName);
}
