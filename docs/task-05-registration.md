# Task 5 — Feature Development with Branching

## Objective

Implement US-01 patient registration on `feature/user-registration`, test it, publish a pull request, record review comments, and merge into `develop`.

## Requirements and deliverables

| Requirement | Deliverable | Status |
|---|---|---|
| Working Feature 1 | Registration form, API, H2 persistence, validation, salted password hashes | ✅ Verified |
| Feature branch | `feature/user-registration` from `develop` | ✅ Created |
| Demonstrate status/add/commit/push/log | Meaningful feature and test commits with saved Git transcript | ✅ Published |
| Pull request title/description/changes/testing/screenshots | GitHub PR #1 into `develop` | ✅ Created and verified |
| Review comments | Explicitly labeled single-owner self-review | ✅ COMMENT review published |
| Merge evidence | GitHub merged PR, merge SHA, local `develop` sync | ✅ Verified |
| Screenshot guidance | Instructions and real registration screenshots below | ✅ Created |

The first feature is registration. Sign-in (US-02) is implemented with the remaining MVP in Task 6. This changes the task allocation only; the frozen scope is unchanged. US-01's Selenium DoD will be verified in Task 9.

## Step-by-step implementation

1. Started from clean `develop` at the verified Task 4 baseline and created `feature/user-registration`.
2. Added `Patient`, a JPA repository, request/response objects, and a shared registration service. The database enforces unique normalized email.
3. Added Thymeleaf `/register` and JSON `POST /api/auth/register`. Both validate names, email, and password before saving. The API never returns password/hash; HTML never fills the password back into the form.
4. Added Spring Security Crypto PBKDF2 password hashing with random salt. Authentication is scheduled for Task 6.
5. Added 14 integration test cases against a real HTTP server and an isolated H2 memory database. They verify valid registration, blank/missing fields, malformed email, password bounds, malformed JSON, duplicate email, concurrent registration, database uniqueness, hash verification/salts, HTML escaping, error rendering, and redirect/refresh behavior.
6. The first test run failed one redirect assertion because a session ID appeared in the URL. Set `server.servlet.session.tracking-modes=cookie` and reran the full build: 14 passed; `BUILD SUCCESS`. The original failure log retains a redaction of the test session ID.
7. Used the real browser to submit fictional patient Asha Patil, `asha.task5@example.test`, password `example123`; observed success. Resubmitted the uppercased email; observed the duplicate field message and empty password.
8. Checked API creation (`201`), duplicate (`409`), and health (`UP`), then checked persistence after restarting the packaged app.
9. Publish the branch, create the PR, self-review the diff/test results, and merge only after verification. Actual PR/review/merge identifiers are recorded below when returned by GitHub.

## Files to create/change

All paths below are relative to the project root `D:\Kirti\__VIT RELATED\Lab Experiments\DEVOPS\DevOps Project`.

- `pom.xml`: password-crypto dependency managed by the Spring Boot parent.
- `src/main/java/com/kirtivispute/physio/patient/`: patient entity/repository, validation DTOs, service, password configuration, page/API controllers, API error handling.
- `src/main/resources/templates/register.html`, `templates/index.html`, `static/css/portal.css`: accessible registration form and homepage navigation.
- `src/main/resources/application.properties`: cookie-only sessions.
- `src/test/java/com/kirtivispute/physio/patient/RegistrationIntegrationTest.java`: real HTTP/database tests.
- `README.md`, `docs/api-documentation.md`, planning/tracker documents: current behavior and evidence.
- `docs/evidence/T05_*`, `screenshots/T05_*`: actual build, browser, Git, and GitHub results.

## Commands

**Terminal:** Windows PowerShell. **Directory:** Project root. These commands reproduce the workflow; branch creation and commits have already been performed, so do not repeat them on the completed branch.

```powershell
Set-Location -LiteralPath 'D:\Kirti\__VIT RELATED\Lab Experiments\DEVOPS\DevOps Project'
git switch develop
git switch -c feature/user-registration
git status --short --branch
mvn clean verify
git add pom.xml src/main
git diff --cached --stat
git commit -m "feat: add patient registration with validated input and salted password hashes"
git add src/test
git commit -m "test: verify registration validation persistence and duplicate protection"
git add README.md docs screenshots
git commit -m "docs: record verified registration workflow and evidence"
git push -u origin feature/user-registration
git log --oneline --decorate -n 6
```

| Command group | Purpose | Expected result | Common error/fix |
|---|---|---|---|
| `Set-Location` | Select repository | Prompt in project root | Path not found: check the exact folder |
| `git switch` | Create branch from `develop` | Feature branch selected | Branch exists: use `git switch feature/user-registration`; don't recreate |
| `git status` | Show branch and working files | Changes on feature branch, later clean | Wrong branch: inspect before staging |
| `mvn clean verify` | Test and package | 14 tests pass; `BUILD SUCCESS`; executable JAR | Test failure: inspect `target/surefire-reports`; download failure: check Maven Central connectivity |
| `git add` / `git diff` | Stage/review selected source | Source/docs/images only | Never stage ignored database, build output, or credentials |
| `git commit` | Record focused changes | New SHA and meaningful message | Nothing to commit: inspect existing log/status |
| `git push` | Publish feature branch | Tracking `origin/feature/user-registration` | Authentication failure: sign in through Git Credential Manager |
| `git log` | Show actual commits | Feature/test/docs commit messages | Wrong branch: select feature or view `--all` |

If Git reports dubious ownership on this existing checkout, use this **process-only** trust setting before the Git commands. It is needed because `.git` was originally created by the Codex sandbox account; no global trust change was made.

