param([ValidateSet('start','stop')][string]$Action = 'start')
$ErrorActionPreference = 'Stop'
if (!$env:WORKSPACE -or $env:BUILD_NUMBER -notmatch '^\d+$') {
    throw 'Run this helper from a Jenkins build with WORKSPACE and BUILD_NUMBER.'
}
$port = 8091
$runHome = Join-Path $env:WORKSPACE 'target/selenium-app'
$stateFile = Join-Path $runHome 'run.json'
$marker = "-Dphysio.seleniumRun=$env:JOB_NAME-$env:BUILD_NUMBER"

function Get-RunProcess($state) {
    $candidate = Get-CimInstance Win32_Process -Filter "ProcessId = $([int]$state.pid)"
    if (!$candidate) { return $null }
    if ($candidate.Name -ne 'java.exe' -or
        !$candidate.CommandLine.Contains([string]$state.marker) -or
        !$candidate.CommandLine.Contains([string]$state.jar) -or
        $candidate.CreationDate.ToUniversalTime().ToString('o') -ne $state.process_created_utc) {
        throw 'Saved test-app PID belongs to another process; refusing to stop it.'
    }
    return $candidate
}

if ($Action -eq 'stop') {
    if (Test-Path -LiteralPath $stateFile) {
        $state = Get-Content -Raw -LiteralPath $stateFile | ConvertFrom-Json
        $owned = Get-RunProcess $state
        if ($owned) {
            Stop-Process -Id $owned.ProcessId
            Wait-Process -Id $owned.ProcessId -Timeout 30 -ErrorAction SilentlyContinue
        }
        Write-Output "Stopped owned Selenium test app for build $($state.build_number) on port $port"
    } else { Write-Output 'No recorded Selenium test app to stop.' }
    exit 0
}

if (@(Get-NetTCPConnection -State Listen -LocalPort $port -ErrorAction SilentlyContinue).Count -gt 0) {
    throw "Port $port is occupied; refusing to interfere with another process."
}
$jar = (Resolve-Path -LiteralPath (Join-Path $env:WORKSPACE 'target/physio-portal-1.0.0.jar')).Path
$java = Join-Path $env:JAVA_HOME 'bin/java.exe'
if (!(Test-Path -LiteralPath $java)) { throw 'JDK 21 JAVA_HOME is required.' }
New-Item -ItemType Directory -Force -Path $runHome | Out-Null
$stdout = Join-Path $runHome 'application.log'
$stderr = Join-Path $runHome 'application-error.log'
$stdin = Join-Path $runHome 'stdin.txt'
[IO.File]::WriteAllText($stdin, '')
$oldCookie = $env:JENKINS_NODE_COOKIE
$env:JENKINS_NODE_COOKIE = "physio-selenium-$env:BUILD_NUMBER"
$process = $null
try {
    $arguments = @(('"'+$marker+'"'), '-jar', ('"'+$jar+'"'),
        "--server.port=$port", '--server.address=127.0.0.1', '--spring.profiles.active=selenium')
    $launcher = New-Object -ComObject WScript.Shell
    $launcher.CurrentDirectory = $runHome
    $command = '"' + $java + '" ' + ($arguments -join ' ') + ' < "' + $stdin + '" > "' + $stdout + '" 2> "' + $stderr + '"'
    $null = $launcher.Run(('"' + $env:ComSpec + '" /d /s /c "' + $command + '"'), 0, $false)
    $launchDeadline = (Get-Date).AddSeconds(15)
    do {
        $processInfo = @(Get-CimInstance Win32_Process -Filter "Name = 'java.exe'" | Where-Object {
            $_.CommandLine -and $_.CommandLine.Contains($marker) -and $_.CommandLine.Contains($jar)
        })
        if ($processInfo.Count -eq 0) { Start-Sleep -Milliseconds 300 }
    } while ($processInfo.Count -eq 0 -and (Get-Date) -lt $launchDeadline)
    if ($processInfo.Count -ne 1) { throw 'Test app launch did not produce exactly one owned Java process; inspect logs.' }
    $process = Get-Process -Id $processInfo[0].ProcessId
    $deadline = (Get-Date).AddSeconds(90)
    $ready = $false
    do {
        if ($process.HasExited) { throw 'Test application exited before readiness; inspect logs.' }
        try {
            $health = Invoke-RestMethod -Uri "http://127.0.0.1:$port/actuator/health" -TimeoutSec 3
            $listener = Get-NetTCPConnection -State Listen -LocalPort $port -ErrorAction SilentlyContinue
            $ready = $health.status -eq 'UP' -and @($listener.OwningProcess) -contains $process.Id
        } catch { $ready = $false }
        if (!$ready) { Start-Sleep -Seconds 2 }
    } while (!$ready -and (Get-Date) -lt $deadline)
    if (!$ready) { throw 'Test application did not become healthy within 90 seconds.' }
    [ordered]@{
        build_number = $env:BUILD_NUMBER; git_commit = $env:GIT_COMMIT
        marker = $marker; pid = $process.Id; process_created_utc = $processInfo[0].CreationDate.ToUniversalTime().ToString('o')
        jar = $jar; port = $port; url = "http://127.0.0.1:$port/"; health = $health.status
        started_at = (Get-Date -Format o); stdout = $stdout; stderr = $stderr
    } | ConvertTo-Json | Set-Content -Encoding UTF8 -LiteralPath $stateFile
    Write-Output "Selenium test app ready: http://127.0.0.1:$port/ (PID $($process.Id), source $env:GIT_COMMIT)"
} catch {
    if ($process -and !$process.HasExited) { Stop-Process -Id $process.Id }
    throw
} finally { $env:JENKINS_NODE_COOKIE = $oldCookie }
