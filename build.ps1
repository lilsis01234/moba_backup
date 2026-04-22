# MOBA Project Build Script

$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$SrcDir = "$ProjectRoot\src"
$BinDir = "$ProjectRoot\bin"
$LibDir = "$ProjectRoot\libs"

if (-not (Test-Path $BinDir)) {
    New-Item -ItemType Directory -Path $BinDir -Force | Out-Null
}

Write-Host "Building MOBA project..." -ForegroundColor Cyan
Write-Host "  Source: $SrcDir"
Write-Host "  Output: $BinDir"
Write-Host "  Libraries: $LibDir"
Write-Host ""

$Libs = Get-ChildItem -Path $LibDir -Filter "*.jar" | ForEach-Object { $_.FullName }
$ClassPath = $Libs -join ";"

$SourceFiles = Get-ChildItem -Path $SrcDir -Filter "*.java" -Recurse | Where-Object { $_.FullName -notmatch "\\test\\" } | ForEach-Object { $_.FullName }

if ($SourceFiles.Count -eq 0) {
    Write-Host "No Java source files found!" -ForegroundColor Red
    exit 1
}

Write-Host "Compiling $($SourceFiles.Count) Java files..."

$JavaC = @(
    "-d", $BinDir,
    "-cp", $ClassPath,
    "-sourcepath", $SrcDir
)

foreach ($file in $SourceFiles) {
    $JavaC += $file
}

& javac @JavaC

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build FAILED!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Build SUCCESS!" -ForegroundColor Green
Write-Host "  Output directory: $BinDir"