# Physiotherapy Appointment Portal

**DevOps project:** Selenium Testing for a Physiotherapy Appointment Portal  
**Student/contributor:** Kirti Vispute (23102C0078)

## Project at a glance

A small clinic can lose track of appointment requests made by phone or message. This project proposes a patient portal for finding a physiotherapist, choosing a free slot, booking it, checking confirmation/status, and cancelling an eligible booking. It also demonstrates planning, Git collaboration, Jenkins CI, Selenium testing, Docker deployment, Ansible configuration, health checks, and rollback.

**Current verified state:** Tasks 1–5 are complete. The app serves patient registration, a homepage, and health. All 14 registration integration tests pass; [PR #1](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/1) has been self-reviewed and merged into `develop`. Sign-in and booking are planned for Task 6; Selenium, Jenkins, Docker, and Ansible remain for later tasks. See the [project tracker](docs/project-tracker.md).

## MVP features

| Feature | Current state |
|---|---|
| Homepage and health check | Verified locally in Task 3 |
| Patient registration | Verified UI/API in Task 5; salted password hashes, validation, and duplicate-email protection |
| Patient sign-in | Planned for Task 6 |
| Physiotherapist and open-slot views | Planned for Task 6 |
| Booking, confirmation, status, cancellation | Planned for Task 6 |
| Validation and duplicate-booking protection | Planned for Task 6 |

The [approved scope](docs/problem-definition.md) excludes payments, medical records, clinician accounts, and a complex administration dashboard.

## Architecture and stack

```text
Browser (Thymeleaf pages)
  → Spring MVC controllers and REST API
  → application services and JPA repositories
  → H2 file database

GitHub → Jenkins → Maven/JUnit → Selenium → versioned Docker image
         → registry → container → health check / rollback
Ansible configures the documented Linux target.
```

The app uses Java 21, Spring Boot 4.0.8, Maven, Thymeleaf, H2, and embedded Tomcat. Delivery uses Git/GitHub, Jenkins, Selenium, Docker Desktop, and Ansible from WSL Ubuntu. The detailed [SRS](docs/srs.md), [architecture diagrams and data model](docs/architecture.md), and [API contract](docs/api-documentation.md) distinguish implemented routes from planned ones. No external Tomcat or Nginx instance is required.

## Repository layout

```text
.github/                  Issue and pull-request templates
docs/                     Scope, planning, architecture, API, setup, tracker
screenshots/              Real demonstration images
src/main/java/            Spring Boot application source
src/main/resources/       Configuration and Thymeleaf pages
src/test/java/            JUnit integration tests
pom.xml                   Maven build definition
.gitignore                Local/build files excluded from Git
```

`Jenkinsfile`, `Dockerfile`, `ansible/`, and deployment scripts will be added in their assigned tasks. Empty placeholder files are not used as proof of implementation.

## Local setup and running

**Terminal:** PowerShell on Windows 11. **Location:** Project root. Java 21 and Maven 3.6.3+ are required. The exact path, commands, expected outputs, and troubleshooting notes are in the [local setup guide](docs/local-setup.md).

```powershell
mvn clean package
$env:PORT='8081'
java -jar target/physio-portal-0.1.0-SNAPSHOT.jar
```

The build creates an executable JAR; the second command sets a local port because 8080 was occupied during Task 3 verification; the third starts the app. Expected: Maven `BUILD SUCCESS` and Spring Boot startup on port 8081. If Maven cannot download dependencies, check network access to Maven Central. If the port is occupied, choose another free `PORT` in the same terminal. Press `Ctrl+C` to stop the app.

In a **second PowerShell terminal, from any location**, run:

```powershell
Invoke-WebRequest -Uri 'http://localhost:8081/' -UseBasicParsing | Select-Object StatusCode
Invoke-RestMethod -Uri 'http://localhost:8081/actuator/health'
```

These check the homepage and health. Expected: HTTP 200 and `status: UP`. If the connection is refused, check that the application is still running and that the port matches. The Task 3 run produced those results; the app is not left running.

## Testing

**Terminal:** PowerShell at the project root. Run `mvn clean verify` to execute JUnit integration tests and package the app. Expected: `Tests run: 14, Failures: 0, Errors: 0, Skipped: 0` and `BUILD SUCCESS`. This result was observed for registration in Task 5. Tests use an isolated H2 memory database; the app uses `data/physio.mv.db`. If a test fails, inspect `target/surefire-reports` before committing. See the [registration implementation guide and evidence](docs/task-05-registration.md).

Selenium is planned for Task 9; no Selenium test has run yet. Its five journeys will cover registration, open-slot view, booking, cancellation, and status. The [user stories](docs/user-stories.md) define their acceptance criteria.

## Git and collaboration

The [Git workflow](docs/git-workflow.md) defines `main`, `develop`, and `feature/<short-name>`, meaningful commit messages, PR review, conflict demonstration, and tagging. Both baseline branches have been pushed and verified. Never force-push or commit credentials or the H2 data directory.

**GitHub repository URL:** [kirti-vispute/physiotherapy-appointment-portal](https://github.com/kirti-vispute/physiotherapy-appointment-portal)

## Jenkins CI and pipeline

**Planned for Tasks 7–10; not installed or verified yet.** Jenkins will check out this repository, run Maven build and unit tests, start the app, run Selenium, publish reports, archive the JAR, and deploy only when tests pass. A `Jenkinsfile` will be added in Task 8. The job trigger will use a practical GitHub webhook or SCM polling, with the chosen method documented. Plugin requirements and exact setup steps will be written and checked during Task 7.

## Docker deployment

**Planned for Tasks 11–12; no image or container exists yet.** A root `Dockerfile` will package the executable JAR. The image will receive a versioned tag, be pushed to a registry, and be deployed by Jenkins only after tests pass. The container will map a host port to its application port and mount a persistent H2 data location. Lifecycle commands, IDs, logs, and health results will be added to `docs/docker.md` when actually run.

## Ansible configuration and reliability

**Planned for Tasks 13–14; no playbook has run yet.** Ansible from WSL Ubuntu will configure the documented Linux target, application folders/files, and container deployment, then verify health. A second run will demonstrate idempotency. A bad-release simulation and rollback to a known good version will be documented with actual logs. Docker Desktop on Windows is a host prerequisite, not something the Linux playbook claims to install. Exact commands will live in `ansible/README.md` after the playbook exists.

## Project workflow and evidence

Requirements → planning → architecture → feature branches/PRs → Maven/Jenkins → Selenium gate → Docker image/registry → deployment → Ansible configuration → health check → feedback/rollback. The [agile plan](docs/agile-plan.md) has the lifecycle diagram, backlog, sprints, and Definition of Done. The [tracker](docs/project-tracker.md) records the status and evidence for all 15 tasks.

### Screenshots

![Task 3 local application](screenshots/T03_local_application.png)

![Task 4 initial GitHub publication](screenshots/T04_repository.jpg)

Task 4 also includes [branch evidence](screenshots/T04_branches.jpg) and [initial commit evidence](screenshots/T04_commits.jpg). These capture the initial publication before the subsequent evidence documentation commit.

Additional images will be added only after the corresponding GitHub, Jenkins, Selenium, Docker, or Ansible step has actually been demonstrated. The screenshot checklist and final audit will link to them.

![Task 5 registration success](screenshots/T05_registration_success.jpg)

[Task 5 duplicate-email message](screenshots/T05_registration_duplicate.jpg)

Task 5 GitHub evidence: [pull request](screenshots/T05_pull_request.jpg), [self-review](screenshots/T05_review.jpg), and [merged PR](screenshots/T05_merge.jpg). Commands, test results, actual SHAs, and screenshot instructions are in the [Task 5 guide](docs/task-05-registration.md).

## Contributors

- **Kirti Vispute**, student and project owner, roll number 23102C0078.
