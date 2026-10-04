# Portal UI refinement

The visual update follows the approved patient portal scope. It changes presentation and public image access; registration, sign-in, booking, status, and cancellation rules stay the same.

## Design

- **Theme:** deep navy for text and footer, teal for primary actions, soft mint for supporting surfaces, and a warm cream accent in the home hero.
- **Physiotherapy context:** a local transparent PNG shows a patient seated on a treatment table and a physiotherapist in teal scrubs and a white coat guiding a shoulder assessment. An exercise ball and resistance band add rehabilitation context. The same illustration supports the home and account pages; provider cards use small clinician icons, and the slot page uses a clock icon beside practical booking information.
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
- [Sign-in, tablet](../screenshots/UI_login_tablet.png)
- [Sign-in, desktop](../screenshots/UI_login_desktop.png)

These screenshots were captured from a temporary local instance using fictional data. The prior T03–T15 screenshots remain dated proof of their original tasks.

## Initial local verification on 4 October 2026

- `mvn -B -ntp clean package`: 34 backend tests, zero failures/errors/skips, BUILD SUCCESS. [Full log](evidence/UI_backend_build.txt).
- The temporary preview on port 8092 returned HTTP 200 for home, providers, registration, sign-in, and the SVG asset, with health `UP`.
- `mvn -B -ntp -Pselenium '-Dselenium.baseUrl=http://127.0.0.1:8092' failsafe:integration-test failsafe:verify`: five Selenium journeys, zero failures/errors/skips, BUILD SUCCESS. The preview JAR was kept unchanged while these goals ran. [Full log](evidence/UI_selenium_browser.txt).
- Browser emulation at 390 CSS pixels found `document.documentElement.scrollWidth == innerWidth == 390` on home, providers, slots, and registration. The images show the actual phone-width layout.

## Reviewed merge and deployed result

[PR #11](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/11) received a [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/11#pullrequestreview-5405388424) and merged into `develop` at `06794732fb80601490a932ad52bfcff65f35f4d6`. This is a single-contributor review record.

The merge automatically triggered Jenkins freestyle CI #21 and Pipeline #13; both finished SUCCESS. [Pipeline #13 evidence](evidence/UI_pipeline13.json) records all 39 tests passing, the versioned local registry image, container replacement, and health gate. The [independent Docker check](evidence/UI_deployment_check.json) matched the running container, image, and source commit to this build.

The [live HTTP check](evidence/UI_live_checks.json) verified the updated home, providers, sign-in, registration, and SVG at [the local portal](http://127.0.0.1:8087/), with HTTP 200 and health `UP`. Later documentation commits can create new build numbers through the same polling pipeline.

## Illustration correction

User feedback identified unclear arm and leg positions in the original vector illustration. The replacement `src/main/resources/static/images/movement-session.png` was created with the built-in image-generation tool and stored in the repository. The prompt requested a seated patient with supported thighs, bent knees and feet on the floor, alongside a standing physiotherapist with separate limbs, natural joints and hands; transparent background, navy/teal/mint palette, no touching or overlapping limbs, and no text.

Home, sign-in, and registration now share this corrected asset. The public-image test checks the PNG content type and binary signature. Previous build #13 evidence remains historical and describes the original SVG deployment.

The home-page badge was removed so it cannot cover the clinician's feet. The mobile illustration container now keeps the complete scene visible. Updated screenshots include the sign-in error view at 744 pixels and the home page at 390 pixels, where page width and viewport width both measured 390 pixels.

Local checks on 4 October 2026 passed: 34 backend tests ([build log](evidence/UI_illustration_backend.txt)) and five Selenium journeys against the final preview ([browser log](evidence/UI_illustration_selenium.txt)), with zero failures, errors, or skips.

[PR #12](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/12) received a [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/12#pullrequestreview-5406606689) and merged at `ef9a997ba600015fc89c9859b35cc10de23ebccf`. Automatic [Pipeline #15](evidence/UI_illustration_pipeline15.json) finished SUCCESS with all 39 tests passing and deployed the corrected image to port 8087. The [live verification](evidence/UI_illustration_live.json) matched the running container to the build, checked all three page references, confirmed the live PNG SHA-256 equals the repository asset, and returned health `UP`.

## Clearer physiotherapy setting

Further user feedback requested a more recognizable patient and clinician. The built-in image-generation tool edited the local illustration to show a patient seated on a padded physiotherapy treatment table, a clinician wearing teal scrubs and a white coat with a blank ID badge, and a guided shoulder mobility assessment. An exercise ball and rolled resistance band supply rehabilitation context. Natural limbs, complete feet, transparency, and the existing palette were retained. This remains a conceptual illustration, with no treatment instructions or outcome claim.

Final prompt summary: edit the existing illustration into a professional physiotherapy appointment with a treatment table, clinical clothing, gentle elbow support during shoulder assessment, natural hands and legs, full-body framing, transparent background, and no text or logos. The result is saved at `src/main/resources/static/images/movement-session.png` and used on home, sign-in, and registration. The home text alternative now describes the assessment. Earlier build and asset hashes document previous versions.

Local packaging with `mvn -B -ntp -DskipTests package` succeeded. Desktop and 744-pixel sign-in screenshots show the clinical setting clearly, and the 390-pixel mobile home screenshot includes the full scene with no horizontal overflow. The existing full test gate runs in Jenkins after merge.

[PR #13](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/13) received a [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/13#pullrequestreview-5406653487) and merged at `4f48cf65f4340d99fcd1b50b64171b2dedfc5424`. The SCM-triggered [build #17](evidence/UI_clinical_scene_aborted17.json) timed out in Start Application before Selenium or Docker deployment, leaving the previous container in place. A manually triggered [retry #18](evidence/UI_clinical_scene_pipeline18.json) completed SUCCESS: 34 backend and five Selenium tests passed, then the container was replaced and health returned `UP`. The [live verification](evidence/UI_clinical_scene_live.json) matched the container source to the build, checked all three page references, and confirmed the served PNG SHA-256 equals the new repository asset.
