# Task 6 — MVP Completion and Git Collaboration

## Objective

Complete the patient workflow and create a tested release baseline, while demonstrating a real Git merge conflict and its resolution.

## Requirements and deliverables

| Requirement | Deliverable | Current gate |
|---|---|---|
| Registration | Existing UI/API extended with CSRF and sign-in navigation | ✅ Verified |
| Sign-in/sign-out | Spring Security session authentication; forms and JSON API | ✅ Verified |
| Physiotherapists and available slots | Two fictional providers; future open slots in Asia/Kolkata | ✅ Verified |
| Booking request | Transactional slot lock; one CONFIRMED booking | ✅ Verified |
| Confirmation/status | Own appointment list/detail with identifier and status | ✅ Verified |
| Cancellation | Owner-only, before start, repeatable; slot released | ✅ Verified |
| Validation/errors | Friendly form/API errors, ownership, CSRF, time boundaries | ✅ Verified |
| Second feature branch | `feature/appointment-booking` | ✅ Created |
| Actual merge conflict and resolution | Opposing edits to `docs/mvp-demo.md` on two branches; real merge output and resolved commit | ✅ Verified |
| Push and successful integration | Feature/conflict branches pushed; integration into `develop` | 🟡 Pending |
| Git tag and release baseline | Version `1.0.0`, annotated `v1.0.0`, `main` release baseline | 🟡 Pending |
| Updated backlog | Stories, board, backlog, tracker, current API/setup | 🟡 In progress |

## Step-by-step implementation

1. Created `feature/appointment-booking` from the verified Task 5 `develop` baseline.
2. Added Spring Security session authentication backed by the existing patient password encoder. HTML uses Spring's login/logout filters; JSON login explicitly applies session-ID/CSRF rotation and saves the security context. Authentication persists across requests. Cookies are HttpOnly, SameSite=Lax, and used instead of URL session rewriting.
3. Enabled CSRF for all state-changing requests, including registration and login. Thymeleaf inserts tokens into POST forms. API clients fetch `/api/auth/csrf`, retain the session cookie, and send `X-CSRF-TOKEN`; fetch a fresh token after login/logout. No CSRF exclusion is used.
4. Added provider, slot, appointment entities and repositories. Seed data creates two fictional providers and 12 slots (09:00 and 11:00 IST over the next three days). Repeated startup preserves existing rows and availability. Old slots remain history and are filtered out of open-slot results. Set `DEMO_SEED_ENABLED=false` to disable seeding.
5. Implemented shared services for page/API behavior. Booking locks the slot in a transaction, rejects occupied/started slots, records CONFIRMED, and occupies the slot. Cancellation checks ownership, locks that same slot, refreshes the appointment after waiting, rejects started appointments, records CANCELLED, and releases the slot. Repeating an old cancellation does not release a subsequent booking.
6. Added provider/slot views, own appointment list/detail, clear empty states, confirmation/cancellation notices, signed-in navigation, and stable `data-testid` locators.
7. Added 20 appointment/security integration tests and extended 14 registration tests to send CSRF. Tests use isolated memory databases and a fixed clock for appointment start boundaries. First run: two assertions expected relative redirects, while Tomcat correctly returned absolute URLs. Second run: concurrent registration used two freshly created sessions; established one shared session before the threads. Both failures are retained as evidence. The final full run is recorded below.
8. Verify the packaged app through the full browser journey and save actual screenshots. Then commit, demonstrate/resolve the documentation conflict, publish a reviewed PR, integrate the release, and tag it. Actual identifiers and results will replace the pending gates.

## Files to create/change

Paths are relative to `D:\Kirti\__VIT RELATED\Lab Experiments\DEVOPS\DevOps Project`.

- `pom.xml`: Spring Security starter and application version `1.0.0`.
- `src/main/java/com/kirtivispute/physio/security/`: filter chain, user lookup, JSON login/CSRF, sign-in page.
- `src/main/java/com/kirtivispute/physio/appointment/`: entities/repositories, transactional service, DTOs, seed data, controllers, page error handling.
- `src/main/java/com/kirtivispute/physio/patient/RegistrationApiErrors.java`: shared expected API errors.
- `src/main/resources/application.properties`: session/cookie and seed settings.
- `src/main/resources/templates/`: shared navigation, sign-in, provider/slot list, appointment list/detail, friendly error page, registration/home updates.
- `src/main/resources/static/css/portal.css`: responsive portal styling.
- `src/test/java/com/kirtivispute/physio/appointment/PortalIntegrationTest.java`: 20 HTTP/database tests.
- `src/test/java/com/kirtivispute/physio/patient/RegistrationIntegrationTest.java`: CSRF-aware regression tests.
- `docs/mvp-demo.md`: real conflict target and resolved demonstration notes.
- `README.md`, design/API/setup/story/backlog/board/tracker documents, `docs/evidence/T06_*`, and `screenshots/T06_*`.

