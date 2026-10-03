param(
    [ValidateSet('preflight','build','tag','push','stop','run','health')][string]$Action,
    [ValidateSet('demo','test')][string]$AppEnv = $env:APP_ENV,
    [ValidateSet('8087','8088')][string]$DockerPort = $env:DOCKER_PORT
)

$ErrorActionPreference = 'Stop'
if (!$AppEnv) { $AppEnv = 'test' }
if (!$DockerPort) { $DockerPort = '8087' }
$dockerExe = 'C:\Users\kirti\AppData\Local\Programs\DockerDesktop\resources\bin\docker.exe'
if (!(Test-Path -LiteralPath $dockerExe)) { throw "Docker CLI missing: $dockerExe" }
$env:DOCKER_HOST = 'npipe:////./pipe/dockerDesktopLinuxEngine'
$dockerConfigDir = Join-Path (Get-Location).Path 'target/docker-cli'
New-Item -ItemType Directory -Force -Path $dockerConfigDir | Out-Null
$dockerConfigJson = @{ cliPluginsExtraDirs = @('C:\Users\kirti\AppData\Local\Programs\DockerDesktop\resources\cli-plugins') } | ConvertTo-Json
[IO.File]::WriteAllText((Join-Path $dockerConfigDir 'config.json'),$dockerConfigJson,[Text.UTF8Encoding]::new($false))
$env:DOCKER_CONFIG = $dockerConfigDir
$env:DOCKER_BUILDKIT = '1'
$registry = 'localhost:5000'
$repository = "$registry/physio-portal"
$containerName = "physio-portal-cd-$AppEnv"
$volumeName = "physio-portal-cd-$AppEnv-data"
$logPath = Join-Path (Get-Location).Path "target/docker-$Action.log"
New-Item -ItemType Directory -Force -Path 'target' | Out-Null
"Task 12 Docker $Action at $(Get-Date -Format o)" | Set-Content -LiteralPath $logPath -Encoding UTF8

function Write-Record([string]$message) {
    Add-Content -LiteralPath $script:logPath -Value $message -Encoding UTF8
    Write-Host $message
}

function Invoke-Docker([string[]]$arguments) {
    Write-Record ('docker ' + ($arguments -join ' '))
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        $output = @(& $script:dockerExe @arguments 2>&1)
        $exitCode = $LASTEXITCODE
    } finally { $ErrorActionPreference = $previousPreference }
    $lines = @($output | ForEach-Object { [string]$_ })
    foreach ($line in $lines) { Write-Record $line }
    Write-Record "exit=$exitCode"
    if ($exitCode -ne 0) { throw "Docker command failed with exit $exitCode" }
    return $lines
}

function Get-ExactContainerId {
    $containerIds = @(Invoke-Docker @('ps','-a','--filter',"name=^/$script:containerName`$",'--format','{{.ID}}'))
    if ($containerIds.Count -gt 1) { throw 'More than one exact container match.' }
    if ($containerIds.Count -eq 0) { return $null }
    return [string]$containerIds[0]
}

if ($Action -eq 'preflight') {
    $serverVersion = @(Invoke-Docker @('version','--format','{{.Server.Version}}'))[-1]
    $osType = @(Invoke-Docker @('info','--format','{{.OSType}}'))[-1]
    $null = Invoke-Docker @('buildx','version')
    if ($osType -ne 'linux') { throw "Expected Docker Linux engine; found $osType" }
    $response = Invoke-WebRequest -Uri "http://127.0.0.1:5000/v2/" -UseBasicParsing -TimeoutSec 5
    if ($response.StatusCode -ne 200) { throw 'Local registry is not healthy.' }
    Write-Record "Docker server=$serverVersion; registry HTTP $($response.StatusCode)"
    exit 0
}

if ($env:BUILD_NUMBER -notmatch '^\d+$' -or $env:GIT_COMMIT -notmatch '^[0-9a-fA-F]{40}$') {
    throw 'Run build/tag/push/deployment actions from a Jenkins Git checkout with BUILD_NUMBER and GIT_COMMIT.'
}
$releaseTag = '1.0.0-b{0}-{1}' -f $env:BUILD_NUMBER, $env:GIT_COMMIT.Substring(0,12).ToLowerInvariant()
$localImage = "physio-portal:$releaseTag"
$registryImage = "${repository}:$releaseTag"
Write-Record "commit=$env:GIT_COMMIT build=$env:BUILD_NUMBER image=$registryImage"

