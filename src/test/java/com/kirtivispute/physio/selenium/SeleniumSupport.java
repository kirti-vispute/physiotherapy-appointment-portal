package com.kirtivispute.physio.selenium;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.*;

abstract class SeleniumSupport {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl, patientName, email, password, providerName;
    protected String bookedUrl, appointmentId, slotsUrl, slotId, slotStart;
    private ZoneId clinicZone;
    @RegisterExtension final FailureScreenshotExtension failureScreenshot = new FailureScreenshotExtension(this);

    protected static By testId(String name) { return By.cssSelector("[data-testid='" + name + "']"); }
    protected WebElement visible(By locator) { return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)); }
    protected void click(By locator) { wait.until(ExpectedConditions.elementToBeClickable(locator)).click(); }

    @BeforeEach void setup(TestInfo test) throws Exception {
        Properties data = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/selenium/test-data.properties")) {
            if (input == null) { throw new IllegalStateException("Selenium fixture resource is missing"); }
            data.load(input);
        }
        String caseName = test.getTestMethod().orElseThrow().getName();
        patientName = data.getProperty("patient.namePrefix") + " " + caseName;
        email = caseName.toLowerCase(Locale.ROOT) + "." + UUID.randomUUID() + "@" + data.getProperty("patient.emailDomain");
        password = data.getProperty("patient.password");
        providerName = data.getProperty("provider.name");
        clinicZone = ZoneId.of(data.getProperty("clinic.zone"));
        baseUrl = System.getProperty("selenium.baseUrl", "http://localhost:8082").replaceAll("/+$", "");
        URI target = URI.create(baseUrl);
        if (!List.of("localhost", "127.0.0.1").contains(target.getHost()) || !"http".equals(target.getScheme())) {
            throw new IllegalArgumentException("This fictional-data suite targets local HTTP demo deployments only");
        }
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("selenium.headless", "true"))) { options.addArguments("--headless=new"); }
        options.addArguments("--window-size=1280,900");
        String binary = System.getProperty("selenium.chromeBinary");
        if (binary != null && !binary.isBlank()) { options.setBinary(binary); }
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(baseUrl + "/");
        visible(testId("browse-providers"));
    }

    protected void register() {
        driver.get(baseUrl + "/register");
        visible(By.id("fullName")).sendKeys(patientName);
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        click(testId("register-submit"));
        assertTrue(visible(testId("registration-success")).getText().contains("Your account has been created."));
    }
    protected void login() {
        driver.get(baseUrl + "/login");
        visible(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        click(testId("login-submit"));
        assertEquals(patientName, visible(testId("signed-in-patient")).getText());
    }
    protected List<WebElement> selectProvider() {
        driver.get(baseUrl + "/physiotherapists");
        List<WebElement> providers = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(testId("physiotherapist-card")));
        assertTrue(providers.size() >= 2, "At least two fictional providers must be visible");
        WebElement provider = providers.stream().filter(card -> card.findElement(By.tagName("h2")).getText().equals(providerName))
                .findFirst().orElseThrow(() -> new AssertionError("Fixture provider is not displayed"));
        WebElement slotsLink = provider.findElement(testId("view-slots"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", slotsLink);
        wait.until(ExpectedConditions.elementToBeClickable(slotsLink)).click();
        List<WebElement> slots = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(testId("slot-card")));
        slotsUrl = driver.getCurrentUrl();
        slotId = slots.getFirst().getDomAttribute("data-slot-id");
        slotStart = slots.getFirst().findElement(testId("slot-start")).getText();
        assertTrue(slotStart.endsWith("IST"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy · HH:mm 'IST'", Locale.ENGLISH);
        assertTrue(LocalDateTime.parse(slotStart, format).atZone(clinicZone).toInstant().isAfter(Instant.now()), "Displayed slot must be in the future");
        return slots;
    }
    protected void book() {
        register(); login();
        List<WebElement> slots = selectProvider();
        slots.getFirst().findElement(testId("book-slot")).click();
        visible(testId("appointment-id"));
        bookedUrl = driver.getCurrentUrl();
        appointmentId = URI.create(bookedUrl).getPath().substring("/appointments/".length());
        assertTrue(appointmentId.matches("\\d+"));
        assertEquals("Appointment #" + appointmentId, driver.findElement(testId("appointment-id")).getText());
        assertEquals("CONFIRMED", visible(testId("appointment-status")).getText());
        assertEquals(slotStart, visible(testId("appointment-start")).getText());
        assertEquals(providerName, driver.findElement(By.cssSelector("main h2")).getText());
        assertEquals("Your appointment is confirmed.", visible(testId("appointment-notice")).getText());
    }
    protected By selectedSlot() { return By.cssSelector("[data-testid='slot-card'][data-slot-id='" + slotId + "']"); }
    protected By ownAppointment() { return By.cssSelector("[data-testid='appointment-card'][data-appointment-id='" + appointmentId + "']"); }

    protected void saveEvidence(String name, String result) throws Exception {
        if (driver == null) { throw new IllegalStateException("Browser unavailable; no screenshot could be captured"); }
        Path folder = Path.of("target", "selenium-evidence"); Files.createDirectories(folder);
        Files.write(folder.resolve(name + ".png"), ((RemoteWebDriver)driver).getScreenshotAs(OutputType.BYTES));
        Properties details = new Properties();
        details.setProperty("result", result); details.setProperty("url", driver.getCurrentUrl());
        details.setProperty("patient.email", email); details.setProperty("captured_at", Instant.now().toString());
        details.setProperty("browser.version", ((RemoteWebDriver)driver).getCapabilities().getBrowserVersion());
        details.setProperty("driver.details", String.valueOf(((RemoteWebDriver)driver).getCapabilities().getCapability("chrome")));
        if (slotId != null) { details.setProperty("slot.id", slotId); }
        if (appointmentId != null) { details.setProperty("appointment.id", appointmentId); }
        try (var output = Files.newOutputStream(folder.resolve(name + ".properties"))) { details.store(output, "Actual Selenium browser evidence; fictional fixture, no password"); }
    }

    @AfterEach void cleanup(TestInfo test) throws Exception {
        if (driver == null) { return; }
        try {
            if (bookedUrl != null) {
                driver.get(bookedUrl);
                if (visible(testId("appointment-status")).getText().equals("CONFIRMED")) {
                    click(testId("cancel-appointment"));
                    wait.until(ExpectedConditions.textToBe(testId("appointment-status"), "CANCELLED"));
                }
                assertEquals("CANCELLED", visible(testId("appointment-status")).getText(), "Fixture booking must be released");
            }
        } catch (Exception | AssertionError error) {
            try { saveEvidence(test.getTestMethod().orElseThrow().getName() + "-cleanup-failure", error.toString()); }
            catch (Exception captureError) { error.addSuppressed(captureError); }
            throw error;
        } finally { driver.quit(); }
    }
}
