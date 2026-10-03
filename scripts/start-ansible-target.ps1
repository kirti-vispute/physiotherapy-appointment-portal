# Keep the Ubuntu WSL target running while its separate Docker Engine serves the app.
param()

$ErrorActionPreference = 'Stop'
$pidFile = Join-Path $PSScriptRoot '..\target\ansible-wsl-keeper.pid'
New-Item -ItemType Directory -Path (Split-Path -Parent $pidFile) -Force | Out-Null

if (Test-Path -LiteralPath $pidFile) {
    $existingPid = 0
    if ([int]::TryParse((Get-Content -LiteralPath $pidFile -Raw).Trim(), [ref]$existingPid)) {
        $existing = Get-CimInstance Win32_Process -Filter "ProcessId=$existingPid" -ErrorAction SilentlyContinue
        if ($existing -and $existing.Name -eq 'wsl.exe' -and $existing.CommandLine -like '*Ubuntu-24.04*sleep infinity*') {
            Write-Output "Ubuntu WSL keeper already running (PID $existingPid)."
            exit 0
        }
    }
}

$keeper = Start-Process -FilePath 'C:\Windows\System32\wsl.exe' -ArgumentList @('-d','Ubuntu-24.04','-u','root','--','sleep','infinity') -WindowStyle Hidden -PassThru
Start-Sleep -Seconds 2
if ($keeper.HasExited) { throw 'Ubuntu WSL keeper exited during startup.' }
Set-Content -LiteralPath $pidFile -Value $keeper.Id -Encoding Ascii
Write-Output "Ubuntu WSL keeper started (PID $($keeper.Id))."
