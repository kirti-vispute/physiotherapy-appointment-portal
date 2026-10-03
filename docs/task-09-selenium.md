# Task 9 — Selenium Design and Local Execution

**Owner:** Kirti Vispute (23102C0078)  
**Date:** 3 October 2026  
**Status:** ✅ Complete; PR #4 merged into `develop`

## Objective and deliverables

Execute five real WebDriver journeys for registration, available slots, booking, cancellation and appointment status. Deliver a complete [test plan](selenium-test-plan.md), fictional fixture data, reusable browser setup/teardown, locators/waits/assertions, screenshot-on-failure mechanism, Maven execution, XML/HTML report and actual screenshots. Task 10 will integrate these tests into Jenkins and prove its deployment gate.

## Requirements and changed files

- Java 21, Maven 3.9.16, Chrome, a healthy local portal and first-run driver download access.
- `pom.xml`: Selenium Java 4.49.0 (test scope), opt-in `selenium` profile, Failsafe 3.5.6 and Surefire Report 3.5.6. Normal `mvn test` runs the existing 34 tests; `*IT` browser tests execute only through the opt-in profile.
- `src/test/resources/selenium/test-data.properties`: dummy name/password, reserved example.test domain, provider and clinic zone.
- `src/test/java/com/kirtivispute/physio/selenium/PortalSeleniumIT.java`: five independent cases.
- `SeleniumSupport.java`: fresh Chrome profile, explicit waits, UI registration/sign-in/booking, dynamic IDs, fixture release, screenshots and driver quit.
- `FailureScreenshotExtension.java`: captures failure before teardown and rethrows the original exception, including setup failures after a browser exists.
- `ScreenshotCaptureProbeIT.java`: explicitly enabled failure diagnostic, excluded from the normal journey suite.
- `docs/selenium-test-plan.md`, this guide, actual evidence in `docs/evidence/T09_*`, and `screenshots/T09_*`.

