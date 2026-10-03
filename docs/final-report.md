# Final project report

**Title:** Selenium Testing for a Physiotherapy Appointment Portal  
**Student:** Kirti Vispute (23102C0078)  
**Date:** 3 October 2026  
**Repository:** [kirti-vispute/physiotherapy-appointment-portal](https://github.com/kirti-vispute/physiotherapy-appointment-portal)

This report is an entry point to the versioned source, executed checks, diagrams, and saved evidence. The [line-by-line audit](final-audit.md), [evidence index](final-evidence-index.md), and [16-part viva sequence](final-demo.md) make each claim inspectable.

## 1. Abstract

This project builds a small patient portal for finding a physiotherapist and booking, tracking, and cancelling an appointment. Its DevOps path uses GitHub, Maven/JUnit, Selenium, Jenkins, Docker, a local registry, Ansible, health checks, and a demonstrated recovery. The final local check ran **34 backend and 5 browser cases with zero failures**; local deployments on ports 8082, 8087, and 8089 all returned health `UP` and HTTP 200 for the home and provider pages. [Raw final output](evidence/T15_final_maven_verify.txt) and [live checks](evidence/T15_live_checks.json) support these results.

## 2. Introduction

Phone or message-based appointment requests are difficult for a small clinic to track. The project demonstrates a working booking workflow and the repeatable steps needed to test and deploy it. It is a local college demonstration using free tools and services; it is not a public clinical system.

## 3. Problem definition

The [approved problem document](problem-definition.md) records the real-world motivation, pain points, users, constraints, assumptions, and frozen MVP boundary. Kirti approved this scope in the project conversation on 2 October 2026.

## 4. Objectives

The objectives are to give patients a clear available-slot and appointment workflow, prevent conflicting bookings, and show a complete build, test, package, deployment, health, and recovery path. Measurable criteria are in the [problem document](problem-definition.md).

## 5. Scope

The frozen MVP includes registration, sign-in, provider and slot views, booking, confirmation/status, cancellation, and validation. Payments, clinical records, clinician accounts, and a complex administration dashboard are excluded.

## 6. Stakeholders

Patients use the portal; physiotherapists are represented by fictional seed data; the clinic/demo operator maintains the local environment; the student, reviewer, and Jenkins operator observe quality and deployment evidence. See [scope](problem-definition.md) and [SRS](srs.md).

## 7. Requirements

The [SRS](srs.md) defines actors, functional and non-functional requirements, use cases, constraints, assumptions, and traceability. The [API contract](api-documentation.md) gives actual pages and JSON routes.

## 8. User stories

The [11 stories](user-stories.md) each have an ID, priority, acceptance criteria, and story Definition of Done. They cover the patient workflow and delivery work.

## 9. Product backlog

The [backlog](backlog.md) maps the project work to priorities, dependencies, and verified outcomes.

## 10. Agile/Scrum plan

The [15-task board and five milestone sprints](agile-plan.md) identify one owner, dependencies, deliverables, and acceptance gates. Status is evidence-based rather than a calendar claim.

## 11. Definition of Done

The [project-level Definition of Done](agile-plan.md#project-level-definition-of-done) requires accepted behavior, relevant passing tests, reviewed code where required, reproducible commands, and saved evidence.

## 12. Technology stack

The portal uses Java 21, Spring Boot 4.0.8 with embedded Tomcat, Thymeleaf, Spring Security, Spring Data JPA, H2, and Maven. Tests use JUnit and Selenium WebDriver with Chrome. Git/GitHub, local Jenkins, Docker Desktop, a local registry, Ubuntu WSL, and Ansible complete the delivery path. There is no paid cloud dependency. See the [architecture](architecture.md) and [setup guide](local-setup.md).

## 13. System architecture

The browser calls Spring MVC pages and REST APIs; services enforce booking and ownership rules; repositories persist to H2. GitHub feeds Jenkins on Windows. Jenkins builds and tests, then publishes a versioned image to the local registry and deploys a Docker Desktop container. A separate Ubuntu WSL Docker Engine is configured and deployed by Ansible. See the [verified architecture](architecture.md#verified-delivery-architecture) and [seven final diagrams](final-diagrams.md).

## 14. Use-case diagram

[Diagram 1](final-diagrams.md#1-use-cases) shows visitor, patient, operator, and CI actions. A physiotherapist is a represented stakeholder, not an interactive account in this MVP.

## 15. Data model

The [ER diagram and table specification](architecture.md#data-model) cover patient, physiotherapist, slot, and appointment IDs, attributes, keys, and relationships. A transaction and slot lock prevent two active bookings of one slot.

## 16. API documentation

The [API guide](api-documentation.md) lists methods, paths, purpose, requests, responses, and status codes. The browser pages and API use the same validated service rules.

## 17. Git workflow

The [Git guide](git-workflow.md) records meaningful commits, status/add/commit/push/log commands, PR review, conflict resolution, and the `v1.0.0` tag. A real conflict and its resolution were retained in [Task 6 evidence](task-06-mvp.md).

## 18. GitHub repository

The [public repository](https://github.com/kirti-vispute/physiotherapy-appointment-portal) has `main` and `develop`, issue/PR templates, source, deployment configuration, docs, and screenshots. PRs #1–#9 have review and merge records in the [tracker](project-tracker.md); single-contributor COMMENT self-reviews are not independent peer approval.

## 19. Branching strategy

`feature/<name>` branches enter `develop` through focused PRs; `main` holds the tagged Task 6 release baseline. [Diagram 4](final-diagrams.md#4-git-branching-and-release) and the [Git guide](git-workflow.md) show the actual scheme.

## 20. CI/CD architecture

The [CI/CD diagram](final-diagrams.md#3-cicd-pipeline) follows the committed [Jenkinsfile](../Jenkinsfile): checkout, Maven build and backend tests, package, temporary app and Selenium gate, report, Docker build/tag/push, container replacement, and health. A deliberately failing browser test skipped deployment in [Task 10](task-10-continuous-testing.md).

## 21. Jenkins setup

The [Task 7 guide](task-07-jenkins-ci.md) records the existing local Windows Jenkins service, Java/Maven/Git tools, plugin verification, public GitHub checkout, freestyle CI job, archived artifact, and actual Poll SCM build #2.

## 22. Jenkins pipeline

The versioned [Jenkinsfile](../Jenkinsfile) is parameterized by `APP_ENV` and `DOCKER_PORT`. [Task 8](task-08-pipeline-deployment.md) verified five initial stages; [Tasks 10](task-10-continuous-testing.md) and [12](task-12-docker-cd.md) extended them with Selenium and Docker stages. The saved Task 12 pipeline job had no automatic SCM trigger; its verified build #9 was started manually. The separate Task 7 freestyle job demonstrated automatic Poll SCM.

## 23. Selenium test plan

The [five-case plan](selenium-test-plan.md) specifies registration, slot view, booking, cancellation, and status with inputs, steps, expected result, and actual result.

## 24. Selenium implementation

The [Task 9 guide](task-09-selenium.md) identifies real Chrome WebDriver classes, stable selectors, explicit waits, assertions, teardown, a failure screenshot probe, and XML/HTML reports. The [final Maven log](evidence/T15_final_maven_verify.txt) recorded five passes after 34 backend passes.

## 25. Docker implementation

The root [Dockerfile](../Dockerfile) packages the tested Java 21 JAR; app configuration uses environment variables and a named H2 data volume. [Diagram 5](final-diagrams.md#5-docker-deployment) shows the actual registry, engine, mapping, and volume.

## 26. Docker lifecycle

[Task 11](task-11-docker.md) records a versioned image, image and container IDs, logs, port mapping, stop/start/restart/remove, and the final healthy local container.

## 27. Jenkins–Docker deployment

[Task 12 build #9](task-12-docker-cd.md) passed 39 tests, pushed tag `1.0.0-b9-180b726bc020` to the loopback registry, removed the old owned container, and started a healthy new one at port 8087. Saved logs identify each stage and the image digest.

## 28. Ansible configuration management

The [Task 13 guide](../ansible/README.md) explains each inventory, variable, template, and playbook task. Its first real run configured Ubuntu WSL prerequisites, Docker, service account, folder, and environment file with `ok=10 changed=5 failed=0`.

## 29. Provisioning

The [Task 14 site playbook](../ansible/site.yml) imports configuration and deployment. Its first run pulled the pinned image into Ubuntu's separate Docker Engine, created a named volume, ran the app at port 8089, and checked health and pages; `ok=16 changed=4 failed=0`.

## 30. Idempotency

The [second full Ansible run](evidence/T14_provision_second.txt) reported `ok=16 changed=0 failed=0`; the container was not replaced unnecessarily.

## 31. Health check

The app exposes `/actuator/health`. The [final live check](evidence/T15_live_checks.json) recorded `UP` and HTTP 200 for home and providers on ports 8082, 8087, and 8089 at the recorded time. These localhost URLs require the relevant local services to be running.

## 32. Rollback and recovery

[Task 14](task-14-provisioning.md) simulated a bad release **configuration** by setting app port 8099 while mapping to 8080. Ansible failed the health gate. Reapplying the good configuration replaced the bad container; the pinned image and named H2 volume remained intact, and health/pages returned 200.

## 33. Screenshots

The [evidence index](final-evidence-index.md) identifies the exact view, command/action, expected result, and saved screenshot or log for each task. Files in [screenshots](../screenshots) are actual captured or documented renderings of real output, with their provenance in each task guide.

## 34. Results

The final [Maven run](evidence/T15_final_maven_verify.txt) ended `BUILD SUCCESS`: 34 JUnit integration tests plus five browser tests, zero failures/errors/skips. The [live check](evidence/T15_live_checks.json) confirmed three local deployments at the stated time. Earlier Jenkins build #9 and registry/container evidence remain in [Task 12](task-12-docker-cd.md). A fresh Jenkins API query during this audit returned HTTP 403, so no new Jenkins build result is claimed.

## 35. Challenges

The work encountered Windows Jenkins process lifetime behavior, a deliberately failing Selenium assertion, Docker Desktop engine access, a separate Ubuntu WSL engine that stopped without a keeper process, and the intentional bad port. Browser/driver version mismatch emitted a CDP compatibility warning during the final run, but the five browser tests passed.

## 36. Solutions

The [Task 8 launcher](task-08-pipeline-deployment.md) detached the local Java process; [Task 10](task-10-continuous-testing.md) corrected the browser assertion and proved deploy gating; [Task 11](task-11-docker.md) documented engine recovery; the [Task 14 helper](../scripts/start-ansible-target.ps1) keeps Ubuntu WSL active during the demonstration; rerunning the versioned Ansible settings restored port 8080 and health.

## 37. Conclusion

The frozen patient MVP and the required local DevOps demonstrations have verifiable source, execution logs, screenshots, and final checks. The separate CI trigger and Docker deployment pipeline should be shown honestly as two Jenkins jobs during a viva. The [audit](final-audit.md) records the remaining presentation limitation rather than presenting a manual pipeline start as automatic.

## 38. Future scope

Possible future work includes a clinician-facing schedule, email reminders, HTTPS for a public environment, external database backups, and an automatic trigger on the Docker deployment pipeline. These are outside the approved MVP and are not claimed as implemented.
