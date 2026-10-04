# Project state tracker

**Project:** Selenium Testing for a Physiotherapy Appointment Portal  
**Owner:** Kirti Vispute (23102C0078)  
**Updated:** 4 October 2026

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
| 13 | Ansible | ✅ | Ubuntu 24.04 WSL configured by Ansible; first run `ok=10 changed=5 failed=0`, Docker/account/files/registry verified; PR #8 COMMENT self-reviewed and merged. See [Task 13 guide](../ansible/README.md). |
| 14 | Provisioning/Reliability | ✅ | Ansible full runs `changed=4` then `changed=0`; Ubuntu container on 8089 healthy; bad port produced `failed=1`, known good rollback restored HTTP 200 and retained database volume. See [Task 14 guide](task-14-provisioning.md). |
| 15 | Final Validation | ✅ | [Final report](final-report.md), [15-task audit](final-audit.md), [evidence index](final-evidence-index.md), [seven diagrams](final-diagrams.md), [16-part viva guide](final-demo.md), final 34+5 local pass and SCM-triggered Jenkins #11 SUCCESS with 39 tests and healthy deployment |

| Area | Current verified state |
|---|---|
| Application | Full patient MVP verified; after Docker Desktop recovery on 4 October, the same build #11 container on 8087 returned health UP and home/providers HTTP 200; earlier 8082 and 8089 checks are timestamped, and local services may stop after reboot |
| GitHub | `origin`: https://github.com/kirti-vispute/physiotherapy-appointment-portal.git; PRs #1–#10 reviewed/merged with honest COMMENT self-reviews; v1.0.0 remains release f629e23; `main` retains Task 6 baseline, `develop` includes Task 15 final validation |
| Jenkins | Windows service 2.568.1 on 8080; Task 7 CI/SCM polling verified; #4 deliberate browser FAILURE skipped Deploy; Task 15 Pipeline #10 timed out, then SCM-caused #11 SUCCESS passed 39 tests, pushed a versioned registry image, replaced the container and passed health |
| Selenium | Selenium 4.49.0 / Chrome 154; five local cases passed twice; Jenkins ran five against a fresh 8091 app, published reports on failure/success, and blocked Deploy on the deliberately failing assertion |
| Docker | Task 11 local lifecycle verified on 8086; SCM-caused Jenkins #11 image `1.0.0-b11-58c86345b231` and container `5c44b13e...` verified on 8087; Task 14 Ubuntu engine retains the separately pinned build #9 image/volume on 8089 |
| Ansible | Ubuntu WSL first configuration run `changed=5`; Task 14 site playbook first deployment `changed=4`, second run `changed=0`; bad port failed health and rollback restored HTTP 200 |
| Documentation | All 15 tasks complete; final report covers 38 required topics, audit covers every task, seven diagrams and 16-part viva sequence are linked |
| Evidence | Tasks 1–14 retained; Task 15 local Maven/HTTP, PR #10, trigger configuration, timed-out #10, successful SCM-caused #11, test/deployment metadata and independent post-reboot check saved |

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
- ✅ Build #8 container was stopped and removed; build #9 container `692075d7910a1de97cf251331e3cd549019972bec26e1fa867fbf248532e472b` ran on `127.0.0.1:8087` with health UP and home/providers HTTP 200, then Task 15 build #11 replaced it
- ✅ Jenkins archive JAR and container JAR hashes match; console, stage logs, metadata, before/after checks and screenshot saved in [Task 12 guide](task-12-docker-cd.md)

## Task 13 verification gate

- ✅ Ubuntu 24.04.5 WSL installed as the Ansible control and managed node; Ansible core 2.16.3 available
- ✅ Inventory, group variables, environment template and playbook pass syntax and inventory checks
- ✅ First real run completed `ok=10 changed=5 unreachable=0 failed=0`; [execution log](evidence/T13_ansible_first_run.txt) retained
- ✅ Independent check confirms Docker active/enabled, `physio` UID/GID 10001, folder mode `750`, environment file mode `640`, registry HTTP 200 and [versioned manifest](evidence/T13_registry_manifest.json)
- ✅ [Task 13 guide](../ansible/README.md) explains every task, Windows/WSL commands, expected/actual results, errors and [screenshot](../screenshots/T13_ansible_execution.png)
- ✅ [PR #8](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/8) received [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/8#pullrequestreview-5401355533) and merged at `4f1c1858bd61e397ba005634136c08295b0d6634`; tested playbook blob matches merged source

## Task 14 verification gate

- ✅ Clean Ubuntu application target had no image/container; [before state](evidence/T14_before_provisioning.json) saved
- ✅ [First full playbook](evidence/T14_provision_first.txt) `ok=16 changed=4 failed=0` pulled the pinned image, created a volume and started the container; health/home/providers passed
- ✅ [Second full playbook](evidence/T14_provision_second.txt) `ok=16 changed=0 failed=0`, demonstrating idempotency without unnecessary container replacement
- ✅ Local WSL keeper made the separate Ubuntu engine available after commands; Windows verified the healthy app on port 8089
- ✅ [Bad configuration](evidence/T14_bad_release.txt) changed `PORT` to 8099 while mapping to 8080; health gate failed with exit 2 and `failed=1`; [state check](evidence/T14_bad_release_state.json) showed the good image and volume intact
- ✅ [Rollback](evidence/T14_rollback.txt) restored the known good port and container; [recovery check](evidence/T14_recovered_state.json) verified bad container removal, retained H2 volume/database file, health UP and three HTTP 200 responses
- ✅ [Task 14 guide](task-14-provisioning.md) and idempotency, health and rollback screenshots saved
- ✅ [PR #9](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/9) received [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/9#pullrequestreview-5401462270) and merged at `955e6b93c740fd1fc550b9c33f89e78fa7b0ef3e`; local `develop` synchronized

## Task 15 verification gate

- ✅ [Final report](final-report.md) covers all 38 documentation topics; [audit](final-audit.md) maps every task to deliverables, evidence and status
- ✅ [Evidence index](final-evidence-index.md) specifies screenshot need, exact view/command and expected result for all 15 tasks; [seven diagrams](final-diagrams.md) match the implementation
- ✅ [Viva sequence](final-demo.md) gives all 16 actions with commands, expected outcomes, and fallback evidence
- ✅ [Final local Maven run](evidence/T15_final_maven_verify.txt) passed 34 backend plus five Selenium cases; [live HTTP check](evidence/T15_live_checks.json) recorded 8082/8087/8089 health UP and page HTTP 200 on 3 October
- ✅ [PR #10](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/10) received [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/10#pullrequestreview-5401701041) and merged at `8aadb3679fa9afc8d429dabd3fc02a54cb8588cc`
- ✅ Jenkins [Poll SCM configuration](evidence/T15_trigger_config.json) active; manual activation [#10](evidence/T15_activation_build.json) timed out and did not deploy, then [SCM-caused #11](evidence/T15_auto_build11.json) completed SUCCESS with [39 passing tests](evidence/T15_auto_tests.json), versioned image push, new container and health gate
- ✅ [Independent check](evidence/T15_auto_independent_check.json) after Docker Desktop reboot recovery matched build #11's image/commit/container/volume, verified registry manifest and old build #9 container removal, and returned health UP and HTTP 200

Tasks 1–15 are ✅. The future viva presentation itself remains to be performed by the student using the verified sequence.

**Later UI refinement:** [Design and verification addendum](ui-refresh.md) records the refreshed patient pages, locally created physiotherapy illustration, 34 passing backend tests, five passing Selenium journeys, and responsive screenshots. It stays within the T1–T15 scope.
