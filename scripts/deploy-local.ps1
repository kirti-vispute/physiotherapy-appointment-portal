param(
    [ValidateSet('demo','test')][string]$AppEnv = $env:APP_ENV,
    [ValidateSet('8081','8082')][string]$Port = $env:PORT,
    [ValidateSet('deploy','stop')][string]$Action = 'deploy'
)
$ErrorActionPreference = 'Stop'
if (!$AppEnv -or !$Port) { throw 'APP_ENV and PORT are required: demo/test and 8081/8082.' }
$deploymentHome = Join-Path $env:ProgramData "Jenkins/physio-portal-demo/$AppEnv-$Port"
$marker = "-Dphysio.deployment=$deploymentHome"
$stateFile = Join-Path $deploymentHome 'deployment.json'

function Get-OwnedProcess($state) {
    $candidate = Get-CimInstance Win32_Process -Filter "ProcessId = $([int]$state.pid)"
    if (!$candidate) { return $null }
    if ($candidate.Name -ne 'java.exe' -or
        !$candidate.CommandLine.Contains($marker) -or
        !$candidate.CommandLine.Contains([string]$state.jar) -or
        $candidate.CreationDate.ToUniversalTime().ToString('o') -ne $state.process_created_utc) {
        throw 'Saved PID belongs to another process; deployment stopped without terminating it.'
    }
    return $candidate
}

if (Test-Path -LiteralPath $stateFile) {
    $previous = Get-Content -Raw -LiteralPath $stateFile | ConvertFrom-Json
    $owned = Get-OwnedProcess $previous
} else { $owned = $null }
if ($Action -eq 'stop') {
    if ($owned) { Stop-Process -Id $owned.ProcessId; Wait-Process -Id $owned.ProcessId -Timeout 30 -ErrorAction SilentlyContinue }
    Write-Output "Stopped owned deployment, if running: $AppEnv on $Port"
    exit 0
}

# Validate the new artifact before replacing any previous owned deployment.
$artifact = (Resolve-Path -LiteralPath 'target/physio-portal-1.0.0.jar').Path
$java = Join-Path $env:JAVA_HOME 'bin/java.exe'
if (!(Test-Path -LiteralPath $java)) { throw 'JAVA_HOME must reference the configured JDK 21.' }
if ($env:BUILD_NUMBER -notmatch '^\d+$') { throw 'Run deployment from the Jenkins pipeline (BUILD_NUMBER required).' }
$listeners = @(Get-NetTCPConnection -State Listen -LocalPort ([int]$Port) -ErrorAction SilentlyContinue)
if ($listeners.Count -gt 0 -and (!$owned -or @($listeners | Where-Object { $_.OwningProcess -ne $owned.ProcessId }).Count -gt 0)) {
    throw "Port $Port is occupied by a process this deployment does not own."
}
$releaseHome = Join-Path $deploymentHome "releases/$env:BUILD_NUMBER"
New-Item -ItemType Directory -Force -Path $releaseHome | Out-Null
$deployedJar = Join-Path $releaseHome 'physio-portal-1.0.0.jar'
Copy-Item -LiteralPath $artifact -Destination $deployedJar
$checksum = (Get-FileHash -LiteralPath $deployedJar -Algorithm SHA256).Hash
if ($checksum -ne (Get-FileHash -LiteralPath $artifact -Algorithm SHA256).Hash) { throw 'Deployed JAR checksum mismatch.' }
if ($owned) { Stop-Process -Id $owned.ProcessId; Wait-Process -Id $owned.ProcessId -Timeout 30 -ErrorAction SilentlyContinue }
$stdout = Join-Path $releaseHome 'application.log'
$stderr = Join-Path $releaseHome 'application-error.log'
$stdin = Join-Path $releaseHome 'stdin.txt'
[IO.File]::WriteAllText($stdin, '')
$oldCookie = $env:JENKINS_NODE_COOKIE
$env:JENKINS_NODE_COOKIE = "physio-$AppEnv-$Port"
$process = $null
try {
    $arguments = @(('"'+$marker+'"'), '-jar', ('"'+$deployedJar+'"'),
        "--server.port=$Port", '--server.address=127.0.0.1', "--spring.profiles.active=$AppEnv")
    # WScript.Shell.Run detaches Windows handles from Jenkins' PowerShell wrapper.
    # Start-Process with redirected streams still inherited a wrapper pipe in run #1.
    $launcher = New-Object -ComObject WScript.Shell
    $launcher.CurrentDirectory = $deploymentHome
    $command = '"' + $java + '" ' + ($arguments -join ' ') + ' < "' + $stdin + '" > "' + $stdout + '" 2> "' + $stderr + '"'
    $null = $launcher.Run(('"' + $env:ComSpec + '" /d /s /c "' + $command + '"'), 0, $false)
    $launchDeadline = (Get-Date).AddSeconds(15)
    do {
        $processInfo = Get-CimInstance Win32_Process -Filter "Name = 'java.exe'" | Where-Object {
            $_.CommandLine -and $_.CommandLine.Contains($marker) -and $_.CommandLine.Contains($deployedJar)
        }
        if (!$processInfo) { Start-Sleep -Milliseconds 300 }
    } while (!$processInfo -and (Get-Date) -lt $launchDeadline)
    if (@($processInfo).Count -ne 1) { throw 'Detached Java launch did not produce exactly one owned process; inspect logs.' }
    $process = Get-Process -Id $processInfo.ProcessId
    $deadline = (Get-Date).AddSeconds(90)
    $ready = $false
    do {
        if ($process.HasExited) { throw 'Application process exited before readiness; inspect deployment logs.' }
        try {
            $health = Invoke-RestMethod -Uri "http://127.0.0.1:$Port/actuator/health" -TimeoutSec 3
            $listener = Get-NetTCPConnection -State Listen -LocalPort ([int]$Port) -ErrorAction SilentlyContinue
            $ready = $health.status -eq 'UP' -and @($listener.OwningProcess) -contains $process.Id
        } catch { $ready = $false }
        if (!$ready) { Start-Sleep -Seconds 2 }
    } while (!$ready -and (Get-Date) -lt $deadline)
    if (!$ready) { throw 'Application did not become healthy within 90 seconds.' }
    $state = [ordered]@{
        app_env = $AppEnv; port = [int]$Port; url = "http://localhost:$Port/"; health = $health.status
        pid = $process.Id; process_created_utc = $processInfo.CreationDate.ToUniversalTime().ToString('o')
        build_number = $env:BUILD_NUMBER; build_url = $env:BUILD_URL; git_commit = $env:GIT_COMMIT
        jar = $deployedJar; sha256 = $checksum; deployed_at = (Get-Date -Format o)
        stdout = $stdout; stderr = $stderr; deployment_home = $deploymentHome
    }
    $state | ConvertTo-Json | Set-Content -Encoding UTF8 -LiteralPath $stateFile
    Copy-Item -LiteralPath $stateFile -Destination 'target/deployment.json'
    $state | ConvertTo-Json | Write-Output
} catch {
    if ($process -and !$process.HasExited) { Stop-Process -Id $process.Id }
    throw
} finally {
    $env:JENKINS_NODE_COOKIE = $oldCookie
    if (Test-Path -LiteralPath $stdout) { Copy-Item -LiteralPath $stdout -Destination 'target/deploy-application.log' }
    if (Test-Path -LiteralPath $stderr) { Copy-Item -LiteralPath $stderr -Destination 'target/deploy-error.log' }
}
