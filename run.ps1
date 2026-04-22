# MOBA Project Run Script

$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "Running MOBA..." -ForegroundColor Cyan
Write-Host ""

& "$ProjectRoot\build.ps1"

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed, cannot run!" -ForegroundColor Red
    exit 1
}

$BinDir = "$ProjectRoot\bin"
$LibDir = "$ProjectRoot\libs"
$SrcDir = "$ProjectRoot\src"
$MainClass = "main.InterfaceLauncher"

$Libs = Get-ChildItem -Path $LibDir -Filter "*.jar" | ForEach-Object { $_.FullName }
$ClassPath = $Libs -join ";"

Write-Host ""
Write-Host "Starting game..." -ForegroundColor Green
Write-Host ""

& java -cp "$BinDir;$SrcDir;$ClassPath" $MainClass