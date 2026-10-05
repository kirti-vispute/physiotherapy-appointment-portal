# Expanded physiotherapist demo data

The portal now seeds 15 fictional physiotherapists: the original Dr Asha Kulkarni and Dr Rohan Deshmukh, plus the following 13. Names, descriptions and schedules are demo content.

| Added physiotherapist | Specialty |
|---|---|
| Dr Neha Shah | Neurological rehabilitation |
| Dr Vikram Patil | Post-operative rehabilitation |
| Dr Meera Joshi | Paediatric physiotherapy |
| Dr Arjun Nair | Geriatric mobility |
| Dr Sneha Iyer | Cardiorespiratory physiotherapy |
| Dr Kabir Mehta | Spine and posture care |
| Dr Priya Rao | Hand and wrist rehabilitation |
| Dr Aditya Kulkarni | Shoulder rehabilitation |
| Dr Ananya Desai | Women's health physiotherapy |
| Dr Rahul Menon | Balance and gait training |
| Dr Kavya Reddy | Occupational rehabilitation |
| Dr Nikhil Bansal | Chronic pain rehabilitation |
| Dr Ishita Verma | Hip and knee rehabilitation |

## Availability and existing data

- Startup adds two 45-minute slots per day (09:00 and 11:00 IST) for each provider over the next three days: six per provider and 90 on a fresh database.
- Existing providers are found by name and retain their IDs. The original two names stay available for the existing browser test journeys.
- Existing slots are found by provider and start time. Their availability is retained, including occupied slots. No patient or appointment data is removed.
- Older slots remain as history; only future available slots are offered for booking. A persistent database may contain more than 90 historical and current slots.
- `DEMO_SEED_ENABLED=false` still disables startup seeding.

## Affected project tasks

This expands the T5–T6 demo directory within the existing patient MVP. The T9–T10 seed integration check now verifies an upgrade from the original two providers to 15, six slots per provider, retained provider IDs and occupied slot state, and repeat startup without duplicates. Existing Maven/Jenkins T7–T8 and Docker T11–T12 configuration is reused. The Linux provisioning demonstrations and original T1–T15 evidence remain historical. This addendum and current directory screenshots supplement the final pack.

## Local verification

On 4 October 2026, `mvn -B -ntp package` passed all 34 backend tests with zero failures, errors or skips ([build log](evidence/Seed_expansion_backend.txt)). The first sandboxed attempt could not establish the tests' localhost connection; the successful rerun had local network access.

The fresh preview's [public API check](evidence/Seed_expansion_preview.json) confirmed exactly 15 providers and 90 available slots, six for every provider. The refreshed [desktop](../screenshots/UI_providers_desktop.png) and [mobile](../screenshots/UI_providers_mobile.png) screenshots show the start of the scrollable directory. At 390 pixels, page scroll width equalled viewport width, with no horizontal overflow. The full Jenkins backend/browser gate runs after merge.

## Longer-directory browser correction

[PR #14](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/14) received a [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/14#pullrequestreview-5407252075) and merged the seed expansion at `949b5e8e1fd5c9fb5fa1600e7cdcd52d69ffa813`. Automatic [build #20](evidence/Seed_expansion_failed20.json) passed 34 backend cases and one browser case; four browser journeys encountered intercepted clicks because the selected original doctor was now below the viewport during smooth scrolling. The gate failed and skipped deployment, leaving the previous container in place.

The Selenium helper now scrolls the selected provider's link instantly to the viewport centre, then waits for clickability and uses a normal WebDriver click. The application's scrolling and booking behavior are unchanged. `mvn -B -ntp -Pselenium '-Dselenium.baseUrl=http://127.0.0.1:8092' test-compile failsafe:integration-test failsafe:verify` passed all five journeys against the 15-provider preview, with zero failures/errors/skips ([browser log](evidence/Seed_expansion_selenium.txt)). The preview JAR was not repackaged while running.

[PR #15](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/15) received a [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/15#pullrequestreview-5407282688) and merged the scrolling correction at `c35ebd01b2744aa6c346c7cce9c702cd80137e14`. The automatic [run #21](evidence/Seed_expansion_aborted21.json) passed all 34 backend tests but timed out in Package before browser tests or Docker deployment. Its record is retained alongside the earlier failed browser run.

The manually restarted [run #22](evidence/Seed_expansion_failed22.json) on 5 October 2026 passed all 39 tests, including the corrected browser journeys, then failed at Docker Build because the local engine was stopped. Starting Docker exposed the previously documented `sailor-ingest.sock` startup error. Following the existing recovery procedure, the two verified transient directories were moved to [dated sibling backups](evidence/Seed_docker_recovery.json) and recreated empty. Docker's engine returned version `29.7.2`; the local registry restarted, and the existing portal container was started while deployment was retried. Images, database volumes and settings were retained.

## Verified live deployment

Manually triggered [Pipeline #23](evidence/Seed_expansion_pipeline23.json) completed SUCCESS on `c35ebd01b2744aa6c346c7cce9c702cd80137e14`: all 34 backend and five browser tests passed, followed by image publication, container replacement and health `UP`.

The [live check on 5 October 2026](evidence/Seed_expansion_live.json) confirmed 15 providers, exactly 13 additions, 15 directory cards and 90 available slots. All new doctors had six slots; the original Dr Asha Kulkarni and Dr Rohan Deshmukh retained IDs 1 and 2. The running container's source and image matched the successful build. The live directory is [http://127.0.0.1:8087/physiotherapists](http://127.0.0.1:8087/physiotherapists).
