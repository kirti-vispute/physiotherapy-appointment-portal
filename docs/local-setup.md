# Local development setup and service guide

**Updated:** 5 October 2026; original Task 3 verification is retained below.

**Host:** Windows 11

**Terminal for all commands here:** PowerShell

**Working directory on the demonstrated computer:** `D:\Kirti\__VIT RELATED\Lab Experiments\DEVOPS\DevOps Project` unless stated otherwise. Paths contain spaces, so use the quoted form when changing directories.

## Prerequisites and observed versions

| Tool | Required | Observed on 2 October 2026 |
|---|---|---|
| Java | JDK 21 | `java 21.0.11` |
| Maven | 3.6.3 or later | `Apache Maven 3.9.16` |
| Git | Required to clone/current branch | Original Task 3 did not check Git; Task 4 verified 2.53.0 |
| Browser | Chrome for Selenium; any browser for patient pages | Original Task 3 did not check Chrome; Task 9 verified Chrome 154 |

Spring Boot 4.0.8 supports this Java/Maven combination; see [Spring Boot system requirements](https://docs.spring.io/spring-boot/4.0/system-requirements.html). The first Maven build needs Maven Central network access to download uncached dependencies.

For a fresh checkout, run `git clone --branch develop https://github.com/kirti-vispute/physiotherapy-appointment-portal.git`, then change into the cloned folder. Substitute that folder for the machine-specific path below. `main` is the earlier Task 6 baseline.

## Build and run

1. **PowerShell, from any directory:**

   ```powershell
   Set-Location -LiteralPath 'D:\Kirti\__VIT RELATED\Lab Experiments\DEVOPS\DevOps Project'
   ```

   This selects the project root. Expected: the prompt changes to that folder. If the path is not found, check the exact drive and folder names.

2. **PowerShell, project root:**

   ```powershell
   mvn clean package
   ```

   Maven removes previous build output, compiles, runs available tests, and creates an executable JAR. Expected: `BUILD SUCCESS` and `target/physio-portal-1.0.0.jar`. On the first run, dependency downloads may take time. If `mvn` is not recognized, check Maven on `PATH`; if a dependency cannot resolve, check network access to Maven Central; if compilation fails, inspect the first compiler error rather than reinstalling tools.

3. **PowerShell, project root, keep this terminal open:**

   ```powershell
   $env:PORT='8083'
   java -jar target/physio-portal-1.0.0.jar
   ```

   This sets the local demo port and starts embedded Tomcat. Expected: startup log showing the application ready on port 8083. Jenkins uses 8080 on this computer; the manual app uses 8083. If 8083 is also occupied, choose another free port and use it in the checks below. If the H2 path cannot be written, check project-folder permissions; the app creates `data/` locally.

4. **A second PowerShell terminal, from any directory:**

   ```powershell
   Invoke-WebRequest -Uri 'http://localhost:8083/' -UseBasicParsing | Select-Object StatusCode
   Invoke-RestMethod -Uri 'http://localhost:8083/actuator/health'
   ```

   The first request checks the home page; expected status `200`. The second checks health; expected `status : UP`. If connection is refused, confirm the application terminal is still running and the chosen port matches. If startup failed, read the first exception in that terminal.

5. **Application terminal:** Press `Ctrl+C` to stop the local app. Expected: the Java process ends and port 8083 becomes free. Keep the `data/` directory; it is ignored by Git and holds the patient and appointment data.

## Configurable values

| Variable | Default | Purpose |
|---|---|---|
| `PORT` | `8080` | Embedded Tomcat HTTP port |
| `DEMO_SEED_ENABLED` | `true` | Seed 15 fictional providers and six slots each across the next three days; set `false` to disable |
| `DB_URL` | `jdbc:h2:file:./data/physio;DB_CLOSE_ON_EXIT=FALSE` | H2 file path relative to the application working directory |

In PowerShell, set a variable in the same terminal before `java -jar`, for example `$env:PORT='8083'`. Expected: startup on 8083. The values are for the local demo, not credentials. At the Task 3 baseline, patient routes did not yet exist. The current Task 6 MVP includes registration, sign-in, provider/slot views, booking, status, and cancellation.

## Local ports and persistence

| Port | Purpose |
|---|---|
| 8080 | Windows Jenkins; also the app port inside each container |
| 8083 | Manual app used in current quick-start instructions |
| 8087 | Current Jenkins test container on Docker Desktop |
| 8088 | Optional Jenkins Docker deployment port |
| 8091 | Temporary isolated Jenkins Selenium app |
| 8089 | Separate Ubuntu WSL Ansible demo pinned to build #9 |
| 5000 | Loopback local Docker image registry |
| 8081/8082 | Original Task 3/8 JAR deployment ports; historical evidence does not establish current uptime |

The manual H2 path is relative to the working directory. Keep `data/physio.mv.db` and restart from the same folder. The Jenkins container uses named volume `physio-portal-cd-test-data`; Ubuntu uses `physio-portal-ansible-data` on its separate engine. Do not remove volumes to restart. A fresh clone has no patient accounts. Seed startup preserves existing IDs, bookings and occupied slots; see [seed data](seed-data.md).

## Jenkins and Docker prerequisites

The full pipeline is a Windows lab setup, not a one-command installation on a new computer. Configure the built-in Jenkins agent with Java 21 named `JDK21`, Maven named `Maven3`, Git and Chrome. Required plugin/configuration evidence is in [Task 7](task-07-jenkins-ci.md). Set the Pipeline SCM to this repository's `develop` and script path `Jenkinsfile`; it uses Poll SCM every two minutes, a 20-minute timeout and serialized builds.

Install Docker Desktop with the Linux engine and prepare the loopback registry as described in [Task 12](task-12-docker-cd.md). `scripts/docker-cd.ps1` contains this computer's Docker CLI/plugin paths under `C:/Users/kirti/AppData/Local/Programs/DockerDesktop`; adjust those paths to your actual installation on another machine. Jenkins must be able to use that engine. `scripts/deploy-local.ps1` is the earlier Task 8 JAR launcher, retained for its demonstration. Current delivery uses `scripts/docker-cd.ps1` and the committed pipeline.

Ansible needs Ubuntu 24.04 WSL and its own Linux Docker Engine. Follow [the Ansible prerequisites](../ansible/README.md) before the [Task 14 site playbook](task-14-provisioning.md). Its pinned historical image is intentional; it does not track the current 15-provider application.

## Restart the existing demonstration

These commands apply to the previously provisioned computer, where the named containers already exist. Start Docker Desktop and wait until its engine is ready. In PowerShell, with the Docker CLI on `PATH`:

```powershell
docker version
docker start physio-local-registry physio-portal-cd-test
Invoke-WebRequest -Uri 'http://127.0.0.1:5000/v2/' -UseBasicParsing | Select-Object StatusCode
Invoke-RestMethod -Uri 'http://127.0.0.1:8087/actuator/health'
```

Expected: Docker server details, registry HTTP 200 and app health UP after startup. An absent container means initial provisioning/deployment is needed through Task 12; a stopped engine must start before container commands work. Inspect `docker logs physio-portal-cd-test` if health fails. For this host's recorded transient socket error, see [the recovery record](evidence/T15_docker_recovery.md); that recovery is specific to the observed failure, not a routine startup requirement.

For the separate Ubuntu demonstration, from the repository root:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/start-ansible-target.ps1
Invoke-RestMethod -Uri 'http://127.0.0.1:8089/actuator/health'
```

The helper keeps the existing Ubuntu engine available. If its container was not provisioned or health is not UP, use the Task 14 playbook/diagnostics. Jenkins is a Windows service; inspect/start the installed Jenkins service if localhost:8080 is unavailable, then sign in with the operator account. No credentials are supplied by the repository. For a simple application run without Jenkins/Docker, use the manual build/run above.

## Verification record

**Actual Task 3 results, 2 October 2026:**

| Check | Observed result |
|---|---|
| `java -version` | Java 21.0.11 |
| `mvn -version` | Maven 3.9.16 |
| Initial `mvn clean package` | Failed because the sandbox denied Maven Central network access; no Spring Boot parent was cached |
| Retried `mvn clean package` with network permission | `BUILD SUCCESS`; executable JAR created at `target/physio-portal-0.1.0-SNAPSHOT.jar` |
| First startup on default port 8080 | Failed because another process already used port 8080 |
| Startup with `PORT=8081` | Spring Boot 4.0.8 started with embedded Tomcat 11.0.24 on port 8081 |
| `GET http://localhost:8081/` | HTTP 200; expected setup text present |
| `GET http://localhost:8081/actuator/health` | `status: UP` |
| H2 file | `data/physio.mv.db` exists |
| Browser screenshot | `screenshots/T03_local_application.png` captured from the running page and visually checked |
| Shutdown | Application Java process stopped |
| Final configuration recheck | Rebuilt after restricting Actuator exposure to health only; application restarted on 8081; home returned HTTP 200 and health returned `UP`; process stopped again |

The actual working local URL for this Task 3 run was `http://localhost:8081/`. This was a temporary local run, not a deployed service. Later task guides record their executed pipeline/container ports; the current port map is above.
