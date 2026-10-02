package com.kirtivispute.physio.appointment;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import static com.kirtivispute.physio.appointment.PortalViews.*;

@RestController
@RequestMapping("/api")
public class AppointmentApiController {
    private final AppointmentService service;
    public AppointmentApiController(AppointmentService service) { this.service = service; }
    @GetMapping("/physiotherapists") public List<ProviderResponse> providers() { return service.providers(); }
    @GetMapping("/physiotherapists/{id}/slots") public List<SlotResponse> slots(@PathVariable Long id) { return service.openSlots(id); }
    @GetMapping("/appointments") public List<AppointmentResponse> owned(Principal patient) { return service.owned(patient.getName()); }
    @GetMapping("/appointments/{id}") public AppointmentResponse ownedById(@PathVariable Long id, Principal patient) { return service.ownedById(id, patient.getName()); }
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody BookingRequest request, Principal patient) {
        var appointment = service.book(request.slotId(), patient.getName());
        return ResponseEntity.created(URI.create("/api/appointments/" + appointment.id())).body(appointment);
    }
    @PatchMapping("/appointments/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable Long id, Principal patient) { return service.cancel(id, patient.getName()); }
}
