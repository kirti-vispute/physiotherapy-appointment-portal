# Task 15 — local Docker Desktop recovery after reboot

**Observed:** 4 October 2026, Windows host. Jenkins build #11 had already completed `SUCCESS` on 3 October with a healthy container. After reboot, `docker version` could not reach `npipe:////./pipe/dockerDesktopLinuxEngine`, and `http://127.0.0.1:8087/actuator/health` refused the connection. Recent Docker Desktop backend logs reported the same `sailor-ingest.sock` rename/access error documented in [Task 11](../task-11-docker.md#startup-blocker-observed-on-3-october-2026).

**Actions actually taken:** The two exact source folders, `C:\Users\kirti\AppData\Local\Docker\run` and `C:\Users\kirti\AppData\Local\docker-secrets-engine`, were checked to exist, resolve under LocalAppData, and not be reparse points. Failed Docker Desktop processes were stopped, `docker-desktop` WSL was terminated, and only those transient folders were moved to dated sibling backups:

- `C:\Users\kirti\AppData\Local\Docker\run.task15-backup-20261004-141702`
- `C:\Users\kirti\AppData\Local\docker-secrets-engine.task15-backup-20261004-141702`

Empty original folders were recreated, and the already installed Docker Desktop application was restarted. `docker info --format '{{.ServerVersion}}'` returned `29.7.2`. Images, containers, volumes, settings, and the Docker data disk were not moved or reset.

The existing `physio-portal-cd-test` container still had build #11's exact ID and image but was stopped, with restart policy `no`. `docker start physio-portal-cd-test` restarted **the same container**. The [independent check](T15_auto_independent_check.json) then matched its image ID, `source.commit` and `jenkins.build` labels, named H2 volume, host port, registry manifest, and HTTP results. Health returned `UP`; home and provider pages returned HTTP 200. This post-reboot start is distinct from Jenkins build #11's automatic container replacement.