## Commands

**Terminal:** Windows PowerShell. **Location for every command:** project root. Commands below describe the demonstrated workflow. Do not recreate existing feature branches, commits, or tag after completion.

```powershell
Set-Location -LiteralPath 'D:\Kirti\__VIT RELATED\Lab Experiments\DEVOPS\DevOps Project'
git switch develop
git switch -c feature/appointment-booking
mvn clean verify
$env:PORT='8081'
java -jar target/physio-portal-1.0.0.jar
```

Purpose: create the second feature branch, run all tests/package, start embedded Tomcat. Expected: 34 tests with zero failures/errors/skips, `BUILD SUCCESS`, and startup on 8081. Open `http://localhost:8081/`. If Maven fails, inspect `target/surefire-reports`; if the port is occupied, choose another free `PORT`. Keep the app terminal open for browser checks; `Ctrl+C` stops it.

In a second PowerShell terminal, project root:

```powershell
Invoke-RestMethod 'http://localhost:8081/actuator/health'
git status --short --branch
git add pom.xml src docs/mvp-demo.md
git commit -m "feat: complete patient appointment workflow with session security"
git branch feature/demo-notes
```

Purpose: check health, record MVP implementation and the conflict base, branch for the competing documentation edit. Expected: `UP`, meaningful commit, both branches from the same notes baseline. If branch creation says it exists, inspect history and reuse it rather than resetting it.

### Conflict demonstration

1. On `feature/appointment-booking`, change `Demo focus: patient registration.` in `docs/mvp-demo.md` to `Demo focus: book and cancel an appointment.`. Stage/commit:

   ```powershell
   git add docs/mvp-demo.md
   git commit -m "docs: focus MVP demonstration on booking and cancellation"
   ```

2. Switch branches, change the same original line to `Demo focus: view providers and available slots.`, and commit:

   ```powershell
   git switch feature/demo-notes
   git add docs/mvp-demo.md
   git commit -m "docs: focus MVP demonstration on providers and slots"
   git switch feature/appointment-booking
   git merge feature/demo-notes
   git status --short
   Get-Content docs/mvp-demo.md
   ```

   Expected: merge exits with a real `CONFLICT (content)` message, status `UU docs/mvp-demo.md`, and `<<<<<<<`, `=======`, `>>>>>>>` markers. Do not interpret this deliberate merge failure as a failed application build.

3. Edit the conflicted section to one line covering the complete workflow: `Demo focus: register, sign in, view slots, book, check status, and cancel an appointment.` Remove all conflict markers, then:

   ```powershell
   git add docs/mvp-demo.md
   git commit -m "docs: resolve demo conflict with complete patient workflow"
   git push -u origin feature/demo-notes
   git push -u origin feature/appointment-booking
   git log --graph --oneline --decorate -n 10
   ```

   Expected: merge commit with both parents, no unmerged files, both branches published. If markers remain, correct the file before committing. If authentication fails, use Git Credential Manager sign-in. Never force-push.

### Release integration and tag

Create a PR with base `develop` and compare `feature/appointment-booking`, record actual tests/screenshots/conflict results, self-review, and merge. Then:

```powershell
git fetch origin
git switch develop
git pull --ff-only origin develop
git switch main
git merge --ff-only develop
git tag -a v1.0.0 -m "Physiotherapy portal MVP 1.0.0: verified patient workflow"
git push origin main
git push origin v1.0.0
git show --no-patch v1.0.0
git ls-remote origin refs/tags/v1.0.0 'refs/tags/v1.0.0^{}'
git switch develop
```

Purpose: synchronize reviewed work, advance `main` to the release baseline, create and publish an annotated tag, verify remote tag/peeled commit. Expected: tag and release commit visible on GitHub; no conflict or history rewrite. If `--ff-only` fails, inspect the divergence rather than force-moving branches. If `v1.0.0` already exists, inspect it; do not overwrite it.

If this checkout reports dubious ownership, use the process-only trust settings in [the Task 5 guide](task-05-registration.md) before Git commands. No global `safe.directory` change is required.

## Verification

