package com.kirtivispute.physio.appointment;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("select a from Appointment a join fetch a.slot s join fetch s.physiotherapist where a.patient.email = :email order by s.startAt desc, a.id desc")
    List<Appointment> findOwned(@Param("email") String email);

    @Query("select a from Appointment a join fetch a.slot s join fetch s.physiotherapist where a.id = :id and a.patient.email = :email")
    Optional<Appointment> findOwnedById(@Param("id") Long id, @Param("email") String email);

    long countBySlot_IdAndStatus(Long slotId, Appointment.Status status);
}
