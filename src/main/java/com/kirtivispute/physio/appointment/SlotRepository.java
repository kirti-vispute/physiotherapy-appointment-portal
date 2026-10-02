package com.kirtivispute.physio.appointment;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    boolean existsByPhysiotherapist_IdAndStartAt(Long providerId, Instant startAt);

    @Query("select s from Slot s join fetch s.physiotherapist where s.physiotherapist.id = :providerId and s.available = true and s.startAt > :now order by s.startAt")
    List<Slot> findOpen(@Param("providerId") Long providerId, @Param("now") Instant now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Slot s where s.id = :id")
    Optional<Slot> lockById(@Param("id") Long id);
}
