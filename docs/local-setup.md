# Task 3 — Local Development Setup

**Host:** Windows 11  
**Terminal for all commands here:** PowerShell  
**Working directory:** `D:\Kirti\__VIT RELATED\Lab Experiments\DEVOPS\DevOps Project` unless stated otherwise. Paths contain spaces, so use the quoted form when changing directories.

## Prerequisites and observed versions

| Tool | Required | Observed on 2 October 2026 |
|---|---|---|
| Java | JDK 21 | `java 21.0.11` |
| Maven | 3.6.3 or later | `Apache Maven 3.9.16` |
| Git | Needed from Task 4 | Not checked for Task 3 |
| Browser | Needed for later Selenium tests | Not checked for Task 3 |

Spring Boot 4.0.8 supports this Java/Maven combination; see [Spring Boot system requirements](https://docs.spring.io/spring-boot/4.0/system-requirements.html). The first Maven build needs Maven Central network access to download uncached dependencies.

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
   $env:PORT='8081'
   java -jar target/physio-portal-1.0.0.jar
   ```

   This sets the local demo port and starts embedded Tomcat. Expected: startup log showing the application ready on port 8081. Port 8080 was already occupied on this machine during Task 3. If 8081 is also occupied, choose another free port and use it in the checks below. If the H2 path cannot be written, check project-folder permissions; the app creates `data/` locally.

4. **A second PowerShell terminal, from any directory:**

   ```powershell
   Invoke-WebRequest -Uri 'http://localhost:8081/' -UseBasicParsing | Select-Object StatusCode
   Invoke-RestMethod -Uri 'http://localhost:8081/actuator/health'
   ```

   The first request checks the home page; expected status `200`. The second checks health; expected `status : UP`. If connection is refused, confirm the application terminal is still running and the chosen port matches. If startup failed, read the first exception in that terminal.

5. **Application terminal:** Press `Ctrl+C` to stop the local app. Expected: the Java process ends and port 8081 becomes free. Keep the `data/` directory; it is ignored by Git and holds the patient and appointment data.

## Configurable values

| Variable | Default | Purpose |
|---|---|---|
| `PORT` | `8080` | Embedded Tomcat HTTP port |
| `DEMO_SEED_ENABLED` | `true` | Seed fictional providers and next-three-day slots on startup; set `false` to disable |
| `DB_URL` | `jdbc:h2:file:./data/physio;DB_CLOSE_ON_EXIT=FALSE` | H2 file path relative to the application working directory |

In PowerShell, set a variable in the same terminal before `java -jar`, for example `$env:PORT='8081'`. Expected: startup on 8081. The values are for the local demo, not credentials. At the Task 3 baseline, patient routes did not yet exist. The current Task 6 MVP includes registration, sign-in, provider/slot views, booking, status, and cancellation.

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

The actual working local URL for this Task 3 run was `http://localhost:8081/`. This was a temporary local run, not a deployed service. Future pipeline/container ports will be specified and checked in their tasks.
