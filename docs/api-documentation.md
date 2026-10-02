# Task 3 — API and Page Contract

**Status:** Contract for future implementation. In Task 3, only `GET /` and `GET /actuator/health` are implemented. All other rows are planned for Tasks 5 and 6. They must not be cited as working endpoints before verification.

All JSON requests use `Content-Type: application/json`. Patient-specific endpoints require a signed-in session cookie. Browser forms and state-changing requests will include CSRF protection when authentication is added. A standard error body is planned: `{"error":"CODE","message":"Readable explanation"}`. Dates in JSON use ISO 8601 with an offset; pages display `Asia/Kolkata` time.

## Browser pages

| Method and path | Purpose | Current state |
|---|---|---|
| `GET /` | Landing page | Implemented in Task 3 |
| `GET /register` | Registration form | Planned |
| `GET /login` | Sign-in form | Planned |
| `GET /physiotherapists` | Browse physiotherapists | Planned |
| `GET /physiotherapists/{id}/slots` | Browse one provider's open slots | Planned |
| `GET /appointments` | See own appointments | Planned |
| `GET /appointments/{id}` | See confirmation and status | Planned |

## JSON API

| Method and endpoint | Purpose | Request | Success response/status | Main error statuses |
|---|---|---|---|---|
| `POST /api/auth/register` | Create patient account | `{"fullName":"Asha Patil","email":"asha@example.test","password":"example123"}` | `201` with `{"id":1,"fullName":"Asha Patil","email":"asha@example.test"}` | `400` validation; `409` duplicate email |
| `POST /api/auth/login` | Start session | `{"email":"asha@example.test","password":"example123"}` | `200` with patient summary and session cookie | `400` missing fields; `401` invalid credentials |
| `POST /api/auth/logout` | End session | Empty body | `204` empty | `401` if session required by implementation |
| `GET /api/physiotherapists` | List providers | None | `200` array of `{id,fullName,specialty,description}` | `500` unexpected server fault |
| `GET /api/physiotherapists/{id}/slots` | List future open slots | Path `id` | `200` array of `{id,physiotherapistId,startAt,endAt}` | `404` unknown provider |
| `POST /api/appointments` | Book slot for current patient | `{"slotId":10}` | `201` with `{id,slotId,physiotherapistId,status,bookedAt}` and `Location` | `400` invalid data; `401` no session; `404` unknown slot; `409` occupied/past slot |
| `GET /api/appointments` | List current patient's appointments | None | `200` array of appointment summaries | `401` no session |
| `GET /api/appointments/{id}` | Get own confirmation/status | Path `id` | `200` with appointment summary | `401` no session; `404` missing or foreign appointment |
| `PATCH /api/appointments/{id}/cancel` | Cancel own future confirmed appointment | Path `id`, empty body | `200` with updated `status:CANCELLED` | `401` no session; `404` missing/foreign; `409` past or ineligible appointment |
| `GET /actuator/health` | Read health | None | `200` with `{"status":"UP"}` when healthy | `503` if an essential health component is down |

`POST /api/appointments` must not accept a patient ID; the authenticated session supplies ownership. A repeated cancellation may return the existing `CANCELLED` result with `200`, matching US-07's idempotent user outcome. Exact response fields will be checked against implementation in Tasks 5 and 6 and updated here if a justified change is needed.

## Verification status

| Endpoint | Planned check | Actual result |
|---|---|---|
| `GET /` | HTTP 200 and setup page | Passed on port 8081 on 2 October 2026; setup text present |
| `GET /actuator/health` | HTTP 200 and `UP` | Passed on port 8081 on 2 October 2026; `UP` observed |
| All patient endpoints | Implement and test in Tasks 5–6 | Not implemented |
