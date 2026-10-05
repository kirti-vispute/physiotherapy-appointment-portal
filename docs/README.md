# Project documentation index

**Updated:** 5 October 2026

**Current version:** [`develop`](https://github.com/kirti-vispute/physiotherapy-appointment-portal/tree/develop). The default `main` and `v1.0.0` preserve the Task 6 release baseline.

The patient MVP and all T1–T15 deliverables have saved evidence. Later updates added the professional interface, corrected clinical illustration and 13 more fictional providers (15 total). [Pipeline #23](evidence/Seed_expansion_pipeline23.json) passed 34 backend and five browser tests; [5 October live verification](evidence/Seed_expansion_live.json) confirmed 15 cards, 90 available slots at that time and health UP. Localhost links work only while their local service runs.

## Current project guides

| Topic | Read |
|---|---|
| Overview, quick start and configuration | [Repository README](../README.md) |
| Build/run, ports, persistence, restart and prerequisites | [Local setup](local-setup.md) |
| Requirements and acceptance | [SRS](srs.md), [approved scope](problem-definition.md) |
| Design and data model | [Architecture](architecture.md), [seven diagrams](final-diagrams.md) |
| Routes, CSRF, sessions and errors | [API contract](api-documentation.md) |
| Theme and clinical image revisions | [UI refinement](ui-refresh.md) |
| All 15 fictional doctors and startup behavior | [Seed data](seed-data.md) |
| Browser journeys and fixtures | [Selenium test plan](selenium-test-plan.md) |
| Git, branches, reviews, conflict and releases | [Git workflow](git-workflow.md) |
| Patient demonstration | [MVP demo](mvp-demo.md) |

## Planning and completion

- [User stories](user-stories.md), [prioritized backlog](backlog.md), [agile lifecycle and sprints](agile-plan.md).
- [Project tracker](project-tracker.md) and [15-task requirement audit](final-audit.md).
- [38-topic final report](final-report.md), [evidence index](final-evidence-index.md), [16-part viva sequence](final-demo.md).

## T1–T15 task records

These guides preserve what was executed at each milestone. Original provider counts, ports, screenshots, image tags and build numbers describe the version tested then. Use the current guides above for today's setup and the post-task addenda for newer changes.

| Task | Record |
|---|---|
| T1 — Problem definition | [Approved scope](problem-definition.md) |
| T2 — Agile planning | [Agile plan](agile-plan.md), [stories](user-stories.md), [backlog](backlog.md) |
| T3 — Architecture/setup | [SRS](srs.md), [architecture](architecture.md), [API](api-documentation.md), [setup/original verification](local-setup.md) |
| T4 — Git/GitHub | [Git workflow/publication](git-workflow.md) |
| T5 — Registration | [Implementation/checks](task-05-registration.md) |
| T6 — Patient MVP/collaboration | [MVP, conflict and release](task-06-mvp.md) |
| T7 — Jenkins CI | [Tools, jobs and automatic trigger](task-07-jenkins-ci.md) |
| T8 — Original pipeline | [JAR pipeline/deployment](task-08-pipeline-deployment.md) |
| T9 — Selenium | [Execution, report and failure capture](task-09-selenium.md) |
| T10 — Continuous testing | [Browser gate/corrected builds](task-10-continuous-testing.md) |
| T11 — Docker | [Image, lifecycle and recovery](task-11-docker.md) |
| T12 — Docker CD | [Registry image/replacement](task-12-docker-cd.md) |
| T13 — Ansible | [Configuration](../ansible/README.md) |
| T14 — Provisioning/reliability | [Idempotency, bad port and recovery](task-14-provisioning.md) |
| T15 — Final validation | [Audit](final-audit.md), [report](final-report.md), [diagrams](final-diagrams.md), [evidence](final-evidence-index.md), [viva](final-demo.md) |

## Documentation review

The 5 October review corrected stale current summaries, expanded setup instructions and checked relative Markdown links and report/task coverage. [Review results](evidence/Documentation_review.json) record the checks. Application source and historical execution evidence were retained; the application result remains the recorded 39 passes from build #23.
