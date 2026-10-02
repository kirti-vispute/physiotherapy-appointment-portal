# Task 1 — Problem Definition and Scope

**Project:** Selenium Testing for a Physiotherapy Appointment Portal  
**Student:** Kirti Vispute (23102C0078)  
**Document status:** Approved; MVP scope frozen  
**Date:** 2 October 2026

## Problem statement

A small physiotherapy clinic may arrange appointments through calls or messages. Patients then have no single place to see open times, confirm a booking, check its status, or cancel it. Clinic staff must reconcile requests manually, which can cause slow replies and conflicting bookings. This project will demonstrate a simple self-service appointment workflow and the DevOps practices used to build, test, and deploy it reliably.

This is a project motivation, not a claim that a particular clinic was surveyed or that a measured industry-wide problem exists.

## Real-world motivation and existing pain points

| User need | Current manual-process pain point | Intended MVP response |
|---|---|---|
| Find a physiotherapist and time | Availability must be requested by phone or message | Show seeded physiotherapists and open slots |
| Book an appointment | Replies may be delayed or duplicated | Submit one booking and reserve its slot |
| Know whether the booking succeeded | A message may be missed or ambiguous | Show confirmation and a persistent status |
| Cancel an appointment | Staff must manually update their list | Let the patient cancel and release the slot |
| Avoid conflicting bookings | Separate messages can request the same time | Reject a second booking for an occupied slot |

## Target users and stakeholders

| Stakeholder | Role or interest | MVP access |
|---|---|---|
| Patient | Registers, signs in, finds a slot, books, checks status, cancels | Direct web access to their own appointments |
| Physiotherapist | Needs an accurate schedule | Represented by a read-only profile and seeded availability; no clinician login in the MVP |
| Clinic receptionist or owner | Needs fewer manual booking conflicts and a demonstrable workflow | Reviews the demo and its records; no separate admin dashboard in the MVP |
| Student developer (Kirti Vispute) | Builds, operates, tests, and documents the project | Repository, CI/CD, and local environment |
| Project evaluator | Verifies the required 15-task lifecycle and evidence | Documentation, screenshots, logs, and live demonstration |

The clinic and its users are representative personas for the assignment. No real patient or clinic data is required.

## Objectives

1. Let a patient create an account and sign in so bookings belong to a known user.
2. Show physiotherapists and open appointment slots.
3. Book one available slot, display confirmation, and let the patient view its status.
4. Let the patient cancel their own booking and make that slot available again.
5. Validate required fields, invalid credentials, and unavailable slots with clear messages.
6. Demonstrate the complete Git → Jenkins → tests → Docker → Ansible → health check → rollback workflow with actual evidence.

## Measurable success criteria

These are acceptance targets, not results already achieved.

| ID | Criterion | How it will be verified |
|---|---|---|
| SC-01 | A new patient can register and sign in with valid inputs; invalid inputs are rejected | Application and automated tests |
| SC-02 | A patient can see at least two seeded physiotherapists and at least one open slot | UI/API inspection and Selenium test |
| SC-03 | Booking an open slot creates one appointment with a visible confirmation and status | UI/API inspection and Selenium test |
| SC-04 | A slot cannot have two active bookings | Automated concurrency or repeated-booking test |
| SC-05 | A patient can cancel their own appointment; the status becomes `CANCELLED` and its slot can be booked again | UI/API inspection and Selenium test |
| SC-06 | The five required browser journeys pass locally and in Jenkins before deployment | Maven/Jenkins test reports |
| SC-07 | A failing Selenium test prevents the Jenkins deployment stage; a corrected test allows it | Two actual Jenkins runs and console logs |
| SC-08 | A versioned Docker image is published and the deployed container answers the health endpoint with HTTP 200 | Registry, Docker, and HTTP output |
| SC-09 | Ansible provisions the documented target; a second run reports `failed=0` with no unnecessary resource changes | Two Ansible recaps |
| SC-10 | A simulated bad release is rolled back to a known good version and the health check returns HTTP 200 | Deployment and rollback log |
| SC-11 | Every required deliverable is linked to real evidence in the final 15-task audit | Final audit review |

## Constraints

