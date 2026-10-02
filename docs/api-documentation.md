# Task 3 — API and Page Contract

**Status:** All browser and JSON routes listed below are implemented and verified through Tasks 5–6. Responses shown in the contract are examples; actual identifiers and outputs are saved in `docs/evidence/T06_local_http.txt`.

All JSON requests use `Content-Type: application/json`. Patient-specific endpoints require a signed-in session cookie. Every state-changing request requires CSRF, including registration, login, and logout. Forms insert the token automatically; API callers fetch `GET /api/auth/csrf` and send its `token` in `X-CSRF-TOKEN` while retaining the same session cookie. Fetch a new token after login/logout. Errors use `{"error":"CODE","message":"Readable explanation"}` with an optional field-message map. Instants in JSON use ISO 8601 UTC (`Z`); pages display `Asia/Kolkata` (IST).

## Browser pages

| Method and path | Purpose | Current state |
|---|---|---|
| `GET /` | Landing page | Implemented in Task 3 |
| `GET /register` | Registration form | Implemented in Task 5 |
| `POST /register` | Submit HTML form | Implemented; 302 redirect on success, 200 with field errors on invalid/duplicate input |
| `GET /login` | Sign-in form | Implemented in Task 6 |
| `POST /login` | Authenticate form | 302 to providers on success; 302 to `/login?error` for invalid credentials |
| `POST /logout` | Sign out | 302 to `/login?logout`; session invalidated |
| `GET /physiotherapists` | Browse physiotherapists | Implemented in Task 6; public |
| `GET /physiotherapists/{id}/slots` | Browse one provider's open slots | Implemented in Task 6; public |
| `GET /appointments` | See own appointments | Implemented in Task 6; sign-in required |
| `GET /appointments/{id}` | See confirmation and status | Implemented in Task 6; owner-only |
| `POST /appointments` | Book form field `slotId` | 302 to own confirmation; 400/404/409 friendly error |
| `POST /appointments/{id}/cancel` | Cancel owned future booking | 302 to updated detail; 404 foreign/missing; 409 started |

## JSON API

| Method and endpoint | Purpose | Request | Success response/status | Main error statuses |
|---|---|---|---|---|
| `GET /api/auth/csrf` | Get token for this session | None; retain returned cookie | `200` with `{token,headerName,parameterName}` | Refresh token after sign-in/sign-out |
| `POST /api/auth/register` | Create patient account | `{"fullName":"Asha Patil","email":"asha@example.test","password":"example123"}` | `201` with `{"id":1,"fullName":"Asha Patil","email":"asha@example.test"}` | `400` validation; `409` duplicate email |
| `POST /api/auth/login` | Start session | `{"email":"asha@example.test","password":"example123"}` | `200` with patient summary and session cookie | `400` missing fields; `401` invalid credentials |
| `POST /api/auth/logout` | End session | Empty body, CSRF header/cookie | `204` empty; also safe when already signed out | `403` absent/invalid CSRF |
| `GET /api/physiotherapists` | List providers | None | `200` array of `{id,fullName,specialty,description}` | `500` unexpected server fault |
| `GET /api/physiotherapists/{id}/slots` | List future open slots | Path `id` | `200` array of `{id,physiotherapistId,startAt,endAt}` | `404` unknown provider |
| `POST /api/appointments` | Book slot for current patient | `{"slotId":10}` | `201` with `{id,slotId,physiotherapistId,status,bookedAt}` and `Location` | `400` invalid data; `401` no session; `404` unknown slot; `409` occupied/past slot |
| `GET /api/appointments` | List current patient's appointments | None | `200` array of appointment summaries | `401` no session |
| `GET /api/appointments/{id}` | Get own confirmation/status | Path `id` | `200` with appointment summary | `401` no session; `404` missing or foreign appointment |
| `PATCH /api/appointments/{id}/cancel` | Cancel own future confirmed appointment | Path `id`, empty body | `200` with updated `status:CANCELLED` | `401` no session; `404` missing/foreign; `409` past or ineligible appointment |
| `GET /actuator/health` | Read health | None | `200` with `{"status":"UP"}` when healthy | `503` if an essential health component is down |

`POST /api/appointments` binds only `slotId`; the authenticated session supplies ownership. A caller-supplied `patientId` cannot assign a booking to someone else. Repeated cancellation returns the existing `CANCELLED` result with `200`, matching US-07's idempotent outcome, and cannot release a later booking of that same slot. A missing/invalid CSRF token returns `403` before controller validation/authentication; with a valid token, an anonymous protected API request returns `401`.

