# Task 9 — Selenium Test Plan

**Owner:** Kirti Vispute (23102C0078)  
**Date:** 3 October 2026  
**Scope:** Five critical journeys from US-01 and US-04–US-07. Backend validation, ownership, races and time boundaries retain the 34 integration tests verified in Task 6. Jenkins integration and the deliberate deployment-blocking failure belong to Task 10.

## Common prerequisites and test data

- Healthy local fictional-data portal at `http://localhost:8082`, with Dr Asha Kulkarni and Dr Rohan Deshmukh and at least one future free slot for Dr Asha Kulkarni.
- Java 21, Maven, installed Chrome, and driver download access on the first run. Selenium Manager resolves a matching driver; each test starts a fresh temporary browser profile.
- Versioned fixture: `src/test/resources/selenium/test-data.properties`. Name prefix **Selenium Demo**, email domain **example.test**, password **FictionalTest123!** (a public dummy test value), provider **Dr Asha Kulkarni**, clinic zone **Asia/Kolkata**.
- Registration/booking/status cases generate `<method>.<UUID>@example.test`, so repeat runs do not conflict with existing accounts. No real patient information is used. Actual generated emails, IDs, browser/driver versions and URLs are saved in `target/selenium-evidence/*.properties`; passwords are omitted.
- Tests run sequentially and do not depend on one another. Booking teardown cancels any still-confirmed fixture appointment through the UI, then closes the driver in `finally`. Accounts and cancelled appointments remain as fictional evidence; no database purge is performed.
- Explicit 15-second waits, stable `data-testid`/input IDs, dynamic slot/appointment IDs and assertions are used. Implicit wait is zero; no hard-coded sleeps are used.

## SEL01 — User registration

**Method:** `PortalSeleniumIT.registration`  
**Objective:** Register a new patient and demonstrate that the created account can sign in.  
**Preconditions:** Healthy portal; fresh signed-out browser; generated email not previously used.  
**Test data:** Name `Selenium Demo registration`, unique `registration.<UUID>@example.test`, dummy password above.

**Steps:**

1. Open `/register`; fill full name, email and password using input IDs.
2. Click `register-submit`; wait for `registration-success`.
3. Assert the account-created message.
4. Open `/login`; sign in with that exact account; wait for `signed-in-patient`.
5. Assert the displayed patient name and capture the authenticated page.

**Expected:** A success message and successful sign-in for the new account.  
**Actual result:** Created the unique fictional account, observed the success notice, and signed in with the same credentials; displayed patient name matched. Both the initial run and focused rerun passed on 3 October 2026.  
**Pass/fail:** ✅ PASS  
**Evidence:** `SEL01-registration.png` and matching metadata; Failsafe case `registration`.

## SEL02 — View available slot

**Method:** `PortalSeleniumIT.availableSlot`  
**Objective:** View a selected physiotherapist's future open appointment while signed out.  
**Preconditions:** Fresh browser; two seeded providers; a future open slot.  
**Test data:** Dr Asha Kulkarni; first displayed available slot (dynamic ID), displayed time parsed in Asia/Kolkata.

**Steps:**

1. Open `/physiotherapists`; wait for provider cards and assert at least two.
2. Find Dr Asha Kulkarni by the fixture name; click that card's `view-slots`.
3. Wait for slot cards; record the first card's slot ID and date/time.
4. Assert an IST label and a start instant later than the current time.
5. Assert no signed-in patient and the selected row's **Sign in to book** action; capture the page.

**Expected:** A future available slot for the chosen provider, with IST time and a sign-in prompt.  
**Actual result:** Two providers were visible; the selected provider offered a future IST slot with a sign-in prompt in the signed-out browser. Both the initial run and focused rerun passed on 3 October 2026.  
**Pass/fail:** ✅ PASS  
**Evidence:** `SEL02-available-slot.png`, metadata and Failsafe case `availableSlot`.

## SEL03 — Book appointment

**Method:** `PortalSeleniumIT.booking`  
**Objective:** Book an available slot and confirm that it disappears from availability.  
**Preconditions:** Fresh browser; newly registered patient; future free slot.  
**Test data:** `Selenium Demo booking`, `booking.<UUID>@example.test`; dynamic selected slot and returned appointment ID.

**Steps:**

1. Register the unique account and sign in.
2. Select Dr Asha Kulkarni and a future open slot; click its scoped `book-slot` button.
3. Wait for the appointment page; record URL and numeric appointment ID.
4. Assert identifier, **CONFIRMED**, selected provider/time and **Your appointment is confirmed.**
5. Capture confirmation, reopen availability and assert that the selected slot ID is absent.
6. Teardown cancels the fixture booking and asserts **CANCELLED** before quitting.

