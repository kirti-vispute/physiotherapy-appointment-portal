# Task 8 — Pipeline as Code and Server Deployment

**Owner:** Kirti Vispute (23102C0078)  
**Project:** Selenium Testing for a Physiotherapy Appointment Portal  
**Status:** 🟡 Execution and evidence collection in progress

## Objective and deliverables

Load a versioned `Jenkinsfile` from GitHub, run Checkout → Build → Unit Test → Package → Deploy, publish the tests and executable JAR, and deploy a healthy application using environment parameters. Required evidence: successful stages, actual parameter values, deployed URL, logs, configuration, and screenshots.

## Requirements and files

- Windows Jenkins service on [localhost:8080](http://localhost:8080/), Java 21, Maven 3.9.16, Git, and the active Pipeline/Git/JUnit plugins configured in Task 7.
- Jenkins tools named **JDK21** and **Maven3**. This college setup runs on the Windows `built-in` node; a different installation must choose its own Windows agent label and tool paths.
- [Jenkinsfile](../Jenkinsfile): declarative stages, two choice parameters, concurrency control, timeout, test gate and artifact publishing.
- [Deployment script](../scripts/deploy-local.ps1): validate parameters and ownership, copy the packaged JAR, start Java, wait for health, and save deployment metadata/logs.
- Job **physio-portal-pipeline**, Pipeline script from SCM, public GitHub repository, `Jenkinsfile` script path. Task 7's separate freestyle job remains available.
- Deployment directory: `C:\ProgramData\Jenkins\physio-portal-demo\<APP_ENV>-<PORT>`. This is outside the checkout, so Maven `clean` cannot remove the running JAR or database.

## Server choice and environment parameters

This Spring Boot executable JAR contains embedded Tomcat. Starting the JAR starts the real HTTP server; a separate Tomcat WAR installation or Nginx proxy is unnecessary for the local assignment deployment. Docker deployment is scheduled for Tasks 11–12.

| Parameter | Allowed values | Default | Actual effect |
|---|---|---|---|
| `APP_ENV` | `demo`, `test` | `demo` | Sets `spring.profiles.active`; selects a separate deployment and persistent H2 directory |
| `PORT` | `8081`, `8082` | `8081` | Sets `server.port` and the application URL |

Both profiles use the existing application configuration and fictional seed data; these names do not imply separate production credentials or business behavior. Java binds to `127.0.0.1`; the URL is local to this computer. Jenkins keeps port 8080.

## Step-by-step setup and execution

1. Open Jenkins and sign in as `admin` using the existing local credential recovered in Task 7. No password reset is required. Credentials are not in this repository or evidence.
2. **Manage Jenkins → Plugins → Installed**: confirm Pipeline, Pipeline: Declarative, Pipeline: Nodes and Processes, Pipeline: SCM Step, Pipeline: Basic Steps, Timestamper, Git and JUnit are enabled. Actual version/active state is in [plugin evidence](evidence/T08_plugins.json).
3. **Manage Jenkins → Tools**: confirm JDK21, Maven3 and Git Default paths from [Task 7](task-07-jenkins-ci.md).
4. Create **New Item → physio-portal-pipeline → Pipeline**. Select **Pipeline script from SCM → Git**, URL `https://github.com/kirti-vispute/physiotherapy-appointment-portal.git`, credentials **none**, branch `*/feature/jenkins-pipeline` for the initial feature verification, script path `Jenkinsfile`. Save. After the verified PR merge, set branch to `*/develop`.
5. The first run establishes parameters from the Jenkinsfile. This job also received the same choice parameters during creation, allowing **Build with Parameters** on run #1. Select the environment/port and build.
6. Open the build's **Pipeline Overview**, **Console Output**, **Tests**, **Parameters**, and **Build Artifacts**. Check all five stages and the source revision. `Package` executes only after tests pass. Maven failure stops execution; an unstable JUnit result skips remaining stages.
7. Open the printed application URL and `/physiotherapists`; call `/actuator/health`. Compare the archived JAR SHA256 with `deployment.json` and the deployed JAR.

The declarative controls and Windows `bat`/`powershell` steps follow the [Jenkins Pipeline syntax reference](https://www.jenkins.io/doc/book/pipeline/syntax/) and [Nodes and Processes steps](https://www.jenkins.io/doc/pipeline/steps/workflow-durable-task-step/). The deployment changes `JENKINS_NODE_COOKIE` only for its child process and redirects stdin/stdout/stderr so the server can survive build completion, as described in [Jenkins process guidance](https://www.jenkins.io/doc/book/managing/spawning-processes/). No global process-cleanup setting is changed.

## Exact commands and expected results

### Maven stages

**Terminal:** Jenkins Windows batch step. **Location:** this job's Jenkins checkout, not the student folder. Jenkins tools supply Java/Maven to PATH.

| Command | Purpose | Expected result | Common error and response |
|---|---|---|---|
| `git rev-parse HEAD` | Record exact checked-out revision | 40-character source SHA | Git unavailable: fix Git tool path |
| `mvn -B -ntp clean compile` | Remove prior build output and compile | `BUILD SUCCESS` | Java mismatch: select JDK21; network dependency error: inspect repository connectivity |
| `mvn -B -ntp test` | Run the existing 34 security/registration/appointment tests | 34 tests, zero failures/errors/skips | Inspect Surefire XML/console; fix code before deploying |
| `mvn -B -ntp -DskipTests package` | Package the already tested classes | Executable `target/physio-portal-1.0.0.jar` | Test skipping here is intentional: the preceding test stage must pass |

The Jenkinsfile uses `call mvn` inside `bat` because Maven is a Windows batch command. Every batch step propagates Maven's exit code to Jenkins. JUnit XML is published even when the test command fails, and the JAR is fingerprinted before deployment.

### Deployment

**Terminal:** Jenkins PowerShell step. **Location:** job workspace. Parameters are read as environment variables, validated by PowerShell, and not interpolated into shell commands.

```powershell
& "$env:WORKSPACE/scripts/deploy-local.ps1"
```

**Purpose:** copy the current JAR into a build-number release folder, validate the checksum, replace only a prior deployment owned by this script, start the server, verify health and save metadata. **Expected:** `health: UP`, build/source/JAR hash, PID, profile, port and URL in `target/deployment.json`; startup logs in `target/deploy-*.log`. **Errors:** occupied unrelated port is rejected; invalid parameter/JAVA_HOME is rejected; server exit or readiness timeout fails the pipeline and stops only the newly started process. Startup errors are retained in the deployment release folder.

The underlying Java command, with actual paths substituted, is:

```powershell
& "$env:JAVA_HOME/bin/java.exe" "-Dphysio.deployment=<deployment-directory>" -jar "<release-directory>/physio-portal-1.0.0.jar" --server.port=8082 --server.address=127.0.0.1 --spring.profiles.active=test
```

This explanatory command runs in the foreground; the script uses Windows `WScript.Shell.Run` with window style 0 (hidden), redirects all three streams, and waits up to 90 seconds for the child PID to own the healthy port. This launcher avoids the inherited Windows pipe that kept the initial Jenkins wrapper open. The database defaults to `data/physio` under the deployment directory, preserving state across redeployment of the same environment/port. Separate parameter combinations have separate databases. The process survives a successful build; it is not an installed auto-start Windows service.

### Independent verification

**Terminal:** PowerShell. **Location:** project root or any folder. These calls require no authentication:

```powershell
Invoke-RestMethod http://localhost:8082/actuator/health
Invoke-WebRequest http://localhost:8082/ | Select-Object StatusCode
Invoke-RestMethod http://localhost:8082/api/physiotherapists
Get-Content -Raw 'C:\ProgramData\Jenkins\physio-portal-demo\test-8082\deployment.json'
Get-FileHash 'C:\ProgramData\Jenkins\physio-portal-demo\test-8082\releases\3\physio-portal-1.0.0.jar' -Algorithm SHA256
```

**Expected:** `UP`, HTTP 200, two fictional providers, metadata matching the build parameters and source, and a hash matching the archived JAR. The release-number path above applies to build #3; use the actual `jar` path from metadata on later runs. **Common error:** connection refused means the application is not listening; inspect the specific build and logs before claiming deployment.

### Stop an owned demonstration deployment

**Terminal:** PowerShell opened as Administrator (Jenkins owns the process as LocalSystem). **Location:** project root. Use only when finished with that demo URL:

```powershell
./scripts/deploy-local.ps1 -Action stop -AppEnv test -Port 8082
```

**Expected:** the owned application process stops; database and releases remain. The script checks the saved PID, Java command, deployment marker, JAR path and process creation time before stopping it. A reused PID is rejected. An ordinary unelevated terminal may be unable to inspect or stop a LocalSystem process. Do not stop all Java processes: Jenkins also runs Java.

## Actual execution record

Initial run #1 checked out `2afdb87248f55f7a0bafc8a3fbba45497bb2508e`, passed 34 tests, packaged and started a healthy demo on 8081. The deployment's Windows wrapper kept waiting after startup. The run was explicitly stopped and finished **ABORTED**, not SUCCESS. Its [console](evidence/T08_console_initial.txt) and [build metadata](evidence/T08_build_initial.json) are retained. Correction commit `6af1dfd` replaces `Start-Process` with the documented Windows detached launcher.

Corrected [feature run #2](http://localhost:8080/job/physio-portal-pipeline/2/) completed **SUCCESS** on source `6af1dfdd4b49b7387a5f0ea426ac78c826e34460`, with 34 passing tests and all five stages. Parameters `demo`/`8081` started PID 16344; an independent post-build health check returned UP and the provider API returned two providers. The archived and deployed JAR SHA256 matched `D11F11B143FDBB4EFDAFD9AC9D74FDFA3C7A230203A564939D234F176639350F`. [Summary](evidence/T08_verified_feature.json), [console](evidence/T08_console_feature.txt), [JUnit report](evidence/T08_tests_feature.json), [metadata](evidence/T08_feature_deployment.json), [startup log](evidence/T08_feature_deploy-application.log), [job configuration](evidence/T08_job_feature.xml), and [screenshot](../screenshots/T08_feature_success.jpg) are saved. Final PR/integration verification is pending.

## Evidence and screenshot instructions

Screenshots are required. Capture real Jenkins/application pages after readiness; no staged or fabricated result is acceptable.

| Filename | Required visible content |
|---|---|
| `screenshots/T08_pipeline_stages.jpg` | Job/build identity, Success and Checkout/Build/Unit Test/Package/Deploy completed |
| `screenshots/T08_pipeline_success.jpg` | Successful final build, Git revision, JAR/deployment archives and passing test link |
| `screenshots/T08_parameters.jpg` | Actual final build's APP_ENV and PORT values |
| `screenshots/T08_job_config.jpg` | Pipeline script from SCM, repository, develop branch and Jenkinsfile path |
| `screenshots/T08_deployed_application.jpg` | Application home and running URL with selected port |
| `screenshots/T08_deployed_providers.jpg` | Two fictional providers rendered by the deployed server |

Additional machine-readable proof: build API, raw console, JUnit report, deployed metadata and startup logs, downloaded archive checksum, independent HTTP check, final Git/PR record. No credentials are included.

## Completion checklist

- [x] Versioned Jenkinsfile with five required stages
- [x] APP_ENV and PORT parameters affect server startup and deployment paths
- [x] Pipeline syntax validated by Jenkins
- [x] Successful feature pipeline with 34 passing tests and archived JAR
- [ ] Reviewed PR merged into develop
- [ ] Successful pipeline from develop with nondefault parameters
- [ ] Application survives build completion and responds at the selected URL
- [ ] Archived/deployed JAR hashes, configuration, logs and screenshots verified
- [ ] README, backlog, board and tracker updated

Task 9 Selenium browser automation begins only after the user asks to continue.
