package com.kirtivispute.physio.selenium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import static org.junit.jupiter.api.Assertions.fail;

/** Explicit diagnostic only; excluded from the five-journey profile's includes. */
@EnabledIfSystemProperty(named = "selenium.failureProbe", matches = "true")
class ScreenshotCaptureProbeIT extends SeleniumSupport {
    @Test void intentionalFailure() {
        fail("Intentional Task 9 local failure: verify screenshot capture before browser teardown");
    }
}
