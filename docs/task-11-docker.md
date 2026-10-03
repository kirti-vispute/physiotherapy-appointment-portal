# Task 11 — Docker image and container lifecycle

## Status

**Complete on 3 October 2026.** `mvn -B clean package` passed all 34 backend tests and produced the executable JAR recorded in [package evidence](evidence/T11_package_result.txt). Docker Desktop's Linux engine 29.7.2 then built `physio-portal:1.0.0`. The [command log](evidence/T11_docker_lifecycle.txt) records the real build, image, run, inspect, logs, stop, start, restart, remove, final run, and health results.

Task 11 stops after the local image and lifecycle demonstration. Registry publication and Jenkins deployment belong to Task 12.

The work was committed on `feature/docker-lifecycle`, [PR #6](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/6) received an honest [COMMENT self-review](https://github.com/kirti-vispute/physiotherapy-appointment-portal/pull/6#pullrequestreview-5400225177), and it merged into `develop` at `fb372c14a8c16925a47e787163e2d766964afade`. The local `develop` branch was synchronized with the remote.

## Runtime layout

| Item | Value |
|---|---|
| Versioned image | `physio-portal:1.0.0` |
| Temporary demonstration container | `physio-portal-task11` |
| Final demonstration container | `physio-portal-task11-final` |
| Host → container port | `127.0.0.1:8086` → `8080/tcp` |
| Persistent H2 data | Docker volume `physio-portal-task11-data` → `/app/data` |
| Health endpoint | `http://localhost:8086/actuator/health` |

The Dockerfile uses a Java 21 runtime and UID 10001, copies only the packaged application, and exposes 8080. The named volume kept the H2 file when the demonstration container was removed. Port 8086 avoids the existing local Jenkins and portal ports.

## Actual results

| Check | Observed result |
|---|---|
| `docker build --pull -t physio-portal:1.0.0 .` | Exit 0; image `sha256:c260571b57675bd2aae05ccdd25b604e57a40c5407aa4d93459f5f59b0d2ae34` |
| `docker images physio-portal` | Tag `1.0.0`, short image ID `c260571b5767`, size 590 MB |
| First `docker run` and `docker ps` | Container `256457adc2c1eecf3a1235acac625900d3f30d57afb715830a560e47e6b6e6ad`, `Up`, `127.0.0.1:8086->8080/tcp` |
| `docker logs` | Spring Boot started and Tomcat listened on container port 8080; H2 connected to `./data/physio` |
| `docker stop` / `docker start` / `docker restart` | All exited 0; the same first container ID returned `UP` after start and restart |
| `docker rm` | The stopped first container was removed; a filtered `docker ps -a` returned no row |
| Final `docker run` | Container `b3ec7ce3aac79450fee7c7267e57a656d1125f705857be3890da47ca4eef99e1` running from the same image and volume |
| Live checks | `/` HTTP 200; `/physiotherapists` HTTP 200; `/actuator/health` status `UP` |
| Persistent data | Volume `physio-portal-task11-data` contains `/app/data/physio.mv.db` owned by UID 10001 after the first container was removed |

## Commands to execute from the project root

Use PowerShell with Docker Desktop's Linux engine running. These commands were executed on 3 October 2026; the exact output and IDs are in the [command log](evidence/T11_docker_lifecycle.txt).

```powershell
mvn -B clean package
docker version
docker build --pull -t physio-portal:1.0.0 .
docker images physio-portal
docker image inspect physio-portal:1.0.0 --format '{{.Id}}'
docker volume create physio-portal-task11-data
docker run -d --name physio-portal-task11 --label project=physio-portal-task11 -p 127.0.0.1:8086:8080 --mount type=volume,source=physio-portal-task11-data,target=/app/data physio-portal:1.0.0
docker ps --filter name=physio-portal-task11
docker inspect physio-portal-task11 --format '{{.Id}} {{.Image}} {{json .NetworkSettings.Ports}}'
docker logs --tail 40 physio-portal-task11
Invoke-RestMethod http://localhost:8086/actuator/health
docker stop physio-portal-task11
docker start physio-portal-task11
docker restart physio-portal-task11
docker logs --tail 40 physio-portal-task11
docker stop physio-portal-task11
docker rm physio-portal-task11
docker run -d --name physio-portal-task11-final --label project=physio-portal-task11 -p 127.0.0.1:8086:8080 --mount type=volume,source=physio-portal-task11-data,target=/app/data physio-portal:1.0.0
docker ps --filter name=physio-portal-task11-final
Invoke-RestMethod http://localhost:8086/actuator/health
```

The observed health output was `status: UP`. The temporary container was removed with `docker rm`, while the final container remains available for a local demonstration. Do not delete the volume: it may contain demonstration accounts and bookings.

## Screenshot evidence

![Task 11 portal served from the running container](../screenshots/T11_docker_running.png)

**Screenshot required?** Yes. **What it shows:** a genuine 1440 × 900 headless Chrome capture of `http://127.0.0.1:8086/physiotherapists`, with both fictional providers visible while the final container was running. The screenshot alone does not show Docker metadata; the adjacent [CLI transcript](evidence/T11_docker_lifecycle.txt) shows its container ID, image ID, tag, `Up` state, logs, and exact port mapping. **Command location/type:** PowerShell at the project root:

```powershell
$chromeExe = 'C:\Program Files\Google\Chrome\Application\chrome.exe'
$shot = Join-Path (Get-Location).Path 'screenshots\T11_docker_running.png'
$profile = Join-Path $env:TEMP ('physio-task11-chrome-' + [guid]::NewGuid().ToString('N'))
& $chromeExe '--headless' '--disable-gpu' '--no-first-run' '--window-size=1440,900' "--user-data-dir=$profile" "--screenshot=$shot" 'http://127.0.0.1:8086/physiotherapists'
```

**Expected and observed result:** a real portal page from the mapped container port. **Filename:** `screenshots/T11_docker_running.png`.

## Startup blocker observed on 3 October 2026

The installed Docker CLI was 29.7.2 with context `desktop-linux`, but `docker version` initially could not connect to `npipe:////./pipe/dockerDesktopLinuxEngine`. Docker Desktop 4.90.0 reported `starting services: initializing Ingest server` because Windows could not rename `sailor-ingest.sock` to its `.stale` file. Both zero-byte socket entries had September timestamps. Narrow file removal failed with Windows error `The file cannot be accessed by the system` ([initial failure](evidence/T11_docker_startup_blocker.txt)).

With the failed Docker processes and `docker-desktop` WSL distribution stopped, I moved the temporary `%LOCALAPPDATA%\Docker\run` and `%LOCALAPPDATA%\docker-secrets-engine` folders to dated sibling backups and recreated both empty. The first staggered attempt exposed the second stale folder; a retry with both empty started the engine. Images, containers, volumes, settings, and the Docker data disk were preserved. No factory reset or Windows restart was needed. A [Docker Desktop issue report](https://github.com/docker/desktop-feedback/issues/554) describes the same Windows socket error and folder workaround; recurrence after a future shutdown is possible, so the temporary folders should be checked again if startup fails.
