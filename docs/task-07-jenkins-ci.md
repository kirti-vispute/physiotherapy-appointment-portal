# Task 7 — Jenkins Installation and Continuous Integration

## Objective

Configure a local Jenkins freestyle job that checks out GitHub, runs Maven tests, packages the portal, publishes JUnit results, and archives the executable JAR. Verify both a manual build and an automatic build caused by a pushed commit.

## Requirements and deliverables

| Requirement | Deliverable | Status |
|---|---|---|
| Jenkins installation | Running controller and installation/version evidence | ✅ Verified existing service |
| Java/JDK, Maven, Git | Verified paths and Jenkins tool configuration | ✅ Verified in settings and builds |
| Required plugins | Installed/active Git and JUnit plugins and dependencies | ✅ Runtime API and UI verified |
| GitHub connection | Checkout of public repository, branch `develop` | ✅ Both builds record actual SHA |
| Build/test/package | Actual `mvn clean test`, then `mvn package` in Jenkins | ✅ Builds #1/#2 SUCCESS |
| Archive | Downloadable, fingerprinted `target/physio-portal-1.0.0.jar` | ✅ Download and fingerprint verified |
| Trigger | Poll SCM configuration plus build cause, commit and polling log | ✅ Build #2 caused by SCM change |
| Evidence | Console logs, build metadata, reports, configuration and screenshots | ✅ Saved |

## Actual installation inspection

On 2 October 2026, the existing Windows service `Jenkins` was Running with automatic startup. Jenkins reports version **2.568.1** at `http://localhost:8080`. Its executable is `C:\Program Files\Jenkins\jenkins.exe`; its WAR is `C:\Program Files\Jenkins\jenkins.war`; its data location is `C:\ProgramData\Jenkins\.jenkins`. Java is `C:\Program Files\DevTools\jdk-21.0.11`, Maven is `C:\Program Files\DevTools\apache-maven-3.9.16`, and Git is `C:\Program Files\Git\cmd\git.exe`.