Selenium Manager handles driver discovery/download; the installed browser was Chrome **154.0.8037.95**, matching ChromeDriver **154.0.8037.92**. Selenium was pinned from its [official downloads page](https://www.selenium.dev/downloads/). [Manager documentation](https://www.selenium.dev/documentation/selenium_manager/) describes driver management. The suite uses the [documented explicit waiting approach](https://www.selenium.dev/documentation/webdriver/waits/) and [Chrome options](https://www.selenium.dev/documentation/webdriver/browsers/chrome/). The report goal renders existing XML without rerunning tests, as documented by [Maven Surefire Report](https://maven.apache.org/surefire-archives/surefire-3.5.6/maven-surefire-report-plugin/failsafe-report-only-mojo.html).

## Step-by-step execution

1. Use the Task 8 portal at [localhost:8082](http://localhost:8082/). If it was stopped/rebooted, run **physio-portal-pipeline → Build with Parameters → APP_ENV=test, PORT=8082**. Alternatively start a manual JAR on an available local port and override `selenium.baseUrl` accordingly.
2. Verify `/actuator/health` and the provider/slot view. At least one future free slot for the fixture provider is required; seed data uses the next three days on startup.
3. Open PowerShell in the project root. Set the Java/Maven paths if they are not on PATH.
4. Execute the normal Maven profile. It first runs 34 integration tests against isolated in-memory databases, packages, then runs the five WebDriver journeys against the configured live app. Failsafe `verify` returns failure if a browser case fails.
5. Review the XML/text results and HTML report. Whole-case outcomes include cleanup. Browser screenshots record assertion checkpoints, not the whole-case verdict by themselves.
6. Run the explicit failure diagnostic when verifying the capture mechanism. Its reports are isolated from the five-journey report; expected Maven exit is 1.
7. Preserve generated reports/screenshots before a later `mvn clean` removes `target`. The committed evidence copies listed below survive clean builds.

## Exact commands

**Terminal:** PowerShell. **Location:** `D:\Kirti\__VIT RELATED\Lab Experiments\DEVOPS\DevOps Project`.

### Configure tools and check the target

```powershell
$env:JAVA_HOME='C:\Program Files\DevTools\jdk-21.0.11'
$env:PATH="$env:JAVA_HOME\bin;C:\Program Files\DevTools\apache-maven-3.9.16\bin;$env:PATH"
$env:SE_AVOID_STATS='true'
java -version
mvn -version
Invoke-RestMethod http://localhost:8082/actuator/health
```

**Purpose:** use the configured JDK/Maven and confirm readiness. **Expected:** Java 21, Maven 3.9.16 and UP. `SE_AVOID_STATS` disables optional Manager statistics for this process. **Common errors:** missing commands require correct tool paths; connection refused requires starting the app at the selected port.

### Normal suite and report

```powershell
mvn -B -ntp -Pselenium '-Dselenium.baseUrl=http://localhost:8082' verify
```

**Purpose:** backend checks, packaging, browser journeys and HTML report. **Expected:** 34 Surefire tests and 5 Failsafe tests, each with zero failures/errors/skips, then BUILD SUCCESS. **Outputs:** `target/surefire-reports/`, `target/failsafe-reports/`, `target/selenium-report/selenium.html`, and `target/selenium-evidence/`.

**Common errors:** Manager download/proxy failure requires access to the official driver distribution or a matching driver already configured on PATH; browser mismatch requires a matching driver. Missing/occupied slots require a healthy demo fixture with future availability. A failed assertion requires inspecting the Failsafe XML and `*-failure.png`; do not change assertions to conceal application defects.

All dotted `-D` parameters are quoted in PowerShell. The first diagnostic command split an unquoted `-Dit.test` and failed before tests; its [actual command error](evidence/T09_probe_command_error.txt) is retained. The corrected quoted command below produced the intended browser assertion failure and screenshot.

### Optional focused browser rerun

```powershell
mvn -B -ntp -Pselenium '-DskipUnitTests=true' '-Dselenium.baseUrl=http://localhost:8082' verify
```

**Purpose:** rerun only the five journeys after backend tests have already passed. **Expected:** backend tests explicitly skipped, five browser cases pass, HTML regenerated. This was used to verify repeat-safe fixture cleanup and the normal report directory after isolating the diagnostic output. It does not replace the initial 34-test evidence.

### Failure screenshot diagnostic

```powershell
mvn -B -ntp -Pselenium '-DskipUnitTests=true' '-Dit.test=ScreenshotCaptureProbeIT' '-Dselenium.failureProbe=true' '-Dselenium.reportsDirectory=target/selenium-probe-reports' '-Dselenium.baseUrl=http://localhost:8082' verify
$LASTEXITCODE
```

**Purpose:** verify capture before teardown and exception propagation. **Expected:** exactly one intentional assertion failure, zero errors/skips, BUILD FAILURE and exit **1**; `target/selenium-evidence/intentionalFailure-failure.png` and matching metadata; separate `target/selenium-probe-reports` XML/summary. A command parsing error or driver setup error does not satisfy this diagnostic. The probe does not create a booking. No Jenkins failure/deploy gate is claimed for Task 9.

### Inspect reports and screenshots

```powershell
Get-Content target/failsafe-reports/com.kirtivispute.physio.selenium.PortalSeleniumIT.txt
Get-ChildItem target/selenium-evidence
```

**Purpose:** inspect case counts and generated screenshots/metadata. **Expected:** five successful cases and five checkpoint PNGs; an additional failure PNG after the explicit diagnostic. Open `target/selenium-report/selenium.html` in a browser. A missing report means the test/report goal did not complete; raw XML/console remains the source for diagnosis.

The preserved report is [docs/evidence/T09_report/selenium.html](evidence/T09_report/selenium.html). A local read-only preview serves that report directory at [localhost:8084/selenium.html](http://localhost:8084/selenium.html). To reproduce a preview from the project root in a separate PowerShell terminal:

```powershell
python -m http.server 8084 --bind 127.0.0.1 --directory docs/evidence/T09_report
```

**Purpose:** serve only the committed report/assets. **Expected:** HTML with five cases/100% success, no app deployment effect. **Common error:** port occupied means an existing preview is already using it; use that preview or another free port. Press Ctrl+C for a foreground preview. The Codex-created hidden preview's PID is recorded locally in `target/T09_report_server_pid.txt`; it is not an installed service.

## Actual results and evidence

Initial normal Maven execution on 3 October 2026 completed **BUILD SUCCESS**, exit 0, in 2 min 37 sec. Surefire passed **34/34** (20 appointment/security + 14 registration); Failsafe passed **5/5**, zero failures/errors/skips, in **56.056 sec**. Four fictional accounts were generated; all three fixture bookings were cancelled by the case or teardown. Selenium used the Task 8 app on 8082, source `7f973000a94840c7a91d17e7ce68218df607f3f1`; the application Java/templates/configuration are unchanged by Task 9.

The corrected screenshot diagnostic ran **one intentional failed case**, zero errors/skips, returned exit **1**, and wrote a real **33,926-byte** failure PNG before closing Chrome. The actual exception message is retained in the XML and [probe result](evidence/T09_probe_exit.json).

The focused rerun also passed **5/5** with zero failures/errors/skips in **36.272 sec** (Maven total 52.259 sec), completing at **00:13:28 IST on 3 October 2026**. Its XML/text/summary are preserved separately in `T09_repeat`; the HTML report renders this rerun. The five checkpoint screenshots and paired metadata retain the initial run. A successful report-only regeneration corrected the project header link to this GitHub repository. The generated report's header is not the run timestamp authority; use the dated CLI log and capture metadata for the actual IST execution time.

[PR #4](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/4) merged the two Task 9 commits into `develop` at `6a7ec38fe8fe713e73a3d4964fd7a009a357b7cd`. [The COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/4#pullrequestreview-5395602129) checks the exact PR head and is explicitly a single-owner review. The merged page, review and SHA were verified in GitHub and recorded in [API evidence](evidence/T09_github_pr.json), [Git workflow](evidence/T09_git_workflow.txt) and an [actual screenshot](../screenshots/T09_pull_request.jpg). Local `develop` was fast-forwarded to the merge before this documentation follow-up.

| Evidence | Contents |
|---|---|
| [T09_maven_local.txt](evidence/T09_maven_local.txt) | Actual initial 34 + 5 Maven run and BUILD SUCCESS |
| [T09_results.json](evidence/T09_results.json) | Counts, methods/durations, target/browser/driver and diagnostic outcome |
| [T09_local](evidence/T09_local/TEST-com.kirtivispute.physio.selenium.PortalSeleniumIT.xml) | Preserved initial Selenium, two backend JUnit XML suites, text/summary |
| [HTML report](evidence/T09_report/selenium.html) / [report screenshot](../screenshots/T09_selenium_report.jpg) | Maven-generated focused-rerun report and its assets; actual browser view |
| [Focused rerun log](evidence/T09_maven_repeat.txt) / [XML](evidence/T09_repeat/TEST-com.kirtivispute.physio.selenium.PortalSeleniumIT.xml) | Five repeated journeys passed, including booking cleanup |
| [Report generation](evidence/T09_report_generation.txt) | Successful report-only regeneration from the rerun XML |
| [PR evidence](evidence/T09_github_pr.json) / [screenshot](../screenshots/T09_pull_request.jpg) | Reviewed and merged PR #4 into `develop` |
| [T09_screenshot_probe.txt](evidence/T09_screenshot_probe.txt) / [XML](evidence/T09_screenshot_probe.xml) | Actual intentional assertion failure, not a Jenkins run |
| [Failure screenshot](../screenshots/T09_failure_capture.png) | Portal captured before diagnostic teardown |
| `docs/evidence/T09_browser_metadata/` | Actual fictional emails, dynamic IDs, URLs, capture times and driver details; no password |

Selenium emitted a CDP-version warning (nearest 153 for Chrome 154). These tests use standard WebDriver commands, not DevTools APIs, and the actual five cases passed. Maven's inherited project metadata also emitted a parent-URL warning; report generation succeeded. These warnings are retained in the log.

## Screenshot instructions and completion checklist

Screenshots are required. Preserve the actual Maven report view with project identity, five cases, zero failures/errors/skips and 100% success as `T09_selenium_report.jpg`. The generated `T09_SEL01-registration.png` shows authenticated registration; `T09_SEL02-available-slot.png` shows provider/IST availability; `T09_SEL03-booking.png` shows the numeric confirmed appointment; `T09_SEL04-cancellation.png` shows cancellation; `T09_SEL05-status.png` shows own confirmed status after signing in again. Failure capture is `T09_failure_capture.png`; its paired metadata/failed XML proves the capture path. Never invent a browser result or crop away the result needed by the case.

- [x] Five test cases with all required fields and story traceability
- [x] Fictional repeat-safe data and stable locators
- [x] Explicit waits/assertions and reusable setup/teardown
- [x] Screenshot-on-failure and failing exit verified by actual local probe
- [x] Normal Maven execution: 34 backend + 5 browser tests passed
- [x] Real XML/HTML report and checkpoint screenshots generated
- [x] Final report view, rerun, reviewed PR and evidence publication recorded
- [x] README/backlog/board/story/tracker updated

Task 10 subsequently added the Jenkins browser gate. Its real failure/correction demonstration is documented in [the continuous testing guide](task-10-continuous-testing.md).
