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
        var seeds = List.of(
                new Physiotherapist("Dr Asha Kulkarni", "Musculoskeletal rehabilitation", "Fictional demo provider for movement and joint rehabilitation."),
                new Physiotherapist("Dr Rohan Deshmukh", "Sports physiotherapy", "Fictional demo provider for activity and sports rehabilitation."),
                new Physiotherapist("Dr Neha Shah", "Neurological rehabilitation", "Fictional demo provider focusing on movement coordination and neurological rehabilitation."),
                new Physiotherapist("Dr Vikram Patil", "Post-operative rehabilitation", "Fictional demo provider focusing on mobility and rehabilitation after surgery."),
                new Physiotherapist("Dr Meera Joshi", "Paediatric physiotherapy", "Fictional demo provider focusing on movement, balance and physical development in children."),
                new Physiotherapist("Dr Arjun Nair", "Geriatric mobility", "Fictional demo provider focusing on everyday mobility and strength for older adults."),
                new Physiotherapist("Dr Sneha Iyer", "Cardiorespiratory physiotherapy", "Fictional demo provider focusing on breathing, endurance and physical conditioning."),
                new Physiotherapist("Dr Kabir Mehta", "Spine and posture care", "Fictional demo provider focusing on spinal mobility, posture and everyday movement."),
                new Physiotherapist("Dr Priya Rao", "Hand and wrist rehabilitation", "Fictional demo provider focusing on hand function, wrist mobility and daily activities."),
                new Physiotherapist("Dr Aditya Kulkarni", "Shoulder rehabilitation", "Fictional demo provider focusing on shoulder mobility and upper-limb rehabilitation."),
                new Physiotherapist("Dr Ananya Desai", "Women's health physiotherapy", "Fictional demo provider focusing on pelvic health and physical rehabilitation for women."),
                new Physiotherapist("Dr Rahul Menon", "Balance and gait training", "Fictional demo provider focusing on balance, walking patterns and movement confidence."),
                new Physiotherapist("Dr Kavya Reddy", "Occupational rehabilitation", "Fictional demo provider focusing on functional movement and return to everyday work activities."),
                new Physiotherapist("Dr Nikhil Bansal", "Chronic pain rehabilitation", "Fictional demo provider focusing on functional movement and rehabilitation for persistent pain."),
                new Physiotherapist("Dr Ishita Verma", "Hip and knee rehabilitation", "Fictional demo provider focusing on lower-limb mobility and hip and knee rehabilitation."));
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
