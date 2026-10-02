package com.kirtivispute.physio.appointment;

import com.kirtivispute.physio.patient.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:portal-tests;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop", "portal.demo.seed-enabled=false"
})
@Import(PortalIntegrationTest.FixedTime.class)
class PortalIntegrationTest {
    @TestConfiguration static class FixedTime {
        @Bean @Primary Clock testClock() { return Clock.fixed(Instant.parse("2026-10-02T14:00:00Z"), ZoneOffset.UTC); }
    }
    @LocalServerPort int port;
    @Autowired PhysiotherapistRepository providers;
    @Autowired SlotRepository slots;
    @Autowired AppointmentRepository appointments;
    @Autowired PatientRepository patients;
    @Autowired PasswordEncoder passwords;
    @Autowired Clock clock;
    Patient asha, dev;
    Physiotherapist provider, emptyProvider;
    Slot future;

    @BeforeEach void fixtures() {
        appointments.deleteAll(); slots.deleteAll(); providers.deleteAll(); patients.deleteAll();
        String hash = passwords.encode("example123");
        asha = patients.saveAndFlush(new Patient("Asha Patil", "asha@example.test", hash));
        dev = patients.saveAndFlush(new Patient("Dev Mehta", "dev@example.test", hash));
        provider = providers.saveAndFlush(new Physiotherapist("Dr Test One", "Movement", "Fictional test provider"));
        emptyProvider = providers.saveAndFlush(new Physiotherapist("Dr Test Two", "Sports", "Fictional test provider"));
        future = slots.saveAndFlush(new Slot(provider, clock.instant().plusSeconds(86400), clock.instant().plusSeconds(89100)));
    }