```powershell
$env:GIT_CONFIG_COUNT='1'
$env:GIT_CONFIG_KEY_0='safe.directory'
$env:GIT_CONFIG_VALUE_0='D:/Kirti/__VIT RELATED/Lab Experiments/DEVOPS/DevOps Project'
```

Start the packaged app in the same PowerShell directory:

```powershell
$env:PORT='8081'
java -jar target/physio-portal-0.1.0-SNAPSHOT.jar
```

Expected: startup on port 8081. Open `http://localhost:8081/register`. A refused connection means the app has not started; an occupied port requires a free `PORT`. Press `Ctrl+C` to stop. The verification app is stopped after this task.

## Verification

- `mvn clean verify` passed all 14 tests with zero failures/errors/skips.
- Actual UI success and duplicate-email messages are saved below.
- Actual API returned `201` with patient summary only and `409` for the repeated email; health reported `UP`.
- Test logs contain expected H2 uniqueness warnings from deliberately rejected duplicates; they are not failed tests.
- Test data is fictional. The H2 database and packaged JAR are ignored by Git.
- No Selenium, independent peer approval, or Jenkins execution is claimed by this task.

## Pull request, review, and merge

| Item | Actual result |
|---|---|
| PR | [#1 — feat: add patient registration](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/1) |
| Feature commit | `6b980264a4d7ba3f33f3b037531eb651738b64e4` |
| Test commit | `d6b3a68381690b9c696ec6bfceecba6ab476fb55` |
| Documentation / reviewed head | `aa8cae957e64de27e9b3b54fc23eee1e4c40d9b4` |
| Review | [5393019879 — COMMENTED](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/1#pullrequestreview-5393019879), by `kirti-vispute`, assisted by Codex |
| Merge into `develop` | `f7713f0406c93747c7909041482f3682267edd0c`, 2 October 2026 14:34:13 UTC |
| Local verification | Fetched and fast-forwarded `develop`; diff of `pom.xml` and `src` against tested feature is empty |

The review checked duplicate/concurrent protection, password hashes/non-disclosure, UI validation/escaping, cookie-only redirect correction, and scope/evidence. No blocking Task 5 findings remained. GitHub shows **Merged**. No independent peer approval or automated GitHub check is claimed. The PR was created, reviewed, and merged through GitHub's REST API using the existing Git Credential Manager sign-in; the public browser page was used for verification/screenshots. Credentials were held in memory and never printed or saved.

The final completion tracker and PR/review/merge artifacts are published in a subsequent documentation commit on `develop`, since the merge's own SHA and final screenshots exist only after merging. The registration feature's reviewed source remains unchanged.

To reproduce through the GitHub UI: choose base `develop`, compare `feature/user-registration`; title the PR `feat: add patient registration`; describe purpose/changes/tests and link the two screenshots. Inspect Files changed and test logs, add a review with `Comment` labeled self-review, then use **Create a merge commit**. An author cannot give their own PR independent approval.

After GitHub merges, synchronize locally in PowerShell at the project root:

```powershell
git fetch origin
git switch develop
git pull --ff-only origin develop
git status --short --branch
git log --graph --oneline --decorate -n 8
```

Expected: clean `develop` at the merge commit, with both feature parents in history. If the pull cannot fast-forward, inspect local changes/history rather than force-pushing.

## Evidence and screenshot instructions

| File | What it proves |
|---|---|
| `docs/evidence/T05_maven_initial_failure.txt` | Real redirect-test failure; test session ID redacted |
| `docs/evidence/T05_maven_verify.txt` | Corrected build and 14 passing tests |
| `docs/evidence/T05_registration_tests.txt` | Surefire summary |
| `docs/evidence/T05_local_http.txt` | Real API, health, UI, and persistence observations |
| `screenshots/T05_registration_success.jpg` | Actual account-created message |
| `screenshots/T05_registration_duplicate.jpg` | Actual duplicate field error and empty password |
| `docs/evidence/T05_git_workflow.txt` | Actual status/add/commit/push/log transcript; raw log whitespace excluded from source whitespace check |
| `docs/evidence/T05_github_pr.json` | Actual PR URL, review body/state/author, head and merge SHAs returned by GitHub |
| `docs/evidence/T05_merge_sync.txt` | Actual fetch/switch/fast-forward and merge graph; tested source unchanged |
| `screenshots/T05_pull_request.jpg` | Open PR title and feature-to-develop branch labels |
| `screenshots/T05_review.jpg` | Published self-review comment with inspected behavior |
| `screenshots/T05_merge.jpg` | GitHub **Merged** badge and target/source branches |

**Screenshot required:** Yes, for the registration UI and GitHub PR/review/merge. These are actual app/browser captures.

For another UI capture, run the app, open `/register`, enter a fresh fictional email (for example `asha.demo.2@example.test`) and submit. Keep the portal heading and account-created message visible. Use `Win+Shift+S`, save as `T05_registration_success_manual.png`. Resubmit that email in uppercase; capture the duplicate message as `T05_registration_duplicate_manual.png`. Do not capture a filled password field.

For GitHub proof, open the recorded PR URL. Capture the title, feature → `develop` branch labels, test summary/screenshots, review comment, and **Merged** badge/merge commit. Save `T05_pull_request_manual.png`, `T05_review_manual.png`, and `T05_merge_manual.png`. Use additional views if those sections cannot fit legibly in one image. For local proof, capture `git status`, `git log --graph`, and `git branch -a`; keep branch and commit labels visible. A local log alone does not replace the GitHub PR evidence.

## Checklist

- ✅ Registration behavior and password protection verified
- ✅ Feature branch created
- ✅ 14 tests and local browser/API checks passed
- ✅ Screenshot guidance and UI evidence saved
- ✅ Commit/push transcript and GitHub PR
- ✅ Self-review comments
- ✅ Merge into `develop` and merged-state evidence
