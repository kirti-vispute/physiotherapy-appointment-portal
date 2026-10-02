# Task 3 — Software Requirements Summary

**Project:** Selenium Testing for a Physiotherapy Appointment Portal  
**Scope:** [Approved Task 1 MVP](problem-definition.md)  
**Stories:** [Task 2 user stories](user-stories.md)  
**Status:** Design specification; patient features are planned, not yet implemented.

## Purpose and system boundary

The portal lets a registered patient find an open physiotherapy slot, book it, see confirmation and status, and cancel it. One Spring Boot application serves browser pages and REST endpoints. H2 stores local demo data. Jenkins, Docker, and Ansible belong to the delivery system around the application.

## Actors

| Actor | Actions | Access boundary |
|---|---|---|
| Visitor | View landing page, register, sign in, and browse physiotherapists/open slots | Cannot book or view appointments |
| Patient | Browse, book, see own appointments, cancel own future bookings, sign out | Cannot see or change another patient's bookings |
| Demo operator | Supplies fictional physiotherapists and slots through seed/configuration data | No administrative UI or live editing in the MVP |
| Physiotherapist | Stakeholder whose name and slots are shown | No direct login or software action in this MVP |
| CI/operator | Builds, tests, deploys, configures, and health-checks the app | Works through Jenkins/Docker/Ansible, not patient pages |

## Functional requirements

| ID | Requirement | Related story | Acceptance basis |
|---|---|---|---|
| FR-01 | Register a patient with name, unique email, and password | US-01 | Valid account created; malformed/duplicate data rejected |
| FR-02 | Authenticate and end a patient session | US-02 | Valid login works; invalid login and protected access are handled |
| FR-03 | List at least two seeded physiotherapists | US-03 | Names and descriptions visible |
| FR-04 | List future open slots for a selected physiotherapist | US-04 | Correct provider, local date/time, and empty state |
| FR-05 | Book one open slot for the signed-in patient | US-05 | One appointment and confirmation; occupied slot rejected |
| FR-06 | Show the patient's appointment identifiers and statuses | US-06 | `CONFIRMED` and `CANCELLED` shown correctly; ownership enforced |
| FR-07 | Cancel an eligible own appointment and release its slot | US-07 | Status changes; past or other-user cancellation denied |
| FR-08 | Return understandable validation and conflict errors | US-08 | No stack traces or database detail shown to users |
| FR-09 | Provide a health endpoint for deployment checks | US-10, US-11 | HTTP 200 and `UP` when healthy |

## Non-functional requirements

| ID | Requirement | Verification approach |
|---|---|---|
| NFR-01 | Avoid duplicate active bookings for one slot | Transaction and repeated-booking test; database state check |
| NFR-02 | Keep patient passwords hashed and restrict appointment access to its owner | Code review and authorization tests |
| NFR-03 | Give clear form errors and stable `data-testid` or ID selectors | Manual review and Selenium tests |
| NFR-04 | Build with Java 21/Maven and run as one executable JAR | `mvn clean package`, JAR startup |
| NFR-05 | Make server port and database path configurable | Environment override test; documented defaults |
| NFR-06 | Preserve local H2 data across application restart when using a persistent path | Restart and record check in later task |
| NFR-07 | Pass five browser journeys locally and in Jenkins before container deployment | Test reports and gated pipeline |
| NFR-08 | Permit health checks and versioned rollback without real patient data | Actuator HTTP response and deployment evidence |

## Use cases

| ID | Primary actor | Preconditions | Main outcome | Failure/alternate path |
|---|---|---|---|---|
| UC-01 Register | Visitor | Email unused | Patient account created | Invalid input or duplicate email shown |
| UC-02 Sign in | Visitor | Registered account | Patient session created | Invalid credentials rejected |
| UC-03 Browse | Visitor or patient | Seed data present | Physiotherapists and open slots shown | Empty list explained |
| UC-04 Book | Patient | Signed in; future open slot | Appointment `CONFIRMED` and slot occupied | Stale/occupied slot rejected |
| UC-05 Track | Patient | Signed in | Own appointments and statuses shown | Another patient's ID hidden |
| UC-06 Cancel | Patient | Own future confirmed appointment | Status `CANCELLED`, slot open | Past/foreign/invalid appointment denied |
| UC-07 Health check | CI/operator | App started | HTTP 200 `UP` | Non-200 stops deployment |

## Constraints and assumptions

- Windows 11 is the host; WSL Ubuntu may host Linux CI/Ansible tooling. Docker Desktop is a separate Windows prerequisite.
- Java 21, Maven, Spring Boot 4, Thymeleaf, H2, Selenium/JUnit, GitHub, Jenkins, Docker, and Ansible are the agreed technologies.
- Time is displayed in `Asia/Kolkata`; exact persistence type and formatting will be fixed in the entity implementation.
- A booking is confirmed immediately. There is no therapist approval queue or admin dashboard.
- Seeded physiotherapists and slots are fictional. The portal stores no treatment notes, records, or payments.
- A cancelled appointment remains as history while its slot can be booked again.
- Task 3 implements only a runnable skeleton and health check. FR-01–FR-08 are design commitments for Tasks 5 and 6.

## Acceptance and traceability

Task 3 is complete when this SRS, the use-case and architecture diagrams, data model, API contract, technology choices, and exact local setup exist, and the skeleton builds and answers its home and health endpoints. Feature-level acceptance remains scheduled for Tasks 5 and 6.
