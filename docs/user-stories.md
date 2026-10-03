# Task 2 — User Stories and Acceptance Criteria

**Project:** Selenium Testing for a Physiotherapy Appointment Portal  
**Scope source:** [Approved Task 1 scope](problem-definition.md)  
**Owner:** Kirti Vispute  
**Planning date:** 2 October 2026

These stories describe outcomes. The specific screen and API design will be settled in Task 3. Test data will use fictional people and physiotherapists.

## US-01 — Patient registration

**Priority:** P0  
**Story:** As a patient, I want to create an account, so that my appointments are associated with me.

**Acceptance criteria**

1. A form accepts name, unique email, and password.
2. Blank required fields, malformed email, and passwords shorter than eight characters show clear errors without creating an account.
3. Registering an email already in use shows a clear error.
4. Valid registration creates one patient account and gives a clear success result.

**Story Definition of Done:** Registration works through the UI; passwords are stored as hashes; validation and duplicate-email tests pass; the registration Selenium journey passes; the behavior is documented.

## US-02 — Patient sign-in and sign-out

**Priority:** P0  
**Story:** As a registered patient, I want to sign in and out, so that only I can manage my bookings.

**Acceptance criteria**

1. Valid credentials start a patient session and show the signed-in state.
2. Invalid credentials show a general error without revealing whether an email exists.
3. Signing out ends the session; protected booking and appointment pages then require sign-in.
4. A patient cannot access another patient's appointment by changing an identifier.

**Story Definition of Done:** Session behavior and access checks work; automated valid/invalid login and ownership tests pass; no password or session secret appears in committed files.

## US-03 — View physiotherapists

**Priority:** P0  
**Story:** As a patient, I want to view physiotherapists, so that I can choose whom to visit.

**Acceptance criteria**

1. At least two fictional physiotherapists are shown with name and a short specialty or description.
2. Selecting one leads to that physiotherapist's available slots.
3. An empty list, if configuration changes, is explained rather than displayed as a broken page.

**Story Definition of Done:** Seed data and UI are present; list and navigation tests pass; the displayed names match the stored records.

## US-04 — View available slots

**Priority:** P0  
**Story:** As a patient, I want to see open appointment slots for a physiotherapist, so that I can select a suitable time.

**Acceptance criteria**

1. The selected physiotherapist's future open slots are shown with date and time in the clinic time zone, Asia/Kolkata.
2. A confirmed booking removes its slot from the open list.
3. When no slots are open, the page shows a clear empty state.
4. A slot belongs to exactly one physiotherapist.

**Story Definition of Done:** Slot filtering and rendering work; the available-slot Selenium journey passes; test coverage checks booked and empty states.

## US-05 — Book an appointment

**Priority:** P0  
**Story:** As a signed-in patient, I want to book one open slot, so that I have a confirmed physiotherapy appointment.

**Acceptance criteria**

1. A patient can select an open slot and submit one booking request.
2. Success creates one appointment with a confirmation identifier and `CONFIRMED` status.
3. A second request for the same occupied slot is rejected and does not create another active appointment.
4. A stale or invalid slot selection shows a clear error.

**Story Definition of Done:** Booking is atomic against duplicate active bookings; UI confirmation and persistence agree; booking Selenium and conflict tests pass.

## US-06 — View appointment confirmation and status

**Priority:** P0  
**Story:** As a patient, I want to view my appointments and their statuses, so that I know what was booked.

**Acceptance criteria**

1. A signed-in patient sees their own appointments with identifier, physiotherapist, slot, and status.
2. A new successful booking appears as `CONFIRMED`.
3. A cancelled booking appears as `CANCELLED`.
4. Another patient's appointments are not visible.

**Story Definition of Done:** UI and stored status agree; ownership tests pass; status/confirmation Selenium journey passes.

## US-07 — Cancel an appointment

**Priority:** P0  
**Story:** As a patient, I want to cancel my future appointment, so that the slot becomes available again.

