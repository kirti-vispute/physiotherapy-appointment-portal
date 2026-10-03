# Task 11 — Docker image and container lifecycle

## Status

The Dockerfile and build context are prepared. On 3 October 2026, `mvn -B clean package` passed all 34 backend tests and produced the executable JAR recorded in [package evidence](evidence/T11_package_result.txt). The Docker lifecycle is **pending**: Docker Desktop's Linux engine did not start because its existing `sailor-ingest.sock` files were inaccessible to Windows. No image, container, port response, or Docker screenshot is claimed yet.

Task 11 stops after the local image and lifecycle demonstration. Registry publication and Jenkins deployment belong to Task 12.

## Runtime layout

| Item | Value |
|---|---|
| Versioned image | `physio-portal:1.0.0` |
| Temporary demonstration container | `physio-portal-task11` |
| Final demonstration container | `physio-portal-task11-final` |
| Host → container port | `127.0.0.1:8086` → `8080/tcp` |
| Persistent H2 data | Docker volume `physio-portal-task11-data` → `/app/data` |
| Health endpoint | `http://localhost:8086/actuator/health` |

The Dockerfile uses a Java 21 runtime and UID 10001, copies only the packaged application, and exposes 8080. The named volume keeps the H2 file when the demonstration container is removed. Port 8086 avoids the existing local Jenkins and portal ports.

## Commands to execute from the project root

Use PowerShell with Docker Desktop's Linux engine running. These commands are planned, **not results**. Save the actual terminal outputs and IDs after execution.

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

Expected health output is `status: UP`; it will be marked verified only after a real response. The temporary container is removed with `docker rm`, while the final container remains available for a local demonstration. Do not delete the volume: it may contain demonstration accounts and bookings.

## Startup blocker observed on 3 October 2026

The installed Docker CLI was 29.7.2 with context `desktop-linux`, but `docker version` could not connect to `npipe:////./pipe/dockerDesktopLinuxEngine`. Docker Desktop 4.90.0 reported `starting services: initializing Ingest server` because Windows could not rename `sailor-ingest.sock` to its `.stale` file. Both zero-byte socket entries had September timestamps. A normal app start, `docker desktop start`, stopping the failed processes and the Docker WSL distribution, and narrow attempts to remove the two old entries all failed; Windows reported `The file cannot be accessed by the system`. No factory reset or Docker data deletion was attempted. A Windows restart is needed before retrying the commands above.