| Check | Actual result |
|---|---|
| Compile | `BUILD SUCCESS`; Spring Security 7.0.7 resolved by Boot 4.0.8 |
| Final `mvn clean verify` | 34 tests, 0 failures, 0 errors, 0 skipped; `BUILD SUCCESS`; `physio-portal-1.0.0.jar` packaged |
| Registration/browser sign-in | Asha Patil, fictional `asha.task6@example.test`, registered and signed in; both seed providers visible |
| Slot selection/booking | Provider #1 slot #1 booked as appointment #1; CONFIRMED and success message shown; slot removed from availability |
| Cancellation/status | Appointment #1 became CANCELLED; own list reflected it; slot #1 returned to availability |
| Sign-out | Signed-out message shown; opening `/appointments` redirected to `/login` |
| Live API | Sign-in 200, booking 201 (appointment #2, slot #7), duplicate 409, foreign view/cancel 404, cancellation/repeat 200, missing CSRF 403, logout 204, protected access after logout 401 |
| Restart persistence | Browser/API appointments #1/#2 remained CANCELLED; two providers and 12 open slots remained; health HTTP 200/UP |
| Real conflict | `git merge feature/demo-notes` returned exit 1, `CONFLICT (content)`, `UU docs/mvp-demo.md`; original markers saved |
| Resolution | `dceeb892f30dc5e76a990785248d2ea1204980a4`, parents `9901333` and `124907f`; complete demo line retained; no unmerged files |
| Publication/tag | Pending; actual PR/merge/tag identifiers will be added after GitHub returns them |

App verification is complete; the publication gate remains open. No application source changed during the documentation conflict. The later release source will be compared with tested source commit `16c17af` before publication. The verification app is stopped at the end of this task.

The suite covers normalized sign-in, generic invalid-credential errors, session and CSRF rotation, logout, visitor/protected-route boundaries, provider/slot filtering and empty states, IST display, confirmation/persistence, ownership attacks, malformed/missing/stale/start-time selections, concurrent booking, repeat/concurrent cancellation, cancellation after rebooking, started appointment read-only behavior, CSRF rejection, HTML form flow, and repeatable seed data.

## Evidence and screenshot guidance

- Build logs: `T06_compile.txt`, `T06_maven_initial_failure.txt`, `T06_maven_csrf_fixture_failure.txt`, and final `T06_maven_verify.txt` under `docs/evidence/`.
- Surefire results: `target/surefire-reports/`; summaries will be copied into `docs/evidence/` after a passing run.
- Browser images: provider list, available slots, confirmation, cancellation, own status list, and sign-out. Keep the portal heading, provider name, appointment identifier/status, and relevant success message visible. Use `Win+Shift+S` to save manual images under `screenshots/T06_*_manual.png`. Never capture passwords or session/CSRF values.
- Git conflict proof: save the terminal merge output, `UU` status, original conflict markers, resolved file, two-parent commit, pushes, and branch graph in `docs/evidence/T06_git_conflict.txt` and `T06_conflict_markers.txt`. A hand-typed example of markers is not evidence.
- GitHub proof: capture actual merged PR/feature labels, review comment, and tag page. Git/REST logs record actual hashes. No independent peer approval is claimed for this single-owner exercise.
- Selenium/Jenkins are future gates. Browser verification here uses the real local UI but does not count as a Selenium run.

| Actual evidence | Purpose |
|---|---|
| `docs/evidence/T06_appointment_tests.txt`, `T06_registration_tests.txt` | Surefire summaries: 20 + 14 passed |
| `docs/evidence/T06_local_http.txt` | Actual live API status/body checks; browser observation record; raw Actuator bytes plus decoded health JSON |
| `docs/evidence/T06_persistence.txt` | Actual checks after stop/restart; stored CANCELLED records and seed counts |
| `docs/evidence/T06_git_conflict.txt` | Real opposing commits, merge failure, resolution, two-parent graph |
| `docs/evidence/T06_conflict_markers.txt` | Copy of the actual conflicted file, intentionally retains markers as evidence |
| `screenshots/T06_registration.jpg`, `T06_physiotherapists.jpg`, `T06_available_slots.jpg` | Registered account and seeded provider/slot views |
| `screenshots/T06_booking_confirmation.jpg`, `T06_slot_occupied.jpg` | CONFIRMED appointment #1; selected slot absent |
| `screenshots/T06_cancellation.jpg`, `T06_appointment_status.jpg`, `T06_slot_released.jpg` | CANCELLED appointment and released original slot |
| `screenshots/T06_sign_out.jpg` | Real sign-out success message |

## Checklist

- ✅ Functional registration/sign-in/provider/slots/booking/confirmation/status/cancellation
- ✅ Full regression/security/concurrency verification and browser proof
- ✅ Second feature branch
- ✅ Demonstrated and resolved merge conflict
- 🟡 Published integration and annotated release tag
- 🟡 Updated backlog and saved evidence
