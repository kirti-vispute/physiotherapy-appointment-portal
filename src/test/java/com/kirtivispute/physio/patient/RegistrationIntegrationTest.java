package com.kirtivispute.physio.patient;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:registration-tests;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class RegistrationIntegrationTest {
    @LocalServerPort int port;
    @Autowired PatientRepository patients;
    @Autowired PasswordEncoder passwords;
    HttpClient client;

    @BeforeEach
    void reset() {
        patients.deleteAll();
        client = HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
    }

    HttpResponse<String> post(String path, String body, String contentType) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", contentType).POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    String json(String name, String email, String password) {
        return "{\"fullName\":\"" + name + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }

    String form(String name, String email, String password) {
        return "fullName=" + URLEncoder.encode(name, StandardCharsets.UTF_8)
                + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);
    }

    @Test
    void createsOnePatientWithNormalizedEmailAndVerifiableHash() throws Exception {
        var response = post("/api/auth/register", json(" Asha Patil ", " ASHA@EXAMPLE.TEST ", "example123"), "application/json");
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.body()).contains("\"fullName\":\"Asha Patil\"", "\"email\":\"asha@example.test\"")
                .doesNotContain("password", "example123", "createdAt");
        assertThat(patients.count()).isEqualTo(1);
        Patient stored = patients.findByEmail("asha@example.test").orElseThrow();
        assertThat(stored.getId()).isPositive();
        assertThat(stored.getCreatedAt()).isNotNull();
        assertThat(stored.getPasswordHash()).isNotEqualTo("example123");
        assertThat(passwords.matches("example123", stored.getPasswordHash())).isTrue();
        assertThat(passwords.matches("wrongpass", stored.getPasswordHash())).isFalse();
    }

    static Stream<String> invalidRequests() {
        return Stream.of(
                "{\"fullName\":\" \",\"email\":\"\",\"password\":\"\"}",
                "{\"fullName\":\"Asha\",\"email\":\"invalid\",\"password\":\"example123\"}",
                "{\"fullName\":\"Asha\",\"email\":\"asha@example.test\",\"password\":\"short\"}",
                "{\"fullName\":\"Asha\",\"email\":\"asha@example.test\",\"password\":\"" + "x".repeat(129) + "\"}",
                "{}");
    }

    @ParameterizedTest @MethodSource("invalidRequests")
    void invalidInputCreatesNoAccount(String body) throws Exception {
        var response = post("/api/auth/register", body, "application/json");
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).contains("VALIDATION_FAILED", "fields").doesNotContain("short", "example123", "stackTrace");
        assertThat(patients.count()).isZero();
    }

    @Test
    void duplicateEmailIsCaseInsensitive() throws Exception {
        post("/api/auth/register", json("Asha", "asha@example.test", "example123"), "application/json");
        var response = post("/api/auth/register", json("Other", " ASHA@EXAMPLE.TEST ", "different123"), "application/json");
        assertThat(response.statusCode()).isEqualTo(409);
        assertThat(response.body()).contains("EMAIL_IN_USE").doesNotContain("password", "sql", "constraint");
        assertThat(patients.count()).isEqualTo(1);
        assertThat(patients.findByEmail("asha@example.test").orElseThrow().getFullName()).isEqualTo("Asha");
    }

    @Test
    void malformedJsonGetsReadableError() throws Exception {
        var response = post("/api/auth/register", "{broken", "application/json");
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).contains("INVALID_REQUEST").doesNotContain("stackTrace");
        assertThat(patients.count()).isZero();
    }

    @Test
    void formSuccessRedirectsAndRefreshDoesNotSubmitAgain() throws Exception {
        var response = post("/register", form("Asha", "asha@example.test", "example123"), "application/x-www-form-urlencoded");
        assertThat(response.statusCode()).isEqualTo(302);
        assertThat(response.headers().firstValue("Location")).hasValue("http://localhost:" + port + "/register");
        assertThat(get("/register").body()).contains("Your account has been created", "registration-success");
        get("/register");
        assertThat(patients.count()).isEqualTo(1);
    }

    @Test
    void formErrorsEscapeInputAndNeverEchoPassword() throws Exception {
        var response = post("/register", form("<script>alert(1)</script>", "invalid", "private-short"), "application/x-www-form-urlencoded");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Enter a valid email address.", "&lt;script&gt;")
                .doesNotContain("<script>alert(1)</script>", "private-short");
        assertThat(patients.count()).isZero();
    }

    @Test
    void duplicateFormShowsFieldError() throws Exception {
        String body = form("Asha", "asha@example.test", "example123");
        post("/register", body, "application/x-www-form-urlencoded");
        var response = post("/register", body, "application/x-www-form-urlencoded");
        assertThat(response.body()).contains("An account with this email already exists.").doesNotContain("example123");
        assertThat(patients.count()).isEqualTo(1);
    }

    @Test
    void databaseRejectsDuplicateEvenWithoutServiceCheck() {
        patients.saveAndFlush(new Patient("First", "asha@example.test", "test-hash"));
        assertThatThrownBy(() -> patients.saveAndFlush(new Patient("Second", "asha@example.test", "other-test-hash")))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThat(patients.count()).isEqualTo(1);
    }

    @Test
    void simultaneousRegistrationsHaveOneWinner() throws Exception {
        try (var executor = Executors.newFixedThreadPool(2)) {
            CountDownLatch start = new CountDownLatch(1);
            Callable<Integer> attempt = () -> {
                start.await();
                return post("/api/auth/register", json("Asha", "race@example.test", "example123"), "application/json").statusCode();
            };
            var first = executor.submit(attempt);
            var second = executor.submit(attempt);
            start.countDown();
            assertThat(List.of(first.get(15, TimeUnit.SECONDS), second.get(15, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(201, 409);
            assertThat(patients.count()).isEqualTo(1);
        }
    }

    @Test
    void samePasswordUsesDifferentSaltForEachPatient() throws Exception {
        post("/api/auth/register", json("Asha", "one@example.test", "example123"), "application/json");
        post("/api/auth/register", json("Dev", "two@example.test", "example123"), "application/json");
        var first = patients.findByEmail("one@example.test").orElseThrow();
        var second = patients.findByEmail("two@example.test").orElseThrow();
        assertThat(first.getPasswordHash()).isNotEqualTo(second.getPasswordHash());
        assertThat(passwords.matches("example123", second.getPasswordHash())).isTrue();
    }
}
