$projectRoot = $PSScriptRoot
$classesDir = Join-Path (Join-Path (Join-Path $projectRoot "out") "production") "moba_backup"
$libDir = Join-Path $projectRoot "lib"

if (-not (Test-Path $classesDir)) {
    Write-Host "No build found. Running build.ps1 first..."
    & (Join-Path $projectRoot "build.ps1")
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Build failed, cannot run."
        exit 1
    }
}

$libJars = @()
if (Test-Path $libDir) {
    $libJars = (Get-ChildItem -Path $libDir -Filter *.jar | ForEach-Object { $_.FullName }) -join ";"
}

$cp = if ($libJars) { "$classesDir;$libJars" } else { $classesDir }

java -cp $cp main.InterfaceLauncher
