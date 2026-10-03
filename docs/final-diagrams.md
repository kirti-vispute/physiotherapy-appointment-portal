# Final diagrams — verified implementation

Each diagram names the components and labels the actual direction of use. These are source diagrams for the report and viva; the linked task guides contain the command and result evidence. All hosts and ports below are local to Kirti's computer.

## 1. Use cases

```mermaid
flowchart LR
    Visitor[Visitor] --> Register([Register])
    Visitor --> Providers([View physiotherapists and slots])
    Patient[Signed-in patient] --> Book([Book free future slot])
    Patient --> Status([View own confirmation and status])
    Patient --> Cancel([Cancel own future booking])
    Operator[Demo operator] --> Seed([Seed fictional providers and slots])
    CI[Jenkins or operator] --> Check([Check application health])
    Physio[Physiotherapist: represented stakeholder]
    Seed --> Providers
```

The physiotherapist has no interactive account in the frozen MVP. See the [SRS](srs.md) and [application architecture](architecture.md).

## 2. Application architecture

```mermaid
flowchart LR
    Browser[Patient browser and Thymeleaf pages] -->|HTTP| MVC[Spring MVC pages and REST API]
    MVC --> Service[Validated account and booking services]
    Service --> Repo[Spring Data JPA repositories]
    Repo --> DB[(H2 file database)]
    MVC --> Health[Actuator /actuator/health]
```

The Spring Boot JAR includes embedded Tomcat; `/app/data` is mounted as a named Docker volume for container deployments. See the [data model](architecture.md#data-model) and [API contract](api-documentation.md).

## 3. CI/CD pipeline

```mermaid
flowchart LR
    GitHub[GitHub develop] -->|Poll SCM and checkout| Jenkins[Jenkins on Windows]
    Jenkins --> Build[Maven compile, 34 JUnit cases, package]
    Build --> Browser[Temporary app on 8091; 5 Selenium cases]
    Browser -->|pass| Docker[Docker build and versioned tag]
    Browser -->|fail| Stop[Stop pipeline; preserve existing deployment]
    Docker --> Registry[Local registry on 5000]
    Registry --> Replace[Replace owned Docker Desktop container on 8087]
    Replace --> Health[Health UP and HTTP 200 gate]
```

The stages follow the committed [Jenkinsfile](../Jenkinsfile). See [Task 10](task-10-continuous-testing.md) for the deliberately failed test gate and [Task 12](task-12-docker-cd.md) for a successful end-to-end registry deployment.

## 4. Git branching and release

```mermaid
gitGraph
    commit id: "initial source"
    branch develop
    checkout develop
    branch feature/user-registration
    checkout feature/user-registration
    commit id: "registration"
    checkout develop
    merge feature/user-registration
    branch feature/appointment-booking
    checkout feature/appointment-booking
    commit id: "booking and conflict resolution"
    checkout develop
    merge feature/appointment-booking
    checkout main
    merge develop tag: "v1.0.0"
    checkout develop
    commit id: "CI, tests, Docker, Ansible"
```

The diagram compresses later feature PRs for readability. [Git workflow](git-workflow.md) names the real branches, PRs, conflict resolution, and release commit; `main` remains the Task 6 baseline while `develop` carries later work.

## 5. Docker deployment

```mermaid
flowchart LR
    JAR[Tested Spring Boot JAR] --> Image[Java 21 Docker image]
    Image --> Tagged[Commit-tagged version]
    Tagged --> Registry[Local registry localhost:5000]
    Registry --> Desktop[Docker Desktop engine on Windows]
    Desktop --> Container[physio-portal-cd container]
    Volume[(Named H2 data volume)] --> Container
    Container -->|127.0.0.1:8087 to 8080| Browser[Browser and health check]
```

The image tag, digest, container ID, and replacement result are in the [Task 12 guide](task-12-docker-cd.md). `8086` is the separate Task 11 lifecycle demonstration.

## 6. Ansible provisioning and recovery

```mermaid
flowchart LR
    Play[Ansible site.yml on Ubuntu WSL] --> Configure[Packages, Docker, account, folder, environment]
    Configure --> Deploy[Pull pinned image, create volume, run container]
    Registry[Same local registry on 5000] --> Deploy
    Deploy --> Ubuntu[Separate Ubuntu Docker Engine]
    Ubuntu -->|127.0.0.1:8089 to 8080| Check[Health and page checks]
    Check -->|bad PORT 8099 simulation| Failed[Health gate fails]
    Failed --> Reapply[Reapply versioned good PORT 8080]
    Reapply --> Deploy
```

The second full playbook run had `changed=0`. The bad release was an intentional port configuration error; the known good image and H2 volume were retained. See [Task 14 evidence](task-14-provisioning.md).

## 7. Complete DevOps lifecycle

```mermaid
flowchart LR
    Scope[Approved scope] --> Plan[Stories, backlog, sprints]
    Plan --> Code[Implement MVP on feature branch]
    Code --> Review[PR, review, merge]
    Review --> CI[Jenkins build and tests]
    CI -->|pass| Release[Versioned Docker image and registry]
    CI -->|fail| Improve[Diagnose and improve]
    Release --> Deploy[Docker deployment; Ansible target]
    Deploy --> Observe[Health, logs, browser check]
    Observe -->|failure| Recover[Restore known good configuration]
    Recover --> Improve
    Observe -->|feedback| Improve
    Improve --> Plan
```

The [agile plan](agile-plan.md#devops-lifecycle) records the same feedback loop. A future public cloud deployment is outside this local, free-service demonstration.
