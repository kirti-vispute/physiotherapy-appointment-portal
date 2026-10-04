# Portal UI refinement

The visual update follows the approved patient portal scope. It changes presentation and public image access; registration, sign-in, booking, status, and cancellation rules stay the same.

## Design

- **Theme:** deep navy for text and footer, teal for primary actions, soft mint for supporting surfaces, and a warm cream accent in the home hero.
- **Physiotherapy context:** an original local SVG shows a guided arm movement session. The same illustration supports the home and account pages; provider cards use small clinician icons, and the slot page uses a clock icon beside practical booking information.
- **Patient flow:** the home page explains three steps; provider cards show specialty and description; slots show duration and IST time zone before booking; account and appointment pages share the same navigation and action styles.
- **Accessibility:** semantic headings and landmarks, a skip link, text alternatives, visible keyboard focus, responsive layouts, and reduced-motion support.
- **Cost:** the illustration, icons, CSS, and system fonts are stored locally. No paid service, image license, remote font, or external runtime asset is required.

The two displayed physiotherapists and their schedules remain fictional demo data. The illustration is conceptual and makes no clinical outcome claim.

## What was rechecked

| Earlier task | Effect of this update |
|---|---|
| T1–T2 scope and planning | No functional scope or user story changed. |
| T3 architecture and local app | Thymeleaf pages and local static files remain in the same architecture; desktop and mobile views were inspected again. |
| T5–T6 patient MVP | Registration, sign-in, providers, slots, booking, appointment status, and cancellation keep their existing routes and test selectors. |
| T7–T8 build/deployment | Existing Maven and Jenkins configuration is reused. |
| T9–T10 testing gate | Backend and Selenium checks are rerun against the updated pages. |
| T11–T12 Docker CD | The versioned image and local container are rebuilt through the existing pipeline after merge. |
| T13–T14 Ansible/recovery | The separate Linux target and its pinned image are historical demonstrations and do not need reprovisioning for a presentation-only change. |
| T15 final pack | This addendum, current screenshots, and new build outcome supplement the original historical evidence. |

## Screenshots

- [Home, desktop](../screenshots/UI_home_desktop.png)
- [Home, mobile](../screenshots/UI_home_mobile.png)
- [Physiotherapists](../screenshots/UI_providers_desktop.png)
- [Available appointments](../screenshots/UI_slots_desktop.png)
- [Registration](../screenshots/UI_registration_desktop.png)
- [Physiotherapists, mobile](../screenshots/UI_providers_mobile.png)
- [Available appointments, mobile](../screenshots/UI_slots_mobile.png)
- [Registration, mobile](../screenshots/UI_registration_mobile.png)

These screenshots were captured from a temporary local instance using fictional data. The prior T03–T15 screenshots remain dated proof of their original tasks.

## Local verification on 4 October 2026

- `mvn -B -ntp clean package`: 34 backend tests, zero failures/errors/skips, BUILD SUCCESS. [Full log](evidence/UI_backend_build.txt).
- The temporary preview on port 8092 returned HTTP 200 for home, providers, registration, sign-in, and the SVG asset, with health `UP`.
- `mvn -B -ntp -Pselenium '-Dselenium.baseUrl=http://127.0.0.1:8092' failsafe:integration-test failsafe:verify`: five Selenium journeys, zero failures/errors/skips, BUILD SUCCESS. The preview JAR was kept unchanged while these goals ran. [Full log](evidence/UI_selenium_browser.txt).
- Browser emulation at 390 CSS pixels found `document.documentElement.scrollWidth == innerWidth == 390` on home, providers, slots, and registration. The images show the actual phone-width layout.

## Reviewed merge and deployed result

[PR #11](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/11) received a [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/11#pullrequestreview-5405388424) and merged into `develop` at `06794732fb80601490a932ad52bfcff65f35f4d6`. This is a single-contributor review record.

The merge automatically triggered Jenkins freestyle CI #21 and Pipeline #13; both finished SUCCESS. [Pipeline #13 evidence](evidence/UI_pipeline13.json) records all 39 tests passing, the versioned local registry image, container replacement, and health gate. The [independent Docker check](evidence/UI_deployment_check.json) matched the running container, image, and source commit to this build.

The [live HTTP check](evidence/UI_live_checks.json) verified the updated home, providers, sign-in, registration, and SVG at [the local portal](http://127.0.0.1:8087/), with HTTP 200 and health `UP`. Later documentation commits can create new build numbers through the same polling pipeline.
