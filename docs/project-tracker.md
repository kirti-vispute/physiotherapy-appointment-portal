# Project state tracker

**Project:** Selenium Testing for a Physiotherapy Appointment Portal  
**Owner:** Kirti Vispute (23102C0078)  
**Updated:** 2 October 2026

Status key: ⬜ Not started · 🟡 In progress · ✅ Completed with evidence · ❌ Needs correction

| Task | Name | Status | Evidence or remaining gate |
|---|---|---|---|
| 01 | Problem Definition | ✅ | Kirti approved scope in chat on 2 October 2026; `docs/problem-definition.md` records frozen scope |
| 02 | Agile Planning | ✅ | `user-stories.md`, `backlog.md`, and `agile-plan.md` created; 11 stories and 15 task rows verified |
| 03 | Architecture | ✅ | SRS, diagrams, model, API contract, and setup created; Maven build, home/health, H2, and screenshot verified |
| 04 | Git/GitHub | ✅ | Verified repository URL, pushed `main`/`develop`, README/templates/policy, initial commits, remote log and GitHub screenshots |
| 05 | Feature Development | ✅ | Registration UI/API/persistence and 14 tests verified; feature commits, PR #1, COMMENT self-review, merge `f7713f0`, Git logs and screenshots saved |
| 06 | MVP Collaboration | ✅ | Full MVP, 34 passing tests, UI/API/restart checks, real conflict resolution, reviewed/merged PR #2, annotated v1.0.0 and main baseline verified |
| 07 | Jenkins CI | ✅ | Existing Jenkins 2.568.1, active plugins/tools, GitHub checkout, builds #1/#2 SUCCESS with 34 tests; downloaded/fingerprinted JARs; actual SCM-triggered commit and logs/screenshots verified |
| 08 | Jenkins Pipeline | 🟡 | Jenkinsfile and deployment script created; actual pipeline/deployment verification in progress |
| 09 | Selenium | ⬜ | Not started |
| 10 | Continuous Testing | ⬜ | Not started |
| 11 | Docker | ⬜ | Not started |
| 12 | Docker CD | ⬜ | Not started |
| 13 | Ansible | ⬜ | Not started |
| 14 | Provisioning/Reliability | ⬜ | Not started |
| 15 | Final Validation | ⬜ | Not started |

| Area | Current verified state |
|---|---|
| Application | Full patient MVP verified on port 8081; 34 passing registration/appointment/security tests, browser/API flow, booking concurrency, CSRF/ownership, and restart persistence; verification app stopped |
| GitHub | `origin`: https://github.com/kirti-vispute/physiotherapy-appointment-portal.git; PRs #1/#2 reviewed/merged; annotated v1.0.0 points to release f629e23; `main` retains the MVP and Task 6 evidence, `develop` adds Task 7 CI setup/evidence |
| Jenkins | Windows service 2.568.1 running on 8080; admin access recovered with authorized existing credential; physio-portal-ci builds #1/#2 passed; H/2 SCM polling, JUnit reports, archives and fingerprints verified |
| Selenium | Not implemented or run |
| Docker | Not checked or configured |
| Ansible | Not checked or configured |
| Documentation | Tasks 1–7 complete; Jenkins setup/reproduction/evidence guide added, README and backlog/board/tracker updated; Selenium story DoD remains pending Task 9 |
| Evidence | Tasks 3–6 retained; Task 7 installation/plugins/job export, manual and SCM build console/API/JUnit reports, downloaded JAR checksums/fingerprints, trigger push/polling logs and actual screenshots |

## Task 1 evidence checklist

- **Screenshot required?** No. A reviewed document and explicit scope decision are stronger evidence for this planning task.
- **What must be visible?** The problem statement, motivation, users and stakeholders, pain points, constraints, objectives, measurable criteria, assumptions, in/out scope, 15-task scope, and approval record in `docs/problem-definition.md`.
- **Command required?** None. Open and review the document in the editor; this task has no executable component.
- **Actual result:** Kirti approved the scope in chat on 2 October 2026; the approval record was updated and Task 1 is ✅.
- **Evidence filename:** `docs/problem-definition.md`. The approval decision is also present in this chat.

## Task 5 verification gate

- ✅ Working registration form/API with validation and salted password hashes
- ✅ `feature/user-registration` created from `develop`; meaningful feature/test/docs commits pushed
- ✅ Real `git status`, `add`, `commit`, `push`, and `log` recorded in `docs/evidence/T05_git_workflow.txt`
- ✅ [PR #1](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/1) includes purpose, changes, tests, and screenshots
- ✅ [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/1#pullrequestreview-5393019879) published; no independent approval claimed
- ✅ Merged into `develop` at `f7713f0406c93747c7909041482f3682267edd0c`; local synchronization and unchanged tested source verified
- ✅ Screenshot/log evidence and reproduction instructions in `docs/task-05-registration.md`

## Task 6 verification gate

- ✅ Registration, sign-in/out, providers, future slots, booking, confirmation/status, cancellation, and errors
- ✅ 34 passing tests; real browser/API checks and stored appointments after restart
- ✅ `feature/appointment-booking` and `feature/demo-notes` pushed; actual conflict and two-parent resolution recorded
- ✅ [PR #2](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/2), [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/2#pullrequestreview-5393574270), merge `f629e232ee99a18ce5eb49d7bcc36f20a0f0742b`
- ✅ `main` release baseline and remote annotated `v1.0.0`: object `ca2238a0338d4660d501843e2dee66a62f35fe54`, peeled release `f629e23`
- ✅ Backlog/board and current documentation updated; actual screenshots and logs in [Task 6 guide](task-06-mvp.md)

## Task 7 verification gate

- ✅ Existing Jenkins installation and Java 21/Maven/Git configured and verified
- ✅ Required Git/JUnit/dependency plugins active and enabled
- ✅ `physio-portal-ci` checks out public GitHub `develop`; credentials none
- ✅ Manual build #1 SUCCESS, source `9e1e2a6`; both Maven commands and 34 tests pass
- ✅ Pushed `9ccbbdc`; actual polling found changes and automatic build #2 SUCCESS with SCM cause, exact source, 34 passing tests
- ✅ Executable JARs downloaded, checksummed and fingerprinted; reports/logs/configuration/screenshots saved in [Task 7 guide](task-07-jenkins-ci.md)

Tasks 1–7 are ✅. Task 8 is 🟡. Tasks 9–15 are ⬜. Selenium, Docker, and Ansible have not been executed.