### Response fields and shared rules

- Providers: `id`, `fullName`, `specialty`, `description`.
- Slots: `id`, `physiotherapistId`, `startAt`, `endAt`, `startLabel`, `endLabel`; only the requested provider's future available slots, ordered by start time.
- Appointments: `id`, `slotId`, `physiotherapistId`, `physiotherapistName`, `startAt`, `endAt`, `startLabel`, `endLabel`, `status`, `bookedAt`, `cancelledAt`, `cancellable`. No password/hash or patient identity input is exposed.
- `startLabel`/`endLabel` are human-readable IST labels. A CONFIRMED booking can be cancelled only while its start is strictly in the future. Started/past records remain readable with `cancellable:false`.
- Registration/login normalize email by stripping surrounding whitespace and lowercasing. Successful sign-in rotates the session ID and CSRF token and persists the identity. Invalid credentials produce one general error for known and unknown email. Logout clears the context/session and cookie.
- Empty provider/slot/appointment lists return `200` with `[]`; corresponding pages explain the empty state. Bad numeric paths return `400`; missing provider/slot or foreign/missing appointment returns `404`. Occupied/started slots and late cancellation return `409`.

### API client example

**Terminal:** PowerShell, any directory, while the app runs on 8081. Use a previously registered fictional account; its password below is demo test data. Tokens/cookies are held in memory and not displayed.

```powershell
$portalUrl='http://localhost:8081'
$portalSession=[Microsoft.PowerShell.Commands.WebRequestSession]::new()
$csrf=Invoke-RestMethod "$portalUrl/api/auth/csrf" -WebSession $portalSession
$login=@{email='asha.task6@example.test';password='example123'} | ConvertTo-Json
Invoke-RestMethod "$portalUrl/api/auth/login" -Method Post -WebSession $portalSession -ContentType 'application/json' -Headers @{'X-CSRF-TOKEN'=$csrf.token} -Body $login
$csrf=Invoke-RestMethod "$portalUrl/api/auth/csrf" -WebSession $portalSession
Invoke-RestMethod "$portalUrl/api/appointments" -WebSession $portalSession
Invoke-RestMethod "$portalUrl/api/auth/logout" -Method Post -WebSession $portalSession -Headers @{'X-CSRF-TOKEN'=$csrf.token}
```

Expected: patient summary, own list, then empty logout response. `401` means credentials/session are missing or invalid; `403` means refresh the CSRF token while preserving the same session cookie. The fictional account must first be registered on the current database; do not assume a clean checkout already contains patient accounts.

## Verification status

### Registration behavior

- Full name is required, limited to 100 characters, and stripped of surrounding whitespace.
- Email is required, validated, limited to 254 characters, stripped and lowercased using `Locale.ROOT`. A database unique constraint protects concurrent registration.
- Password is required and must have 8–128 characters. Whitespace is preserved. Spring Security Crypto PBKDF2 with a random salt stores a hash; neither password nor hash appears in the response. See [Spring's password-storage documentation](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html).
- Invalid JSON fields return `400` with `error:VALIDATION_FAILED`, `message`, and a `fields` map of field messages. Malformed JSON returns `400` with `error:INVALID_REQUEST`. Duplicate email returns `409` with `error:EMAIL_IN_USE`.
- HTML success redirects to `/register` with a one-time message, so refreshing does not repeat the POST. Invalid/duplicate input shows field messages and clears the password. Sessions use cookies only.
- Registration creates an account; Task 6 adds sign-in, protected routes, and CSRF. The Task 5 results remain historical evidence from before authentication was added.

| Endpoint | Planned check | Actual result |
|---|---|---|
| `GET /` | HTTP 200 and setup page | Passed on port 8081 on 2 October 2026; setup text present |
| `GET /actuator/health` | HTTP 200 and `UP` | Passed on port 8081 on 2 October 2026; `UP` observed |
| Registration page/form/API | Valid, invalid, duplicate, persistence, concurrency, password protection | 14 JUnit integration tests passed; browser success and duplicate messages verified in Task 5 |
| Sign-in, providers, slots, booking, own status, cancellation, logout | Functional/security/concurrency and real browser/API checks | 20 additional integration tests passed; full suite 34/34; actual browser/API journey verified in Task 6 |
