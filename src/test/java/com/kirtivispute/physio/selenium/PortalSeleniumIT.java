package com.kirtivispute.physio.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

/** Five independent journeys; each booking is released by reusable teardown. */
class PortalSeleniumIT extends SeleniumSupport {
    @Test @DisplayName("SEL01 — Register a patient and sign in")
    void registration() throws Exception {
        register(); login();
        // Task 10 deliberate failing assertion; the correction commit restores patientName.
        assertEquals("TASK10_INTENTIONALLY_WRONG_NAME", visible(testId("signed-in-patient")).getText());
        saveEvidence("SEL01-registration", "PASS");
    }

    @Test @DisplayName("SEL02 — View a future available slot")
    void availableSlot() throws Exception {
        selectProvider();
        assertTrue(driver.findElements(testId("signed-in-patient")).isEmpty(), "Public slot view must work signed out");
        assertTrue(driver.findElements(selectedSlot()).getFirst().getText().contains("Sign in to book"));
        saveEvidence("SEL02-available-slot", "PASS");
    }

    @Test @DisplayName("SEL03 — Book an appointment and remove its slot from availability")
    void booking() throws Exception {
        book();
        saveEvidence("SEL03-booking", "PASS");
        driver.get(slotsUrl);
        visible(By.cssSelector("main h2"));
        assertTrue(driver.findElements(selectedSlot()).isEmpty(), "Booked slot must no longer be offered");
    }

    @Test @DisplayName("SEL04 — Cancel an appointment and release its slot")
    void cancellation() throws Exception {
        book(); click(testId("cancel-appointment"));
        wait.until(ExpectedConditions.textToBe(testId("appointment-status"), "CANCELLED"));
        assertEquals("Your appointment is cancelled. The slot is available again.", visible(testId("appointment-notice")).getText());
        assertTrue(driver.findElements(testId("cancel-appointment")).isEmpty());
        visible(testId("cancelled-message"));
        saveEvidence("SEL04-cancellation", "PASS");
        driver.get(slotsUrl);
        assertEquals(slotStart, visible(selectedSlot()).findElement(testId("slot-start")).getText());
    }

    @Test @DisplayName("SEL05 — Verify confirmation and own status after signing in again")
    void appointmentStatus() throws Exception {
        book(); click(testId("logout"));
        visible(testId("logout-success")); login(); click(testId("my-appointments"));
        var row = visible(ownAppointment());
        assertEquals("CONFIRMED", row.findElement(testId("appointment-status")).getText());
        assertTrue(row.getText().contains(providerName));
        assertTrue(row.getText().contains(slotStart));
        assertEquals(bookedUrl, row.findElement(testId("view-appointment")).getDomAttribute("href").startsWith("http")
                ? row.findElement(testId("view-appointment")).getDomAttribute("href")
                : baseUrl + row.findElement(testId("view-appointment")).getDomAttribute("href"));
        saveEvidence("SEL05-status", "PASS");
        row.findElement(testId("view-appointment")).click();
        assertEquals("Appointment #" + appointmentId, visible(testId("appointment-id")).getText());
        assertEquals("CONFIRMED", visible(testId("appointment-status")).getText());
        assertEquals(slotStart, visible(testId("appointment-start")).getText());
    }
}
