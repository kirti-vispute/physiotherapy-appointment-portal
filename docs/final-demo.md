# Final viva and live demonstration sequence

**Project:** Selenium Testing for a Physiotherapy Appointment Portal  
**Presenter:** Kirti Vispute (23102C0078)  
**Environment:** one Windows 11 computer, GitHub Free, local Jenkins, local Docker Desktop registry, Ubuntu WSL with Ansible. No paid cloud account is required.

This is a **rehearsal script**, not a claim that the future viva actions have already happened. The saved [Task 7](task-07-jenkins-ci.md), [Task 12](task-12-docker-cd.md), and [Task 14](task-14-provisioning.md) evidence can be shown if a live build is too slow. Explain clearly that `physio-portal-ci` polls GitHub automatically, while `physio-portal-pipeline` currently requires a manual **Build with Parameters**. A manual pipeline build must not be described as an automatic trigger.

## Before presenting

**PowerShell, repository root:** inspect the branch and local services. Expected: clean `develop`, GitHub `origin`, Jenkins login page or dashboard, registry HTTP 200, Docker Desktop engine, and health `UP` for running portals. If a service is unavailable, use its task guide; do not delete a container, volume, or database to make the demo look clean.

```powershell
git status --short --branch
git remote -v
docker version
Invoke-WebRequest http://127.0.0.1:5000/v2/ -UseBasicParsing | Select-Object StatusCode
Invoke-RestMethod http://127.0.0.1:8087/actuator/health
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/start-ansible-target.ps1
Invoke-RestMethod http://127.0.0.1:8089/actuator/health
```

The workspace's Git ownership workaround is documented in [Task 4](git-workflow.md); it is specific to this machine's sandbox account. If Jenkins requires sign-in, use the existing local account. During the Task 15 audit an unauthenticated Jenkins API request returned HTTP 403; do not treat that as a build failure. If the source checkout is dirty or a feature branch already exists, finish or preserve the work before making the viva edit.

## 1. Show the GitHub repository

