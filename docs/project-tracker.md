# Project state tracker

**Project:** Selenium Testing for a Physiotherapy Appointment Portal  
**Owner:** Kirti Vispute (23102C0078)  
**Updated:** 2 October 2026

Status key: ⬜ Not started · 🟡 In progress · ✅ Completed with evidence · ❌ Needs correction

| Task | Name | Status | Evidence or remaining gate |
|---|---|---|---|
| 01 | Problem Definition | ✅ | Kirti approved scope in chat on 2 October 2026; `docs/problem-definition.md` records frozen scope |
| 02 | Agile Planning | ✅ | `user-stories.md`, `backlog.md`, and `agile-plan.md` created; 11 stories and 15 task rows verified |
| 03 | Architecture | ✅ | SRS, diagrams, model, API contract, and setup created; Maven build, home/health, H2, and screenshot verified |
| 04 | Git/GitHub | ✅ | Verified repository URL, pushed `main`/`develop`, README/templates/policy, initial commits, remote log and GitHub screenshots |
| 05 | Feature Development | ✅ | Registration UI/API/persistence and 14 tests verified; feature commits, PR #1, COMMENT self-review, merge `f7713f0`, Git logs and screenshots saved |
| 06 | MVP Collaboration | ⬜ | Not started |
| 07 | Jenkins CI | ⬜ | Not started |
| 08 | Jenkins Pipeline | ⬜ | Not started |
| 09 | Selenium | ⬜ | Not started |
| 10 | Continuous Testing | ⬜ | Not started |
| 11 | Docker | ⬜ | Not started |
| 12 | Docker CD | ⬜ | Not started |
| 13 | Ansible | ⬜ | Not started |
| 14 | Provisioning/Reliability | ⬜ | Not started |
| 15 | Final Validation | ⬜ | Not started |

| Area | Current verified state |
|---|---|
| Application | Registration UI/API, validation, salted password hashes, duplicates/concurrency, persistence, homepage/health verified on port 8081; verification app stopped |
| GitHub | `origin`: https://github.com/kirti-vispute/physiotherapy-appointment-portal.git; PR #1 merged into `develop`; `main` retains the Task 4 baseline |
| Jenkins | Not checked or configured |
| Selenium | Not implemented or run |
| Docker | Not checked or configured |
| Ansible | Not checked or configured |
| Documentation | Tasks 1–5 complete; registration guide, API, README, story/backlog/board/tracker updated; US-01 Selenium DoD remains pending Task 9 |
| Evidence | Tasks 3–4 evidence plus Task 5 failure/correction logs, 14-test summary, local HTTP/persistence, Git transcripts, PR/review/merge JSON and screenshots |

## Task 1 evidence checklist

- **Screenshot required?** No. A reviewed document and explicit scope decision are stronger evidence for this planning task.
- **What must be visible?** The problem statement, motivation, users and stakeholders, pain points, constraints, objectives, measurable criteria, assumptions, in/out scope, 15-task scope, and approval record in `docs/problem-definition.md`.
- **Command required?** None. Open and review the document in the editor; this task has no executable component.
- **Actual result:** Kirti approved the scope in chat on 2 October 2026; the approval record was updated and Task 1 is ✅.
- **Evidence filename:** `docs/problem-definition.md`. The approval decision is also present in this chat.

## Task 5 verification gate

- ✅ Working registration form/API with validation and salted password hashes
- ✅ `feature/user-registration` created from `develop`; meaningful feature/test/docs commits pushed
- ✅ Real `git status`, `add`, `commit`, `push`, and `log` recorded in `docs/evidence/T05_git_workflow.txt`
- ✅ [PR #1](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/1) includes purpose, changes, tests, and screenshots
- ✅ [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/1#pullrequestreview-5393019879) published; no independent approval claimed
- ✅ Merged into `develop` at `f7713f0406c93747c7909041482f3682267edd0c`; local synchronization and unchanged tested source verified
- ✅ Screenshot/log evidence and reproduction instructions in `docs/task-05-registration.md`

Task 6 remains ⬜. No remaining MVP, Selenium, Jenkins, Docker, or Ansible execution is claimed.