**Expected:** One confirmed appointment with correct details; its slot is removed from the open list and released by teardown.  
**Actual result:** Received a numeric CONFIRMED appointment with matching provider/time and notice; its slot disappeared from availability; teardown cancelled the fixture. Both the initial run and focused rerun passed on 3 October 2026.  
**Pass/fail:** ✅ PASS  
**Evidence:** `SEL03-booking.png`, metadata and Failsafe case `booking`.

## SEL04 — Cancel appointment

**Method:** `PortalSeleniumIT.cancellation`  
**Objective:** Cancel a future booking and verify the same slot returns to availability.  
**Preconditions:** Fresh browser; unique signed-in patient with a new future confirmed fixture booking.  
**Test data:** `Selenium Demo cancellation`, `cancellation.<UUID>@example.test`; dynamic slot/appointment IDs.

**Steps:**

1. Register, sign in and create a confirmed booking through the UI.
2. Click `cancel-appointment`; explicitly wait for **CANCELLED**.
3. Assert the cancellation/release notice, cancelled message and absence of the cancel button.
4. Capture cancelled status; revisit provider availability.
5. Assert the exact selected slot ID is visible again with the original time.
6. Teardown verifies cancelled state and quits.

**Expected:** Cancelled appointment, no cancel control, and the original slot available again.  
**Actual result:** Status changed to CANCELLED, the cancel control disappeared, and the exact selected slot/time returned to availability. Both the initial run and focused rerun passed on 3 October 2026.  
**Pass/fail:** ✅ PASS  
**Evidence:** `SEL04-cancellation.png`, metadata and Failsafe case `cancellation`.

## SEL05 — Appointment status/confirmation

**Method:** `PortalSeleniumIT.appointmentStatus`  
**Objective:** Confirm the booking remains in the patient's own list after signing out/in, with consistent details.  
**Preconditions:** Fresh browser; new account and future confirmed fixture booking.  
**Test data:** `Selenium Demo appointmentStatus`, `appointmentstatus.<UUID>@example.test`; exact returned appointment ID/provider/time.

**Steps:**

1. Register, sign in and book; verify initial confirmation details.
2. Sign out and wait for the logout success notice.
3. Sign in again with the same account; click `my-appointments`.
4. Find the row by its exact appointment ID; assert **CONFIRMED**, provider/time and detail link.
5. Capture the list; open the detail link and reassert the same ID, status and time.
6. Teardown cancels the fixture booking and closes the browser.

**Expected:** The same own confirmed appointment is visible after reauthentication; list/detail data agree.  
**Actual result:** After signing out/in, the own appointment list and detail page showed the same ID, CONFIRMED status and slot time; teardown cancelled the fixture. Both the initial run and focused rerun passed on 3 October 2026.  
**Pass/fail:** ✅ PASS  
**Evidence:** `SEL05-status.png`, metadata and Failsafe case `appointmentStatus`.

## Failure screenshot mechanism and diagnostic

`FailureScreenshotExtension` captures a PNG and metadata before browser cleanup for a test-body or setup failure, then rethrows the original exception. Cleanup failures also capture evidence; all teardown paths call `quit`. If browser startup fails before a driver exists, no screenshot can be taken; the original error remains in the report.

`ScreenshotCaptureProbeIT.intentionalFailure` is an explicitly enabled local diagnostic, excluded from the normal five-journey profile. It opens the home page and raises an intentional assertion failure. Expected diagnostic outcome: Maven exit 1, one failed case, and `intentionalFailure-failure.png` plus metadata. This does not count as a sixth user journey or Jenkins deployment-gate proof. Actual probe: one intentional assertion failure, Maven exit 1, and a 33,926-byte PNG; evidence is recorded in the Task 9 guide.

## Traceability and report authority

| Journey | Story | Test method | Required assertion |
|---|---|---|---|
| SEL01 | US-01 | registration | Created account can sign in |
| SEL02 | US-04 | availableSlot | Selected provider's future IST slot is visible |
| SEL03 | US-05, US-04 | booking | Confirmed ID/details; booked slot absent |
| SEL04 | US-07 | cancellation | Cancelled status; exact slot released |
| SEL05 | US-06 | appointmentStatus | Own list/detail agree after reauthentication |

Failsafe XML records whole-test outcomes including teardown. Screenshots show observed checkpoints and support that report; a screenshot alone cannot establish that all later assertions or cleanup passed. See [Task 9 execution guide](task-09-selenium.md) for commands and actual results.