The existing service uses LocalSystem and Java 21.0.11. This is an existing single-machine college setup. Jenkins documents a dedicated service account for production. No service identity, password, or authorization change has been made. [Official Windows installation guide](https://www.jenkins.io/doc/book/installing/windows/).

Login is required. The owner reported forgotten credentials. Automatic approval review initially rejected checking the setup credential without specific authorization. The owner then explicitly approved testing that existing credential only against local Jenkins. The `admin` setup credential was valid; authenticated access was recovered without changing passwords or security settings. The credential is not displayed, saved in project files, or committed. For later sign-in, the owner can privately read the existing `C:\ProgramData\Jenkins\.jenkins\secrets\initialAdminPassword` file and use username `admin`.

## Step-by-step implementation

1. Open `http://localhost:8080`, sign in, and check **Manage Jenkins → About Jenkins** for the version. Reuse the existing installation. For a fresh machine, use the official Windows MSI, select an available port and supported JDK, then complete the unlock/plugins/admin wizard. Do not reinstall an existing configured controller to solve a login problem.
2. Open **Manage Jenkins → Plugins → Installed plugins**. Confirm the following required plugins are enabled. If missing, use **Available plugins**, search the exact plugin name, select it, and install with its dependencies; verify enabled status afterward. [Official plugin management instructions](https://www.jenkins.io/doc/book/managing/plugins/).

   | Plugin | Purpose | Actual file version found |
   |---|---|---|
   | Git (`git`) | GitHub checkout and SCM polling | 5.10.1 |
   | Git client (`git-client`) | Command-line Git implementation | 6.6.1 |
   | Credentials (`credentials`) | Git plugin dependency; this public checkout needs no stored credential | 1511.v2e3cb_0008ef0 |
   | SCM API (`scm-api`) | SCM integration dependency | 728.vc30dcf7a_0df5 |
   | JUnit (`junit`) | Publish Surefire XML as Jenkins test results | 1424.vc64a_edde7777 |

   Archive manifests and the authenticated plugin API confirmed all six required plugins (including Mailer 534.v1b_36f5864073, a dependency) enabled and active. Runtime results are saved in `docs/evidence/T07_plugins.json`. A Maven project-type plugin is unnecessary when a freestyle job invokes the installed Maven executable from a Windows batch step. [Git plugin](https://plugins.jenkins.io/git/), [JUnit plugin](https://plugins.jenkins.io/junit/).

3. Open **Manage Jenkins → Tools**. Configure JDK `JDK21` with home `C:\Program Files\DevTools\jdk-21.0.11`, Git with executable `C:\Program Files\Git\cmd\git.exe`, and Maven `Maven3` with home `C:\Program Files\DevTools\apache-maven-3.9.16`. Uncheck automatic installers because these tools already exist. Save and verify paths. [Official tools guide](https://www.jenkins.io/doc/book/managing/tools/).
4. Create a freestyle project named `physio-portal-ci`. Configure JDK `JDK21`; the batch step also sets its exact home. Disable concurrent builds. Set build retention suitable for this small demonstration (ten builds). The job was created through Jenkins's authenticated configuration API with a session CSRF crumb, then inspected in its UI. The complete actual export is `docs/evidence/T07_job_config.xml`; the following UI steps reproduce the same configuration.
5. Under **Source Code Management → Git**, use `https://github.com/kirti-vispute/physiotherapy-appointment-portal.git`, credential **none**, and branch `*/develop`. This public repository supports anonymous read access. Never copy a personal GitHub token into the URL or build commands.
6. Enable **Poll SCM** with `H/2 * * * *`. Jenkins checks the repository every two minutes at a stable offset and builds only when its revision changes. A GitHub webhook cannot reach localhost directly; polling avoids a public tunnel or firewall changes. The schedule was saved and observed to trigger build #2.
7. Add **Execute Windows batch command** using the build block below. Java/Maven/Git paths must exist for the Jenkins service account, not just the interactive user. `call` and error checks preserve Maven failures on Windows.
8. Add post-build action **Publish JUnit test result report**, pattern `target/surefire-reports/TEST-*.xml`; keep missing reports an error. Add **Archive the artifacts**, pattern `target/physio-portal-1.0.0.jar`, fingerprinting enabled, archive only on success, and do not allow an empty archive.
9. Save, run **Build Now**, inspect checkout SHA, both Maven commands, test count and final result, then download/verify the archived JAR. This is actual Jenkins evidence only after the build executes.
10. Push a meaningful documentation update to `develop`, wait for SCM polling, and record the automatically triggered build's cause, checked-out commit, polling log, tests and artifact. A manually clicked build does not prove polling.

## Files to create/change

All project paths are under `D:\Kirti\__VIT RELATED\Lab Experiments\DEVOPS\DevOps Project`.

- `docs/task-07-jenkins-ci.md`: setup, expected commands, actual verification and evidence.
- `docs/project-tracker.md`, `docs/backlog.md`, `docs/agile-plan.md`, `README.md`: verified Task 7 state.
- `docs/evidence/T07_*`: actual installation/plugin/job/build/test/polling evidence when available.
- `screenshots/T07_*`: actual Jenkins views when available.
- `docs/evidence/T07_job_config.xml` is the actual sanitized job export. Jenkins home, credentials, caches and workspaces remain outside the Git repository.

## Commands

**Terminal:** PowerShell. **Location:** any directory. These inspect existing host tools/service:

```powershell
java -version
mvn -version
git --version
Get-Service -Name Jenkins
```

Purpose: confirm versions and running service. Observed: Java 21.0.11, Maven 3.9.16, Git 2.53.0, Jenkins Running. If a command is missing, check its installed executable path; if service inspection fails, check the actual service name rather than reinstalling.

**Terminal:** Jenkins **Execute Windows batch command**. **Location:** Jenkins job workspace after Git checkout. Configure this complete block:

```bat
@echo off
set "JAVA_HOME=C:\Program Files\DevTools\jdk-21.0.11"
set "MAVEN_HOME=C:\Program Files\DevTools\apache-maven-3.9.16"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;C:\Program Files\Git\cmd;%PATH%"
java -version
if errorlevel 1 exit /b 1
call mvn -version
if errorlevel 1 exit /b 1
git rev-parse HEAD
if errorlevel 1 exit /b 1
call mvn -B -ntp clean test
if errorlevel 1 exit /b 1
call mvn -B -ntp package
if errorlevel 1 exit /b 1
exit /b 0
```

Purpose: record tools/source, compile and test, then package the executable JAR. Expected: both Maven invocations finish `BUILD SUCCESS`; final test report contains 34 passing tests; Jenkins says `Finished: SUCCESS`, publishes tests and archives the JAR. Those results were observed in builds #1 and #2. The second command reruns the tests as required by Maven's package lifecycle. A missing executable means service PATH/tool configuration is wrong; dependency failures require inspecting network access from the service; `call` is necessary because Maven is a Windows command script. A test failure must stop packaging rather than be ignored.

### Reproduce the automatic trigger

**Terminal:** PowerShell. **Location:** `D:\Kirti\__VIT RELATED\Lab Experiments\DEVOPS\DevOps Project`. Make a meaningful documentation/code update first; inspect it, then:

```powershell
git status --short --branch
git diff
git add docs/task-07-jenkins-ci.md
git commit -m "docs: update Jenkins CI verification notes"
git push origin develop
```

Purpose: publish a genuine change for SCM polling. Expected: a new commit, successful push, and an automatic build within the polling interval plus queue time. If there is no file change, Git will create no commit and Jenkins should create no new build. If the current branch is not `develop`, follow the Git workflow before pushing. If the checkout reports dubious ownership, use the process-only trust setting in the Task 5 guide. Inspect **Git Polling Log** and build **Status**; do not click Build Now to prove this trigger. The actual demonstration used commit `9ccbbdc` with the setup, exported configuration and manual-build evidence; its push transcript and source identifier are saved.

## Verification

Installation, authenticated access, active plugins, tools, and the new `physio-portal-ci` freestyle job are verified. Existing `Selenium-Maven-Test` was preserved. `T07_job_config.xml` is the actual sanitized configuration returned by Jenkins.

**Manual build #1:** `SUCCESS`, started by `admin`, checked out `9e1e2a61139d70b825b398254b6d4064a0ac892e`. Both `mvn -B -ntp clean test` and `mvn -B -ntp package` reported 34 tests, zero failures/errors/skips and `BUILD SUCCESS`. The package step reran the same 34 tests; the published JUnit report represents 34 distinct tests. Jenkins recorded test results, archived the JAR, and recorded fingerprints. The downloaded archive is 65,083,824 bytes, SHA-256 `815F24ACFAB5B9AE9A728482E78385662B3FA440BAFC6116F8317D352CE48017`, MD5 fingerprint `f77c0eae763daad76a3d9653e843306e`. Console and API results are in `docs/evidence/T07_*_manual.*`.

**Automatic build #2:** pushed `9ccbbdcdc2eb3e506395a371961782e506e623f0` to `develop` at 23:10:10 IST. Polling at 23:10:57 reported the new remote revision and `Changes found`; build #2 started at 23:11:08 with cause `Started by an SCM change`. It checked out that exact commit and completed `SUCCESS` in 2 min 48 sec, with both Maven commands successful and 34 tests passing. No manual build request was sent for this change. Its archive is 65,083,824 bytes, SHA-256 `CA502786C6DA131B82317040F64482C7DF0E10053DC7FCA6FB76511365BBA255`, and fingerprint `fde0e1d0ec66405022d493402485f961`; the downloaded MD5 matches Jenkins's fingerprint API. `T07_gate.json` records the source/result/test/fingerprint checks.

Jenkins runs tests in its own workspace and H2 memory databases; the fictional local demo database is not included. JAR checksums vary between separate packaging runs; each archive has its own recorded checksum. Application source and `pom.xml` remain unchanged from tested `v1.0.0`.

The existing controller displays core/plugin update notices and uses the built-in node under LocalSystem. This evidence covers the existing local college demonstration; no controller upgrade, public exposure, service identity change or production hardening is claimed. Its existing unrelated job was preserved. SCM polling remains enabled, so future `develop` pushes (including final documentation) can produce further builds; the required proof here is builds #1/#2. Task 8 adds the pipeline and deployment; Selenium remains Task 9.

## Evidence and screenshot guidance

| Evidence | What must be visible | Action/expected result | Suggested filename |
|---|---|---|---|
| Installation | Jenkins version and dashboard identity | Sign in and inspect About Jenkins | `T07_jenkins_installation.jpg` |
| Plugins/tools | Required enabled plugin names/versions and configured tool paths | Installed plugins and Tools views | `T07_plugins.jpg`, `T07_tools.jpg` |
| Job configuration | Repository URL, `*/develop`, polling schedule, Maven steps, JUnit/archive patterns | Open job Configure after saving | `T07_job_config.jpg`; sanitized XML export |
| Successful build | Job/build number, actual commit, successful result, test link and downloadable JAR | Build Now; expected `SUCCESS` | `T07_jenkins_success.jpg`, `T07_console_manual.txt` |
| Tests | Actual total, failures and skipped count | Open Test Result; expected 34/0/0 | `T07_test_results.jpg` |
| Artifact | Filename, download, checksum/fingerprint | Open/download archived JAR | `T07_artifact.jpg`; metadata JSON |
| Poll trigger | SCM-triggered cause, pushed commit, polling change detection and successful build | Push then wait for polling | `T07_scm_trigger.jpg`, `T07_polling.txt`, `T07_console_scm.txt` |

Never capture passwords, cookies, API tokens or CSRF crumbs. Screenshots/logs are saved only after their corresponding action executes.

### Actual saved evidence

- Installation/tools/plugins: `T07_installation.json`, `T07_plugins.json`, `T07_job_config.xml`; `T07_jenkins_installation.jpg`, `T07_tools.jpg`, `T07_plugins.jpg`, `T07_junit_plugin.jpg`, `T07_github_config.jpg`, `T07_job_config.jpg`, `T07_archive_config.jpg`.
- Manual build: `T07_console_manual.txt`, `T07_build_manual.json`, `T07_tests_manual.json`, `T07_verified_manual.json`; `T07_jenkins_success.jpg`, `T07_test_results.jpg`, `T07_artifact.jpg`.
- Automatic build: `T07_trigger_push.txt`, `T07_trigger_commit.json`, `T07_polling.txt`, `T07_console_scm.txt`, `T07_build_scm.json`, `T07_tests_scm.json`, `T07_verified_scm.json`, `T07_fingerprint_scm.json`, `T07_gate.json`; `T07_scm_trigger.jpg`, `T07_polling.jpg`.
- JSON/XML/text files are under `docs/evidence/`; real images are under `screenshots/`. Downloaded archived JAR copies remain ignored under `target/`. The Jenkins artifact links remain on their corresponding build pages.

## Checklist

- ✅ Existing Jenkins installation, host tools and plugin files inspected
- ✅ Authenticated access and runtime plugin checks
- ✅ Jenkins tools and GitHub job configured
- ✅ Actual successful checkout/test/package builds #1/#2
- ✅ Test results and archived artifacts verified
- ✅ Actual SCM polling trigger demonstrated
- ✅ Evidence saved and backlog updated to completed

Task 8 is not started.