**Acceptance criteria**

1. The owner can cancel a future `CONFIRMED` appointment.
2. The status changes to `CANCELLED`, and the associated slot returns to the available list.
3. Repeating cancellation does not create another change or an error page.
4. Another patient cannot cancel it; a past appointment cannot be cancelled.

**Story Definition of Done:** Cancellation and slot release work; authorization and boundary tests pass; cancellation Selenium journey passes.

## US-08 — Clear validation and failure handling

**Priority:** P0  
**Story:** As a patient, I want understandable errors, so that I can correct my input or choose another slot.

**Acceptance criteria**

1. Required-field and format errors identify the field to fix.
2. An occupied or invalid slot is rejected without a duplicate booking.
3. Unauthorized access is redirected to sign-in or receives a suitable error response.
4. Expected user errors do not expose stack traces or database details in the UI.

**Story Definition of Done:** The named errors have automated tests; messages are readable; application logs still retain useful diagnostics without sensitive values.

## US-09 — Continuous integration feedback

**Priority:** P0  
**Story:** As a student developer, I want commits checked automatically, so that defects are found before deployment.

**Acceptance criteria**

1. Jenkins checks out the GitHub repository and runs Maven build, unit tests, and packaging.
2. Selenium tests execute against a started application and publish a report.
3. A deliberate failing Selenium test blocks the deploy stage; the corrected commit passes.
4. A successful run archives the build artifact and records its commit.

**Story Definition of Done:** Jenkins configuration is versioned; both real failure and success logs, test report, trigger, and artifact are saved as evidence.

## US-10 — Versioned container deployment

**Priority:** P0  
**Story:** As a student developer, I want a tested versioned image deployed automatically, so that the running demo matches a known release.

**Acceptance criteria**

1. A passing pipeline builds an image with a release or commit-based tag and pushes it to a registry.
2. Jenkins replaces the previous container with the new version and checks HTTP health.
3. The deployed version, image ID, container ID, and port mapping can be identified.
4. A failed test or failed health check is reported as a failed deployment.

**Story Definition of Done:** Dockerfile and pipeline are versioned; registry, deployment, logs, and health evidence are captured; `latest` is not the sole release identifier.

## US-11 — Repeatable provisioning and recovery

**Priority:** P0  
**Story:** As a student operator, I want repeatable configuration and rollback, so that I can recover a broken local deployment.

**Acceptance criteria**

1. Ansible configures the documented Linux target, folders, files, and service/container configuration.
2. The first and second playbook runs finish with `failed=0`; the second avoids unnecessary changes.
3. A bad release is demonstrated without corrupting the known good image.
4. Restoring the known good version results in HTTP 200 from the health endpoint.

**Story Definition of Done:** Inventory, playbook, and run instructions are versioned; two real run recaps, bad-release evidence, rollback log, and recovered health result are saved.

## Story completion rule

An acceptance criterion is checked only against actual implementation and evidence. US-01–US-08 functionality is implemented and verified through Tasks 5–6: 34 integration tests, actual browser/API checks, and file-database persistence. US-01–US-08 story DoD are now verified: Task 9 passed all five required Selenium journeys twice and preserved XML/HTML, fixture cleanup and screenshots in addition to the 34 backend tests. US-09 Definition of Done is verified across Tasks 7–10: automatic SCM CI trigger, versioned Jenkins pipeline, locally and Jenkins-run Selenium/reporting, an actual failing browser assertion that skipped Deploy, a correction commit, successful rerun and archived artifact. US-10 is in progress: Task 11 verified the versioned local image, image/container IDs, port mapping, Docker lifecycle, logs and health; registry push and Jenkins container deployment remain Task 12. US-11 remains planned for Ansible and reliability tasks. See `docs/task-11-docker.md` and `docs/evidence/T11_docker_lifecycle.txt`. The approved functionality and frozen scope are unchanged.