**Browser:** open [the public repository](https://github.com/kirti-vispute/physiotherapy-appointment-portal). Show `README.md`, `src/`, `docs/`, `Jenkinsfile`, `Dockerfile`, `ansible/`, and `screenshots/`. **Expected:** the URL and files are visible; the current integration branch is `develop`. [Task 4 screenshot](../screenshots/T04_repository.jpg) is historical backup.

## 2. Show the application

**Browser:** open [localhost:8087](http://localhost:8087/) and `/physiotherapists`. Use fictional data to register, sign in, choose a future free slot, book it, inspect status, and cancel it. **Expected:** visible confirmation/status and the released slot. If no future slot appears, check the fictional seed setup in [Task 6](task-06-mvp.md); do not insert real patient data. Use the [Task 6 screenshots](task-06-mvp.md) as backup.

## 3. Show branches and a reviewed PR

**PowerShell, repository root:**

```powershell
git branch -a
git tag -l
git log --oneline --decorate -n 8
```

**Browser:** open [PR #2](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/2) and its merge/review record. **Expected:** `main`, `develop`, feature branches, `v1.0.0`, and meaningful commits. Explain that a COMMENT self-review records the single-contributor review, not independent approval. A missing remote branch means `git fetch origin` may be needed.

## 4. Show Jenkins

**Browser:** open [local Jenkins](http://localhost:8080/), sign in, then show `physio-portal-ci` and `physio-portal-pipeline`. **Expected:** Task 7 freestyle CI has Poll SCM; the Pipeline job reads the committed `Jenkinsfile`. Show [historical CI auto-trigger](../screenshots/T07_scm_trigger.jpg) and [pipeline stages](../screenshots/T08_pipeline_stages.jpg) if no new build has begun yet. A login page means the presenter must sign in before using the private job pages.

## 5. Make a small code change

**PowerShell, repository root:** first confirm a clean `develop`, then branch:

```powershell
git status --short --branch
git switch -c feature/viva-home-copy
```

**Editor:** in `src/main/resources/templates/index.html`, change only the homepage heading `Your next step towards better movement` to `Plan your next step towards better movement`. Do not alter `data-testid` attributes, paths, or booking logic. **Expected:** `git diff -- src/main/resources/templates/index.html` shows a one-line text change. The current Selenium suite does not assert this heading, but the CI result is the authority. If the branch name already exists, choose a new descriptive `feature/<name>` branch from clean `develop`.

## 6. Push to GitHub and merge the PR

**PowerShell, repository root:**

```powershell
git diff --check
git add src/main/resources/templates/index.html
git commit -m "docs: refine homepage welcome text"
git push -u origin feature/viva-home-copy
```

**Browser:** open GitHub, create a PR **from `feature/viva-home-copy` into `develop`**, describe the one-line change and tests, inspect the diff, record a COMMENT review, and merge when it is ready. **Expected:** a new merge commit appears on `develop`; [Task 5](task-05-registration.md) demonstrates the same branch/PR/review path. The automatic CI job watches `develop`, so a feature-branch push alone does not prove its trigger. Then sync locally:

```powershell
git switch develop
git pull --ff-only origin develop
git status --short --branch
```

If the PR reports a conflict, follow the [Git guide](git-workflow.md); do not force-push `develop`.

## 7. Show Jenkins starting automatically

**Browser:** after the `develop` merge, open `physio-portal-ci` → **Git Polling Log** and the next build. Poll SCM uses `H/2 * * * *`; wait for its schedule and queue. **Expected:** a new build cause says **Started by an SCM change** and the checked-out commit matches the new `develop` merge. [Task 7's recorded automatic build #2](evidence/T07_build_scm.json) is backup. Do not click Build Now to claim this automatic trigger. If nothing starts, inspect Polling Log and the branch/repository settings in [Task 7](task-07-jenkins-ci.md).

## 8. Show Maven building

**Jenkins:** open that CI build's Console Output, JUnit view, and archived JAR. **Expected:** checkout, `mvn clean test`, `mvn package`, 34 passing backend tests, and a packaged artifact. On failure, read the first Maven error and do not continue to a claimed successful release.

## 9. Run the Selenium deployment pipeline

**Jenkins:** open `physio-portal-pipeline` → **Build with Parameters** → `APP_ENV=test`, `DOCKER_PORT=8087` → Build. This is a **manual start** of the deployment pipeline. It checks out the newly merged `develop` commit, starts a temporary test app on port 8091, and runs five Chrome browser journeys after the backend build. **Expected:** `Selenium Tests` and `Publish Test Report` stages appear. This separation is necessary because the saved Pipeline job has no SCM trigger; see the [actual configuration](evidence/T12_job_develop.xml).

## 10. Show tests passing

**Jenkins:** open the pipeline's JUnit/test report and Selenium HTML artifact. **Expected:** 34 backend and five browser tests, zero failures/errors/skips, with registration, slot, booking, cancellation, and status named. If Selenium fails, the pipeline must stop before Docker deployment, as [Task 10](task-10-continuous-testing.md) proves.

## 11. Show the Docker image build

**Jenkins:** open `Docker Build` stage and archived `target/docker-build.log`. **Expected:** successful build from the committed [Dockerfile](../Dockerfile) using the tested JAR. A Docker daemon error requires the [Task 11 engine check](task-11-docker.md); do not claim an image was built.

## 12. Show the versioned image published

**Jenkins:** open `Docker Tag` and `Docker Push` stage logs. **PowerShell, repository root:**

```powershell
Invoke-WebRequest http://127.0.0.1:5000/v2/_catalog -UseBasicParsing | Select-Object StatusCode,Content
```

**Expected:** a `1.0.0-b<build>-<commit>` tag and push digest in the stage log, with `physio-portal` in the local registry. The catalog alone does not prove the exact new tag; use the saved deployment JSON or push log. The registry is loopback, so no paid Docker Hub account is involved.

## 13. Show the new container

**Jenkins:** open `Stop Previous Container`, `Run New Container`, and `Health Check`; inspect archived `target/docker-deployment.json`. **PowerShell, repository root:**

```powershell
docker ps --filter name=physio-portal-cd-test
Invoke-RestMethod http://127.0.0.1:8087/actuator/health
```

**Expected:** prior owned container removed, a new container ID/commit label, port 8087, and `UP`. Compare before/after IDs; [Task 12](task-12-docker-cd.md) contains an actually verified historical replacement. If the pipeline fails before deployment, the previous healthy container should remain; do not stop it manually.

## 14. Show Ansible configuring the separate target

**PowerShell, repository root:** the Task 14 Ubuntu target is a **separate Docker Engine** and currently uses the pinned known good Task 12 image. It does not automatically follow the new viva image. Run the helper and normal playbook:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/start-ansible-target.ps1
$linuxProject = '/mnt/d/Kirti/__VIT RELATED/Lab Experiments/DEVOPS/DevOps Project'
wsl -d Ubuntu-24.04 -u root -- ansible-playbook -i "$linuxProject/ansible/inventory.ini" "$linuxProject/ansible/site.yml"
```

**Expected:** Docker, account, folder, environment, volume and container correct; an already-correct target reports `changed=0 failed=0`. The [first](evidence/T14_provision_first.txt) and [second](evidence/T14_provision_second.txt) saved logs prove original provisioning and idempotency. If WSL or its Docker socket is down, use [Task 14's smallest safe checks](task-14-provisioning.md).

## 15. Show the health check

**PowerShell, repository root:**

```powershell
Invoke-RestMethod http://127.0.0.1:8089/actuator/health
(Invoke-WebRequest http://127.0.0.1:8089/physiotherapists -UseBasicParsing).StatusCode
```

**Expected:** `status: UP` and `200`. The [final live record](evidence/T15_live_checks.json) also checked 8082 and 8087. If the local WSL target stopped after reboot, restart its keeper and rerun the normal playbook.

## 16. Show rollback and recovery

**PowerShell, repository root:** only do this during a planned live demo because the Ubuntu app briefly becomes unavailable. The override simulates a bad **port configuration**, not a different image. Save the bad-run failure, then immediately reapply the known good configuration:

```powershell
wsl -d Ubuntu-24.04 -u root -- ansible-playbook -i "$linuxProject/ansible/inventory.ini" "$linuxProject/ansible/site.yml" -e portal_application_port=8099 -e portal_health_retries=3 -e portal_health_delay=2
$LASTEXITCODE
wsl -d Ubuntu-24.04 -u root -- ansible-playbook -i "$linuxProject/ansible/inventory.ini" "$linuxProject/ansible/site.yml"
Invoke-RestMethod http://127.0.0.1:8089/actuator/health
```

**Expected:** the bad run exits **2** with `failed=1` at the health gate; the normal run ends `failed=0` and health returns `UP`, retaining the pinned image and H2 volume. Show the [saved bad](evidence/T14_bad_release.txt), [rollback](evidence/T14_rollback.txt), and [recovered state](evidence/T14_recovered_state.json) if the live demonstration is skipped. If recovery fails, inspect container logs as described in [Task 14](task-14-provisioning.md) before changing any image or volume.

## Closing explanation

Explain the two quality gates: Selenium failure stopped deployment in Task 10, and Ansible health failure exposed a bad configuration in Task 14. Show the [final audit](final-audit.md). The presenter can truthfully say the **freestyle CI build is automatically triggered after a `develop` change**, while the **Docker deployment pipeline is manually started with parameters**. A fully automatic trigger for that second job is future work.
