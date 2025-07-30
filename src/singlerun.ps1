# === CONFIGURATION ===
$project = "active"
$version = "init"
$module = "activemq-kahadb-store"

$jprofilerDll = "C:\PROGRA~1\jprofiler15\bin\windows-x64\jprofilerti.dll"
$jprofilerConfig = "$env:USERPROFILE\.jprofiler15\jprofiler_config.xml"
$notify = $true

$startPath = Get-Location
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Definition
$projectPath = Join-Path $scriptPath "..\projects\$project\$version\$module"

# === START LOG ===
Write-Host "`nRunning tests for $project/$version/$module" -ForegroundColor Yellow
Write-Host "Active Java version:"
& java -version

# === NAVIGATE TO MODULE DIRECTORY ===
Set-Location $projectPath

# === EXECUTE TESTS ===
$argLine = "-agentpath:$jprofilerDll=offline,id=113,config=$jprofilerConfig"

mvn test `
    -D"argLine=$argLine" `
    -D"maven.test.failure.ignore=true" `
    -D"maven.failsafe.ignoreFailures=true" `
    -D"rat.skip=true" `
    -D"enforcer.skip=true"

$exitCode = $LASTEXITCODE

# === FAILURE NOTIFICATION ===
if ($exitCode -ne 0) {
    Write-Host " TEST FAILED for $module" -ForegroundColor Red
    if ($notify) {
        Set-Location $scriptPath
        python .\notification\SendMessage.py "Test failed: $project/$version/$module"
        Set-Location $projectPath
    }
} else {
    Write-Host " TEST PASSED for $module" -ForegroundColor Green
}

# === FINAL NOTIFICATION ===
if ($notify) {
    Set-Location $scriptPath
    python .\notification\SendMessage.py "Finished" true
}
Set-Location $startPath

