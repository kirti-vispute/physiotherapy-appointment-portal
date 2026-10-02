# Project state tracker

**Project:** Selenium Testing for a Physiotherapy Appointment Portal  
**Owner:** Kirti Vispute (23102C0078)  
**Updated:** 3 October 2026

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
| 08 | Jenkins Pipeline | ✅ | Five stages; feature #2/develop #3 SUCCESS, 34 tests each; test/8082 HTTP 200/UP and live/archive hash match; PR #3 merged, logs/configuration/screenshots saved |
| 09 | Selenium | ✅ | Five actual Chrome WebDriver cases passed twice, 34 backend tests passed; HTML/XML, fixture cleanup, failure screenshot and exit 1 verified; Task 9 guide/evidence published |
| 10 | Continuous Testing | ⬜ | Not started |
| 11 | Docker | ⬜ | Not started |
| 12 | Docker CD | ⬜ | Not started |
| 13 | Ansible | ⬜ | Not started |
| 14 | Provisioning/Reliability | ⬜ | Not started |
| 15 | Final Validation | ⬜ | Not started |

| Area | Current verified state |
|---|---|
| Application | Full patient MVP and 34 tests verified; healthy Task 8 test deployment at localhost:8082 and demo at 8081; embedded Tomcat, separate persistent databases, live/archive JAR hashes matched |
| GitHub | `origin`: https://github.com/kirti-vispute/physiotherapy-appointment-portal.git; PRs #1/#2/#3 reviewed/merged; v1.0.0 remains release f629e23; `main` retains Task 6 baseline, `develop` includes CI/pipeline/deployment evidence |
| Jenkins | Windows service 2.568.1 running on 8080; Task 7 CI/SCM polling verified; physio-portal-pipeline #2/#3 SUCCESS with 34 tests, JAR/log/metadata archives and parameterized deployment; initial hung #1 ABORTED and correction recorded |
| Selenium | Selenium 4.49.0 / Chrome 154; five local cases passed twice, report on 8084; intentional local failure captured; Jenkins gate remains Task 10 |
| Docker | Not checked or configured |
| Ansible | Not checked or configured |
| Documentation | Tasks 1–9 complete; five-case test plan, Selenium guide, reports, README, backlog, board, stories and tracker updated; Jenkins Selenium gate remains Task 10 |
| Evidence | Tasks 3–7 retained; Task 8 failure/fix/success runs, JUnit, archive/deployment hashes, live HTTP/profile/port checks, job XML, PR/review/merge and actual screenshots |

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

## Task 8 verification gate

- ✅ Jenkinsfile from GitHub with Checkout → Build → Unit Test → Package → Deploy
- ✅ Required Pipeline plugins active; syntax validated and tool names verified
- ✅ Initial wrapper hang recorded as ABORTED #1; detached Windows launcher correction committed
- ✅ Feature #2 SUCCESS on `6af1dfd`, 34 tests, demo/8081 healthy after build
- ✅ PR #3 self-reviewed and merged at `7f973000a94840c7a91d17e7ce68218df607f3f1`
- ✅ Develop #3 SUCCESS on that merge, 34 tests, nondefault test/8082; five stages and archives
- ✅ Home/providers HTTP 200, health UP, profile/port log and live/archive SHA256 verified
- ✅ Screenshots, configuration, console/JUnit/build/deployment/PR evidence and [Task 8 guide](task-08-pipeline-deployment.md) published

## Task 9 verification gate

- ✅ Five independent WebDriver journeys with all required test-plan fields and actual outcomes
- ✅ Explicit waits, assertions, stable locators, fictional data, fresh browsers and UI booking cleanup
- ✅ Initial Maven verification: 34 backend + 5 browser cases, zero failures/errors/skips
- ✅ Focused repeat: five cases, zero failures/errors/skips; XML/text/HTML preserved
- ✅ Intentional local probe: one assertion failure, Maven exit 1 and real failure PNG before quit
- ✅ Actual browser report/checkpoint screenshots and [Task 9 guide](task-09-selenium.md)
- GitHub PR/review/merge details are recorded in the guide after publication

Tasks 1–9 are ✅. Tasks 10–15 are ⬜. Next: Task 10 Jenkins continuous testing and deployment gate. Docker and Ansible have not been executed.