- This is a small local college demonstration on a Windows 11 computer, with Ubuntu in WSL 2 where Linux tooling is needed.
- Prefer Java 21, Spring Boot, Maven, Thymeleaf, H2, JUnit, Selenium, Git/GitHub, Jenkins, Docker, and Ansible.
- The app uses Spring Boot's embedded Tomcat. An external Tomcat or Nginx instance is not required for the proposed architecture.
- Browser tests and Jenkins require an installed, accessible browser and adequate machine memory.
- GitHub and image-registry publication require accounts, credentials, and network access; they cannot be claimed complete until verified.
- Docker Desktop and WSL integration are host prerequisites. The Linux Ansible playbook will configure its documented Linux target, not install Docker Desktop on Windows.
- The demo uses fictional names and test accounts; no patient health records or sensitive medical information are stored.
- H2 file data needs a persistent path or Docker volume; schema changes across rollback versions must remain compatible for this demonstration.

## Approved assumptions

1. One patient account owns each appointment. A patient can see and cancel only their own appointments.
2. Physiotherapists and availability are seeded test data. They do not sign in or edit their schedules in the MVP.
3. Slots use one local clinic time zone: Asia/Kolkata. A slot has a fixed start and end time.
4. A successful booking is confirmed immediately; there is no clinician approval queue.
5. Cancellation is allowed before the slot starts. Past appointments are read-only.
6. The project is demonstrated on local infrastructure; a public production website is outside scope.
7. Task 15 means final validation, as named in the brief's audit and tracker; detailed task descriptions were provided for Tasks 1–14.

## Proposed frozen MVP scope

**Scope decision:** Approved by Kirti Vispute on 2 October 2026. This is the frozen MVP scope. Changes to this list require an explicit scope update in this document and the backlog.

### In scope — application

- Patient registration and sign-in/sign-out.
- Read-only physiotherapist list and seeded available slots.
- Select a slot, submit a booking, and receive a confirmation identifier.
- View one's own appointment status and cancel an eligible appointment.
- Basic input validation, access control, conflict handling, and understandable error messages.
- Small responsive pages sufficient for a live college demonstration.
- H2-backed persistence and a health endpoint.

### In scope — 15-task DevOps demonstration

| Task | Included output |
|---|---|
| 01 | Problem definition, stakeholders, constraints, objectives, criteria, approved scope |
| 02 | User stories, acceptance criteria, backlog, board, sprint plan, Definition of Done, lifecycle |
| 03 | SRS, use cases, architecture, data model, APIs, local setup |
| 04 | GitHub repository, README, ignore rules, issue templates, branch policy, initial commits |
| 05 | First feature branch, PR, review, merge, evidence |
| 06 | Complete MVP, second branch, intentional conflict and resolution, release tag |
| 07 | Jenkins CI job, GitHub checkout, Maven build/test/package, archived artifact, trigger |
| 08 | Parameterized Jenkinsfile, pipeline run, executable-JAR deployment and URL |
| 09 | Five Selenium journeys, test data, assertions, failure screenshot support, local report |
| 10 | Jenkins Selenium gate, deliberate failure, correction commit, successful rerun |
| 11 | Dockerfile, versioned image, container lifecycle and port/log evidence |
| 12 | Registry publication and Jenkins-driven Docker deployment after passing tests |
| 13 | Minimal Ansible inventory, configuration specification, playbook, first run |
| 14 | Provisioning, second-run idempotency, health check, rollback and recovery |
| 15 | Full documentation, evidence audit, and viva/demo sequence |

### Out of scope

- Payments, insurance claims, prescriptions, medical records, video calls, and chat.
- Physiotherapist self-service or complex clinic administration.
- Multi-clinic scheduling, recurring appointments, waitlists, reminders, and email/SMS integration.
- Public production hosting, high availability, disaster recovery across machines, and compliance certification.
- Real patient data or medical advice.

## Approval and change record

| Date | Decision | By | Status |
|---|---|---|---|
| 2 October 2026 | Initial Task 1 proposal created | Project assistant | Awaiting Kirti's review |
| 2 October 2026 | Approved Task 1 scope and assumptions in chat | Kirti Vispute | Approved; scope frozen |

**Task 1 completion evidence:** Kirti's explicit “Approve Task 1 scope” message and this updated approval record. All Task 1 sections are present.
