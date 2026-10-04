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
