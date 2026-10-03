# Task 2 — Product Backlog

**Scope source:** [Approved Task 1 scope](problem-definition.md)  
**Story details:** [User stories](user-stories.md)  
**Owner:** Kirti Vispute  
**Updated:** 3 October 2026

Priority: **P0** is required for submission. The sequence is controlled by dependencies and the [15-task board](agile-plan.md), not priority alone. A backlog item is complete only when its acceptance criteria and evidence are verified.

| ID | Backlog item | Priority | Related stories | Task | Planned sprint | Status |
|---|---|---|---|---|---|---|
| PB-01 | Define and freeze problem, stakeholders, success criteria, and MVP scope | P0 | All | 01 | 0 | ✅ Approved |
| PB-02 | Plan stories, acceptance criteria, board, sprints, DoD, lifecycle | P0 | US-01–US-11 | 02 | 0 | ✅ Complete |
| PB-03 | Design SRS, use cases, data model, APIs, and local setup | P0 | US-01–US-08 | 03 | 0 | ✅ Complete |
| PB-04 | Initialize GitHub repository, templates, branches, and README | P0 | US-09–US-11 | 04 | 1 | ✅ Complete |
| PB-05 | Implement registration on a branch and review PR | P0 | US-01 | 05 | 1 | ✅ Complete; PR #1 merged |
| PB-06 | Finish sign-in, physiotherapist, slot, booking, status, and cancellation flows; conflict and tag | P0 | US-02–US-08 | 06 | 1 | ✅ Complete; PR #2 merged, v1.0.0 published |
| PB-07 | Configure Jenkins checkout, build, unit tests, package, archive, trigger | P0 | US-09 | 07 | 2 | ✅ Builds #1/#2, 34 tests, archive/fingerprint and SCM trigger verified |
| PB-08 | Write parameterized pipeline and deploy runnable JAR | P0 | US-09 | 08 | 2 | ✅ Pipeline #2/#3, 34 tests, test/8082 deployment/health/hash, PR #3 verified |
| PB-09 | Implement five Selenium journeys, data, report, screenshot on failure | P0 | US-01, US-04–US-07 | 09 | 2 | ✅ Five cases passed twice; 34 backend tests, HTML/XML and failure capture verified |
| PB-10 | Gate deployment on Selenium; demonstrate failure and corrected rerun | P0 | US-09 | 10 | 2 | ✅ Jenkins #4 failed/skipped Deploy; correction #5 and merged #6 passed 39 tests/deployed; PR #5 reviewed/merged |
| PB-11 | Build versioned Docker image and demonstrate lifecycle commands | P0 | US-10 | 11 | 3 | ✅ Image, IDs, mapping, logs, lifecycle, health and screenshot verified |
| PB-12 | Publish image and deploy new container from passing Jenkins pipeline | P0 | US-10 | 12 | 3 | ⬜ |
| PB-13 | Specify configuration and create Ansible inventory/playbook | P0 | US-11 | 13 | 3 | ⬜ |
| PB-14 | Provision target, prove idempotency, health, rollback, and recovery | P0 | US-11 | 14 | 3 | ⬜ |
| PB-15 | Audit every requirement and prepare final documentation and viva sequence | P0 | All | 15 | 4 | ⬜ |

## Backlog update rule

After each task, update its status and evidence in [the project tracker](project-tracker.md). If a story or requirement changes, check it against the frozen Task 1 scope and record a scope change before changing this backlog. Do not mark a future task complete because its file exists; retain execution evidence where the assignment requires it.