switch ($Action) {
    'build' {
        if (!(Test-Path -LiteralPath 'target/physio-portal-1.0.0.jar')) { throw 'Tested JAR missing.' }
        $null = Invoke-Docker @('buildx','version')
        $null = Invoke-Docker @('build','--pull','-t',$localImage,'.')
        $null = Invoke-Docker @('image','inspect',$localImage,'--format','image_id={{.Id}}')
    }
    'tag' {
        $null = Invoke-Docker @('tag',$localImage,$registryImage)
        $null = Invoke-Docker @('image','inspect',$registryImage,'--format','image_id={{.Id}}')
    }
    'push' {
        $response = Invoke-WebRequest -Uri "http://127.0.0.1:5000/v2/" -UseBasicParsing -TimeoutSec 5
        if ($response.StatusCode -ne 200) { throw 'Local registry is not healthy.' }
        $null = Invoke-Docker @('push',$registryImage)
        $null = Invoke-Docker @('manifest','inspect',$registryImage)
        Write-Record "Registry manifest verified: $registryImage"
    }
    'stop' {
        $existingId = Get-ExactContainerId
        if ($existingId) {
            $labelsJson = @(Invoke-Docker @('inspect',$containerName,'--format','{{json .Config.Labels}}'))[-1]
            $labels = $labelsJson | ConvertFrom-Json
            if ($labels.project -ne 'physio-portal-cd' -or $labels.'managed-by' -ne 'jenkins') {
                throw "Existing container $containerName is not owned by this pipeline."
            }
            $running = @(Invoke-Docker @('inspect',$containerName,'--format','{{.State.Running}}'))[-1]
            if ($running -eq 'true') { $null = Invoke-Docker @('stop',$containerName) }
            $null = Invoke-Docker @('rm',$containerName)
            Write-Record "Removed previous owned container $existingId"
        } else { Write-Record "No previous $containerName container to stop." }
    }
    'run' {
        if (Get-ExactContainerId) { throw "Container name $containerName is still occupied." }
        $listeners = @(Get-NetTCPConnection -LocalPort ([int]$DockerPort) -State Listen -ErrorAction SilentlyContinue)
        if ($listeners.Count -gt 0) { throw "Host port $DockerPort is occupied." }
        $null = Invoke-Docker @('pull',$registryImage)
        $null = Invoke-Docker @('volume','create',$volumeName)
        $null = Invoke-Docker @('run','-d','--name',$containerName,
            '--label','project=physio-portal-cd','--label','managed-by=jenkins',
            '--label',"source.commit=$env:GIT_COMMIT",'--label',"jenkins.build=$env:BUILD_NUMBER",
            '--label',"app.env=$AppEnv",'-e',"SPRING_PROFILES_ACTIVE=$AppEnv",
            '-p',"127.0.0.1:${DockerPort}:8080",
            '--mount',"type=volume,source=$volumeName,target=/app/data",$registryImage)
        $null = Invoke-Docker @('ps','--filter',"name=^/$containerName`$",'--format','id={{.ID}} image={{.Image}} status={{.Status}} ports={{.Ports}}')
    }
    'health' {
        $containerId = Get-ExactContainerId
        if (!$containerId) { throw "Deployment container $containerName is absent." }
        $labelsJson = @(Invoke-Docker @('inspect',$containerName,'--format','{{json .Config.Labels}}'))[-1]
        $labels = $labelsJson | ConvertFrom-Json
        if ($labels.project -ne 'physio-portal-cd' -or $labels.'source.commit' -ne $env:GIT_COMMIT) {
            throw 'Running container does not match the Jenkins source commit.'
        }
        $deadline = (Get-Date).AddSeconds(90)
        $ready = $false
        do {
            try {
                $health = Invoke-RestMethod -Uri "http://127.0.0.1:$DockerPort/actuator/health" -TimeoutSec 3
                $ready = $health.status -eq 'UP'
            } catch { $ready = $false }
            if (!$ready) { Start-Sleep -Seconds 2 }
        } while (!$ready -and (Get-Date) -lt $deadline)
        if (!$ready) {
            $null = Invoke-Docker @('logs','--tail','80',$containerName)
            throw 'New container did not become healthy within 90 seconds.'
        }
        $homeStatus = (Invoke-WebRequest -Uri "http://127.0.0.1:$DockerPort/" -UseBasicParsing -TimeoutSec 10).StatusCode
        $providersStatus = (Invoke-WebRequest -Uri "http://127.0.0.1:$DockerPort/physiotherapists" -UseBasicParsing -TimeoutSec 10).StatusCode
        if ($homeStatus -ne 200 -or $providersStatus -ne 200) { throw 'Portal pages did not return HTTP 200.' }
        $inspect = @(& $dockerExe inspect $containerName | ConvertFrom-Json)[0]
        $image = @(& $dockerExe image inspect $registryImage | ConvertFrom-Json)[0]
        $state = [ordered]@{
            build_number = $env:BUILD_NUMBER; build_url = $env:BUILD_URL; git_commit = $env:GIT_COMMIT
            image_tag = $registryImage; image_id = $inspect.Image; registry_digests = $image.RepoDigests
            container_name = $containerName; container_id = $inspect.Id; app_env = $AppEnv
            host_port = [int]$DockerPort; container_port = 8080; volume = $volumeName
            health = $health.status; homepage_http = $homeStatus; providers_http = $providersStatus
            url = "http://localhost:$DockerPort/"; deployed_at = (Get-Date -Format o)
        }
        $state | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath 'target/docker-deployment.json' -Encoding UTF8
        Write-Record ($state | ConvertTo-Json -Compress -Depth 5)
        $null = Invoke-Docker @('logs','--tail','40',$containerName)
    }
}
