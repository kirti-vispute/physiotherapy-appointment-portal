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
| 10 | Continuous Testing | ✅ | Jenkins #4 deliberate Selenium FAILURE skipped Deploy; old deployment unchanged/UP; correction #5 and merged #6 passed 39 tests and deployed; reports, screenshots and PR #5 saved |
| 11 | Docker | ✅ | 34-test JAR; Docker image `physio-portal:1.0.0`; versioned image/container IDs, 8086→8080 mapping, logs, stop/start/restart/rm, final healthy container and screenshot verified in [Task 11 guide](task-11-docker.md). |
| 12 | Docker CD | ✅ | PR #7 COMMENT self-reviewed and merged; develop Jenkins #9 passed 39 tests, pushed versioned image, removed prior owned container and deployed a new healthy 8087 container. See [Task 12 guide](task-12-docker-cd.md). |
| 13 | Ansible | ✅ | Ubuntu 24.04 WSL target configured by Ansible; first run `ok=10 changed=5 failed=0`, Docker active/enabled, user/folder/config permissions and registry manifest verified. See [Task 13 guide](../ansible/README.md). |
| 14 | Provisioning/Reliability | ⬜ | Not started |
| 15 | Final Validation | ⬜ | Not started |

| Area | Current verified state |
|---|---|
| Application | Full patient MVP verified; healthy Task 10 merged build #6 on localhost:8082 and earlier demo on 8081; embedded Tomcat, separate persistent databases, live/archive JAR hashes matched |
| GitHub | `origin`: https://github.com/kirti-vispute/physiotherapy-appointment-portal.git; PRs #1–#7 reviewed/merged with honest COMMENT self-reviews; v1.0.0 remains release f629e23; `main` retains Task 6 baseline, `develop` includes Task 12 Docker CD |
| Jenkins | Windows service 2.568.1 running on 8080; Task 7 CI/SCM polling verified; #4 deliberate browser FAILURE skipped Deploy; Task 12 feature #8 and merged develop #9 SUCCESS with 39 tests, versioned registry pushes and healthy Docker deployment |
| Selenium | Selenium 4.49.0 / Chrome 154; five local cases passed twice; Jenkins ran five against a fresh 8091 app, published reports on failure/success, and blocked Deploy on the deliberately failing assertion |
| Docker | Task 11 local image/lifecycle verified on 8086; Task 12 Jenkins #9 pushed `1.0.0-b9-180b726bc020` to loopback registry, removed build #8 container, deployed new container on 8087 and verified health UP |
| Ansible | Ubuntu 24.04 WSL control/target configured; inventory, variables, template and playbook verified; first run `ok=10 changed=5 failed=0`; Ubuntu Docker Engine active/enabled, registry manifest reachable. Task 14 container deployment and second run remain. |
| Documentation | Tasks 1–13 complete; Task 13 guide specifies packages, users, folders, files, ports, services and each Ansible task with exact commands and results |
| Evidence | Tasks 3–12 retained; Task 13 first-run log, independent target-state check, registry manifest and screenshot saved |

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
- ✅ [PR #4](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/4), [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/4#pullrequestreview-5395602129), merge `6a7ec38fe8fe713e73a3d4964fd7a009a357b7cd` and synchronized local `develop`

## Task 10 verification gate

- ✅ Versioned Jenkins stages start a fresh app on 8091, run five Selenium journeys and archive XML/HTML/screenshots before Deploy
- ✅ Deliberate commit `67b5d20` produced Jenkins #4 FAILURE: 34 backend passes, one of five browser cases failed, Deploy skipped
- ✅ Before/after state proves healthy build #3 at 8082 was unchanged after the failure; temporary test app stopped
- ✅ Correction commit `8ff53a5` produced Jenkins #5 SUCCESS: 39 passes, report published, healthy 8082 deployment and archive/live JAR hash equality
- ✅ [PR #5](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/5) COMMENT self-review and merge `d48aacfbfb9118c68afcbb518847a8f626ddd545`; final develop #6 SUCCESS
- ✅ [Task 10 guide](task-10-continuous-testing.md), console/API/XML/HTML, job config and real failed/successful Jenkins screenshots published

## Task 11 verification gate

- ✅ `mvn -B clean package` succeeded with 34 backend tests; executable JAR SHA256 recorded
- ✅ Docker Desktop Linux engine recovered from inaccessible temporary socket files without deleting images, containers, or volumes
- ✅ `docker build --pull` created `physio-portal:1.0.0` with full image ID `sha256:c260571b57675bd2aae05ccdd25b604e57a40c5407aa4d93459f5f59b0d2ae34`
- ✅ First container `256457adc2c1eecf3a1235acac625900d3f30d57afb715830a560e47e6b6e6ad` ran on `127.0.0.1:8086→8080/tcp`; logs showed Tomcat startup and health UP
- ✅ Exact `stop`, `start`, `restart`, second `stop`, and `rm` commands succeeded; health returned UP after both restarts and removal was verified
- ✅ Final container `b3ec7ce3aac79450fee7c7267e57a656d1125f705857be3890da47ca4eef99e1` runs from the same image/volume; home/providers HTTP 200, health UP and H2 file retained
- ✅ [Task 11 guide](task-11-docker.md), [command log](evidence/T11_docker_lifecycle.txt), and real [container-served page screenshot](../screenshots/T11_docker_running.png) saved
- ✅ [PR #6](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/6) received COMMENT self-review and merged at `fb372c14a8c16925a47e787163e2d766964afade`; local `develop` synchronized

## Task 12 verification gate

- ✅ [PR #7](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/7) received [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/7#pullrequestreview-5400448994) and merged at `180b726bc020aaa534f65f69283f30b9bb04abeb`; Jenkins SCM restored to `develop`
- ✅ Feature build #8 and merged [develop build #9](http://localhost:8080/job/physio-portal-pipeline/9/) completed the 39-test gate and Docker Build → Tag → Push → Stop → Run → Health sequence
- ✅ Registry contains `localhost:5000/physio-portal:1.0.0-b9-180b726bc020` at digest `sha256:3977b8ccbc1706b2eb00425f4589ad0a720752d492c02b4f37ea410b63d5f9e5`
- ✅ Build #8 container was stopped and removed; build #9 container `692075d7910a1de97cf251331e3cd549019972bec26e1fa867fbf248532e472b` runs on `127.0.0.1:8087`, health UP and home/providers HTTP 200
- ✅ Jenkins archive JAR and container JAR hashes match; console, stage logs, metadata, before/after checks and screenshot saved in [Task 12 guide](task-12-docker-cd.md)

## Task 13 verification gate

- ✅ Ubuntu 24.04.5 WSL installed as the Ansible control and managed node; Ansible core 2.16.3 available
- ✅ Inventory, group variables, environment template and playbook pass syntax and inventory checks
- ✅ First real run completed `ok=10 changed=5 unreachable=0 failed=0`; [execution log](evidence/T13_ansible_first_run.txt) retained
- ✅ Independent check confirms Docker active/enabled, `physio` UID/GID 10001, folder mode `750`, environment file mode `640`, registry HTTP 200 and [versioned manifest](evidence/T13_registry_manifest.json)
- ✅ [Task 13 guide](../ansible/README.md) explains every task, Windows/WSL commands, expected/actual results, errors and [screenshot](../screenshots/T13_ansible_execution.png)

Tasks 1–13 are ✅. Tasks 14–15 are ⬜. The Ubuntu application container, second Ansible run, health check and rollback remain Task 14 work.