    final class BrowserSession {
        final CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        final HttpClient client = HttpClient.newBuilder().cookieHandler(cookies).connectTimeout(Duration.ofSeconds(10)).build();
        HttpResponse<String> request(String method, String path, String body, String type, String token) throws Exception {
            var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).timeout(Duration.ofSeconds(20));
            if (type != null) builder.header("Content-Type", type);
            if (token != null) builder.header("X-CSRF-TOKEN", token);
            builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
            return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        }
        HttpResponse<String> get(String path) throws Exception { return request("GET", path, null, null, null); }
        String csrf() throws Exception {
            var response = get("/api/auth/csrf");
            assertThat(response.statusCode()).isEqualTo(200);
            var match = Pattern.compile("\"token\":\"([^\"]+)\"").matcher(response.body());
            assertThat(match.find()).isTrue(); return match.group(1);
        }
        HttpResponse<String> mutate(String method, String path, String body) throws Exception { return request(method, path, body, "application/json", csrf()); }
        void login(String email) throws Exception {
            var response = mutate("POST", "/api/auth/login", "{\"email\":\"" + email + "\",\"password\":\"example123\"}");
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).doesNotContain("password", "example123");
        }
        String sessionId() { return cookies.getCookieStore().getCookies().stream().filter(c -> c.getName().equals("JSESSIONID")).findFirst().orElseThrow().getValue(); }
        long book(Long slotId) throws Exception {
            var response = mutate("POST", "/api/appointments", "{\"slotId\":" + slotId + "}");
            assertThat(response.statusCode()).isEqualTo(201);
            long id = id(response.body());
            assertThat(response.headers().firstValue("Location")).hasValue("/api/appointments/" + id);
            return id;
        }
    }
    long id(String body) {
        var match = Pattern.compile("\"id\":([0-9]+)").matcher(body);
        assertThat(match.find()).isTrue(); return Long.parseLong(match.group(1));
    }
    BrowserSession signedIn(String email) throws Exception { var session = new BrowserSession(); session.login(email); return session; }

    @Test void loginRotatesSessionAndCsrfAndPersistsIdentity() throws Exception {
        var session = new BrowserSession(); String oldToken = session.csrf(); String oldId = session.sessionId();
        session.login(" ASHA@EXAMPLE.TEST ");
        assertThat(session.sessionId()).isNotEqualTo(oldId);
        assertThat(session.get("/appointments").body()).contains("Asha Patil", "You have no appointments yet");
        assertThat(session.request("POST", "/api/appointments", "{\"slotId\":" + future.getId() + "}", "application/json", oldToken).statusCode()).isEqualTo(403);
        assertThat(appointments.count()).isZero();
        assertThat(session.get("/api/appointments").statusCode()).isEqualTo(200);
    }

    @Test void invalidCredentialsHaveTheSameGeneralError() throws Exception {
        var session = new BrowserSession();
        var wrongPassword = session.mutate("POST", "/api/auth/login", "{\"email\":\"asha@example.test\",\"password\":\"incorrect\"}");
        var unknownUser = session.mutate("POST", "/api/auth/login", "{\"email\":\"missing@example.test\",\"password\":\"incorrect\"}");
        assertThat(wrongPassword.statusCode()).isEqualTo(401);
        assertThat(unknownUser.statusCode()).isEqualTo(401);
        assertThat(wrongPassword.body()).isEqualTo(unknownUser.body()).contains("Invalid email or password.");
        assertThat(session.get("/api/appointments").statusCode()).isEqualTo(401);
    }

    @Test void loginValidationAndMissingCsrfCannotAuthenticate() throws Exception {
        var session = new BrowserSession();
        assertThat(session.mutate("POST", "/api/auth/login", "{}").statusCode()).isEqualTo(400);
        assertThat(session.request("POST", "/api/auth/login", "{\"email\":\"asha@example.test\",\"password\":\"example123\"}", "application/json", null).statusCode()).isEqualTo(403);
        assertThat(session.get("/api/appointments").statusCode()).isEqualTo(401);
    }

    @Test void anonymousUsersCanBrowseButCannotManageAppointments() throws Exception {
        var session = new BrowserSession();
        assertThat(session.get("/physiotherapists").statusCode()).isEqualTo(200);
        assertThat(session.get("/api/physiotherapists").body()).contains("Dr Test One", "Dr Test Two");
        assertThat(session.get("/api/appointments").statusCode()).isEqualTo(401);
        assertThat(session.mutate("POST", "/api/appointments", "{\"slotId\":" + future.getId() + "}").statusCode()).isEqualTo(401);
        assertThat(session.get("/appointments").headers().firstValue("Location")).hasValue("http://localhost:" + port + "/login");
        assertThat(appointments.count()).isZero();
    }

    @Test void availabilityFiltersPastOccupiedAndOtherProvidersAndShowsClinicTime() throws Exception {
        Slot past = slots.saveAndFlush(new Slot(provider, clock.instant().minusSeconds(3600), clock.instant().minusSeconds(900)));
        Slot occupied = new Slot(provider, clock.instant().plusSeconds(172800), clock.instant().plusSeconds(175500));
        occupied.setAvailable(false); slots.saveAndFlush(occupied);
        var session = new BrowserSession();
        var response = session.get("/api/physiotherapists/" + provider.getId() + "/slots");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"id\":" + future.getId(), "19:30 IST", "2026-10-03T14:00:00Z")
                .doesNotContain("\"id\":" + past.getId(), "\"id\":" + occupied.getId());
        assertThat(session.get("/physiotherapists/" + emptyProvider.getId() + "/slots").body()).contains("No future slots are available");
        assertThat(session.get("/api/physiotherapists/" + emptyProvider.getId() + "/slots").body()).isEqualTo("[]");
    }

    @Test void missingAndMalformedProviderIdsShowReadableErrors() throws Exception {
        var session = new BrowserSession();
        assertThat(session.get("/api/physiotherapists/999999/slots").statusCode()).isEqualTo(404);
        assertThat(session.get("/api/physiotherapists/not-a-number/slots").statusCode()).isEqualTo(400);
        var html = session.get("/physiotherapists/999999/slots");
        assertThat(html.statusCode()).isEqualTo(404);
        assertThat(html.body()).contains("Physiotherapist not found.").doesNotContain("stackTrace", "SQLException");
    }

    @Test void noProvidersHasAnExplicitEmptyState() throws Exception {
        slots.deleteAll(); providers.deleteAll();
        var session = new BrowserSession();
        assertThat(session.get("/physiotherapists").body()).contains("No physiotherapists are available right now.");
        assertThat(session.get("/api/physiotherapists").body()).isEqualTo("[]");
    }

    @Test void bookingCreatesConfirmationAndRemovesSlotFromAvailability() throws Exception {
        var session = signedIn(asha.getEmail()); long appointmentId = session.book(future.getId());
        assertThat(session.get("/api/appointments/" + appointmentId).body()).contains("CONFIRMED", "Dr Test One", "19:30 IST");
        assertThat(session.get("/appointments/" + appointmentId).body()).contains("Appointment #" + appointmentId, "CONFIRMED", "cancel-appointment");
        assertThat(session.get("/api/appointments").body()).contains("\"id\":" + appointmentId);
        assertThat(session.get("/api/physiotherapists/" + provider.getId() + "/slots").body()).isEqualTo("[]");
        assertThat(slots.findById(future.getId()).orElseThrow().isAvailable()).isFalse();
        assertThat(appointments.countBySlot_IdAndStatus(future.getId(), Appointment.Status.CONFIRMED)).isEqualTo(1);
    }

    @Test void suppliedPatientIdCannotChangeBookingOwnership() throws Exception {
        var session = signedIn(asha.getEmail());
        var response = session.mutate("POST", "/api/appointments", "{\"slotId\":" + future.getId() + ",\"patientId\":" + dev.getId() + "}");
        assertThat(response.statusCode()).isEqualTo(201);
        var other = signedIn(dev.getEmail());
        assertThat(other.get("/api/appointments").body()).isEqualTo("[]");
        assertThat(other.get("/api/appointments/" + id(response.body())).statusCode()).isEqualTo(404);
    }

    @Test void invalidMissingPastAndOccupiedSlotsCannotCreateBookings() throws Exception {
        var session = signedIn(asha.getEmail());
        assertThat(session.mutate("POST", "/api/appointments", "{}").statusCode()).isEqualTo(400);
        assertThat(session.mutate("POST", "/api/appointments", "{\"slotId\":-1}").statusCode()).isEqualTo(400);
        assertThat(session.mutate("POST", "/api/appointments", "{\"slotId\":999999}").statusCode()).isEqualTo(404);
        Slot atStart = slots.saveAndFlush(new Slot(provider, clock.instant(), clock.instant().plusSeconds(2700)));
        assertThat(session.mutate("POST", "/api/appointments", "{\"slotId\":" + atStart.getId() + "}").statusCode()).isEqualTo(409);
        session.book(future.getId());
        assertThat(session.mutate("POST", "/api/appointments", "{\"slotId\":" + future.getId() + "}").statusCode()).isEqualTo(409);
        assertThat(appointments.count()).isEqualTo(1);
    }

    @Test void twoPatientsRacingToBookHaveExactlyOneWinner() throws Exception {
        var first = signedIn(asha.getEmail()); var second = signedIn(dev.getEmail());
        String firstCsrf = first.csrf(), secondCsrf = second.csrf();
        try (var executor = Executors.newFixedThreadPool(2)) {
            var start = new CountDownLatch(1);
            var a = executor.submit(() -> { start.await(); return first.request("POST", "/api/appointments", "{\"slotId\":" + future.getId() + "}", "application/json", firstCsrf).statusCode(); });
            var b = executor.submit(() -> { start.await(); return second.request("POST", "/api/appointments", "{\"slotId\":" + future.getId() + "}", "application/json", secondCsrf).statusCode(); });
            start.countDown();
            assertThat(List.of(a.get(25, TimeUnit.SECONDS), b.get(25, TimeUnit.SECONDS))).containsExactlyInAnyOrder(201, 409);
        }
        assertThat(appointments.count()).isEqualTo(1);
        assertThat(appointments.countBySlot_IdAndStatus(future.getId(), Appointment.Status.CONFIRMED)).isEqualTo(1);
    }

    @Test void anotherPatientCannotViewOrCancelAnAppointment() throws Exception {
        var owner = signedIn(asha.getEmail()); long appointmentId = owner.book(future.getId());
        var other = signedIn(dev.getEmail());
        assertThat(other.get("/api/appointments").body()).isEqualTo("[]");
        assertThat(other.get("/api/appointments/" + appointmentId).statusCode()).isEqualTo(404);
        assertThat(other.mutate("PATCH", "/api/appointments/" + appointmentId + "/cancel", null).statusCode()).isEqualTo(404);
        assertThat(other.get("/appointments/" + appointmentId).statusCode()).isEqualTo(404);
        assertThat(appointments.findById(appointmentId).orElseThrow().getStatus()).isEqualTo(Appointment.Status.CONFIRMED);
        assertThat(slots.findById(future.getId()).orElseThrow().isAvailable()).isFalse();
    }

    @Test void cancellationChangesStatusAndReleasesSlotAndIsRepeatable() throws Exception {
        var session = signedIn(asha.getEmail()); long appointmentId = session.book(future.getId());
        var response = session.mutate("PATCH", "/api/appointments/" + appointmentId + "/cancel", null);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("CANCELLED", "\"cancellable\":false");
        assertThat(session.get("/appointments/" + appointmentId).body()).contains("This appointment is cancelled.").doesNotContain("data-testid=\"cancel-appointment\"");
        assertThat(session.get("/api/physiotherapists/" + provider.getId() + "/slots").body()).contains("\"id\":" + future.getId());
        var again = session.mutate("PATCH", "/api/appointments/" + appointmentId + "/cancel", null);
        assertThat(again.body()).isEqualTo(response.body());
        assertThat(appointments.count()).isEqualTo(1);
    }

    @Test void repeatedOldCancellationDoesNotReleaseASlotRebookedByAnotherPatient() throws Exception {
        var owner = signedIn(asha.getEmail()); long oldId = owner.book(future.getId());
        owner.mutate("PATCH", "/api/appointments/" + oldId + "/cancel", null);
        var other = signedIn(dev.getEmail()); long newId = other.book(future.getId());
        assertThat(owner.mutate("PATCH", "/api/appointments/" + oldId + "/cancel", null).statusCode()).isEqualTo(200);
        assertThat(slots.findById(future.getId()).orElseThrow().isAvailable()).isFalse();
        assertThat(appointments.findById(newId).orElseThrow().getStatus()).isEqualTo(Appointment.Status.CONFIRMED);
        assertThat(appointments.countBySlot_IdAndStatus(future.getId(), Appointment.Status.CONFIRMED)).isEqualTo(1);
    }

    @Test void concurrentCancellationIsIdempotent() throws Exception {
        var owner = signedIn(asha.getEmail()); long appointmentId = owner.book(future.getId());
        String token = owner.csrf();
        try (var executor = Executors.newFixedThreadPool(2)) {
            var start = new CountDownLatch(1);
            Callable<Integer> cancel = () -> { start.await(); return owner.request("PATCH", "/api/appointments/" + appointmentId + "/cancel", null, "application/json", token).statusCode(); };
            var a = executor.submit(cancel); var b = executor.submit(cancel); start.countDown();
            assertThat(List.of(a.get(25, TimeUnit.SECONDS), b.get(25, TimeUnit.SECONDS))).containsExactly(200, 200);
        }
        assertThat(slots.findById(future.getId()).orElseThrow().isAvailable()).isTrue();
        assertThat(appointments.findById(appointmentId).orElseThrow().getCancelledAt()).isEqualTo(clock.instant());
    }

    @Test void atStartTimeCancellationClosesButStatusRemainsReadable() throws Exception {
        Slot started = new Slot(provider, clock.instant(), clock.instant().plusSeconds(2700));
        started.setAvailable(false); started = slots.saveAndFlush(started);
        var appointment = appointments.saveAndFlush(new Appointment(asha, started, clock.instant().minusSeconds(3600)));
        var owner = signedIn(asha.getEmail());
        assertThat(owner.mutate("PATCH", "/api/appointments/" + appointment.getId() + "/cancel", null).statusCode()).isEqualTo(409);
        assertThat(owner.get("/api/appointments/" + appointment.getId()).body()).contains("CONFIRMED", "\"cancellable\":false");
        assertThat(owner.get("/appointments/" + appointment.getId()).body()).contains("Cancellation is closed.");
        assertThat(slots.findById(started.getId()).orElseThrow().isAvailable()).isFalse();
    }

    @Test void missingOrInvalidCsrfCannotBookOrCancel() throws Exception {
        var owner = signedIn(asha.getEmail());
        assertThat(owner.request("POST", "/api/appointments", "{\"slotId\":" + future.getId() + "}", "application/json", null).statusCode()).isEqualTo(403);
        long appointmentId = owner.book(future.getId());
        assertThat(owner.request("PATCH", "/api/appointments/" + appointmentId + "/cancel", null, "application/json", "invalid-token").statusCode()).isEqualTo(403);
        assertThat(appointments.findById(appointmentId).orElseThrow().getStatus()).isEqualTo(Appointment.Status.CONFIRMED);
    }

    @Test void logoutEndsSessionAndProtectsAppointmentPagesAgain() throws Exception {
        var owner = signedIn(asha.getEmail()); owner.book(future.getId());
        assertThat(owner.mutate("POST", "/api/auth/logout", null).statusCode()).isEqualTo(204);
        assertThat(owner.get("/api/appointments").statusCode()).isEqualTo(401);
        assertThat(owner.get("/appointments").headers().firstValue("Location")).hasValue("http://localhost:" + port + "/login");
    }

    @Test void browserFormsLoginBookAndCancelWithCsrfAndRedirects() throws Exception {
        var browser = new BrowserSession();
        String form = "email=" + URLEncoder.encode(asha.getEmail(), StandardCharsets.UTF_8) + "&password=example123";
        var login = browser.request("POST", "/login", form, "application/x-www-form-urlencoded", browser.csrf());
        assertThat(login.statusCode()).isEqualTo(302);
        assertThat(login.headers().firstValue("Location")).hasValue("http://localhost:" + port + "/physiotherapists");
        assertThat(browser.get("/physiotherapists").body()).contains("Asha Patil", "My appointments");
        var booking = browser.request("POST", "/appointments", "slotId=" + future.getId(), "application/x-www-form-urlencoded", browser.csrf());
        assertThat(booking.statusCode()).isEqualTo(302);
        String detail = URI.create(booking.headers().firstValue("Location").orElseThrow()).getPath();
        assertThat(browser.get(detail).body()).contains("Your appointment is confirmed.", "CONFIRMED");
        var cancellation = browser.request("POST", detail + "/cancel", "", "application/x-www-form-urlencoded", browser.csrf());
        assertThat(cancellation.statusCode()).isEqualTo(302);
        assertThat(browser.get(detail).body()).contains("Your appointment is cancelled.", "CANCELLED");
        var logout = browser.request("POST", "/logout", "", "application/x-www-form-urlencoded", browser.csrf());
        assertThat(logout.statusCode()).isEqualTo(302);
        assertThat(browser.get("/login?logout").body()).contains("You have signed out.");
    }

    @Test void seedDataIsRepeatableWithoutDuplicateOrReopenedSlots() {
        slots.deleteAll(); providers.deleteAll();
        DemoData seed = new DemoData(providers, slots, clock);
        seed.run(null);
        assertThat(providers.count()).isEqualTo(2); assertThat(slots.count()).isEqualTo(12);
        Slot closed = slots.findAll().getFirst(); closed.setAvailable(false); slots.saveAndFlush(closed);
        seed.run(null);
        assertThat(providers.count()).isEqualTo(2); assertThat(slots.count()).isEqualTo(12);
        assertThat(slots.findById(closed.getId()).orElseThrow().isAvailable()).isFalse();
    }
}
