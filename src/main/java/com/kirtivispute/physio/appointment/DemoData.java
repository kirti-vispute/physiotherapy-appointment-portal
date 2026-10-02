package com.kirtivispute.physio.appointment;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;

@Component
@ConditionalOnProperty(name = "portal.demo.seed-enabled", havingValue = "true", matchIfMissing = true)
public class DemoData implements ApplicationRunner {
    private final PhysiotherapistRepository providers;
    private final SlotRepository slots;
    private final Clock clock;
    public DemoData(PhysiotherapistRepository providers, SlotRepository slots, Clock clock) {
        this.providers = providers; this.slots = slots; this.clock = clock;
    }
    @Override @Transactional
    public void run(ApplicationArguments arguments) {
        var seeds = List.of(new Physiotherapist("Dr Asha Kulkarni", "Musculoskeletal rehabilitation", "Fictional demo provider for movement and joint rehabilitation."),
                new Physiotherapist("Dr Rohan Deshmukh", "Sports physiotherapy", "Fictional demo provider for activity and sports rehabilitation."));
        ZoneId clinic = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(clock.withZone(clinic));
        for (var seed : seeds) {
            var provider = providers.findByFullName(seed.getFullName()).orElseGet(() -> providers.save(seed));
            for (int day = 1; day <= 3; day++) {
                for (int hour : new int[]{9, 11}) {
                    Instant start = today.plusDays(day).atTime(hour, 0).atZone(clinic).toInstant();
                    if (!slots.existsByPhysiotherapist_IdAndStartAt(provider.getId(), start))
                        slots.save(new Slot(provider, start, start.plusSeconds(45 * 60)));
                }
            }
        }
    }
}
