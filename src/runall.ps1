# === CONFIGURATION ===
# Save initial folder
$startPath = Get-Location

$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Definition
$projectsPath = Join-Path $scriptPath "..\projects"

# Projects categorized by Java version
$projectsJava8 = @("openmrs")
#$projectsJava22 = @("roller")
$projectsJava22 = @("jeromq", "compress")
$projectsJava17 = @("jackrabbit")

# Java Paths
$java8Path = "C:\Program Files\Java\jdk1.8.0_202"
$java22Path = "C:\Program Files\Java\jdk-22"
$java17Path = "C:\Program Files\Java\jdk-17"

# Combine all projects
$projects = $projectsJava8 + $projectsJava22 + $projectsJava17

# Versions to test
$versions = @("Init", "noModular", "noDependency")

# Number of runs
$repeat = 10
$currentRun = 1

# JProfiler
$jprofilerDll = "C:\PROGRA~1\jprofiler15\bin\windows-x64\jprofilerti.dll"
$jprofilerConfig = "$env:USERPROFILE\.jprofiler15\jprofiler_config.xml"

# Telegram notifications
$notify = $true

# === EXECUTION ===
foreach ($project in $projects) {
    Write-Host "`n================ PROJECT: $project ================" -ForegroundColor Yellow

    for ($run = $currentRun; $run -le $repeat; $run++) {
        Write-Host "`n======== RUN $run ========" -ForegroundColor Magenta

        foreach ($version in $versions) {
            $projectPath = Join-Path $projectsPath "$project\$version"
            if (!(Test-Path $projectPath)) {
                Write-Host "Folder not found: $projectPath" -ForegroundColor Red
                continue
            }

            Set-Location $projectPath
            Write-Host ("--- Executing $project ($version) - Run $run ---") -ForegroundColor Cyan

            # Dynamically change JAVA_HOME
            if ($projectsJava8 -contains $project) {
                $env:JAVA_HOME = $java8Path
                Write-Host "Using Java 8 for $project" -ForegroundColor DarkCyan
            } elseif ($projectsJava22 -contains $project) {
                $env:JAVA_HOME = $java22Path
                Write-Host "Using Java 22 for $project" -ForegroundColor DarkCyan
            } elseif ($projectsJava17 -contains $project) {
                $env:JAVA_HOME = $java17Path
                Write-Host "Using Java 17 for $project" -ForegroundColor DarkCyan
            } else {
                Write-Host "Java not defined for project $project" -ForegroundColor Yellow
                continue
            }
            $env:Path = "$($env:JAVA_HOME)\bin;$($env:Path)"

            # Print the active Java version
            Write-Host "Current Java version:" -ForegroundColor Gray
            & java -version

            $argLine = "-agentpath:$jprofilerDll=offline,id=113,config=$jprofilerConfig"

            mvn test `
                -DargLine="$argLine" `
                -D"maven.test.failure.ignore=true" `
                -D"rat.skip=true" `
                -D"enforcer.skip=true"

            $exitCode = $LASTEXITCODE

            if ($exitCode -ne 0) {
                Write-Host " Error during execution of $project ($version) - Run $run" -ForegroundColor Red
                if ($notify) {
                    Set-Location $startPath
                    $date = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
                    $errorMsg = " [$date] mvn test failed on project **$project**, version **$version**, run **$run**"
                    python .\notification\SendMessage.py $errorMsg
                    Set-Location $projectPath
                }
            }

            # Search and rename all snapshotAnalysis.jps found in the current version
            $snapshots = Get-ChildItem -Filter "snapshotAnalysis.jps" -Path $projectPath -Recurse -ErrorAction SilentlyContinue

            if ($snapshots.Count -eq 0) {
                Write-Host "No snapshotAnalysis.jps found in $projectPath" -ForegroundColor DarkYellow
            } else {
                $i = 1
                foreach ($snapshot in $snapshots) {
                    $newName = "snapshotAnalysis${run}.jps"
                    $newPath = Join-Path $snapshot.Directory.FullName $newName

                    if (Test-Path $newPath) {
                        Remove-Item $newPath -Force
                    }

                    Rename-Item -Path $snapshot.FullName -NewName $newName
                    Write-Host ("Renamed: $($snapshot.FullName) → $newName") -ForegroundColor Cyan
                    $i++
                }
            }

            Write-Host ("Completed $project ($version) - Run $run") -ForegroundColor Green
            Write-Host ""
        }

        Set-Location $startPath
        $msg = "RUN $run completed"
        python .\notification\SendMessage.py $msg true
    }
}

# === RETURN TO START DIRECTORY ===
Set-Location $startPath

# === RUN PYTHON SCRIPTS ===
python snapshot_extractor.py
python performance.py

if ($notify) {
    python .\notification\SendMessage.py
}

Set-Location $startPath
