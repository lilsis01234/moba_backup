$ProjectDir = $PSScriptRoot
$SrcDir = "$ProjectDir\src"
$BinDir = "$ProjectDir\bin"
$LibsDir = "$ProjectDir\libs"

$Libs = Get-ChildItem -Path $LibsDir -Filter "*.jar" | ForEach-Object { $_.FullName }
$Classpath = ($Libs -join ";") + ";$BinDir"

Write-Host "Building project..." -ForegroundColor Cyan

if (!(Test-Path $BinDir)) {
    New-Item -ItemType Directory -Path $BinDir -Force | Out-Null
}

Copy-Item -Path "$SrcDir\game_config" -Destination $BinDir -Recurse -Force -ErrorAction SilentlyContinue
Copy-Item -Path "$SrcDir\res" -Destination $BinDir -Recurse -Force -ErrorAction SilentlyContinue

$SourceFiles = Get-ChildItem -Path $SrcDir -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName

$CompilerArgs = @(
    "-d", $BinDir,
    "-cp", $Classpath,
    "-source", "11",
    "-target", "11",
    "-encoding", "UTF-8"
) + $SourceFiles

$JavacPath = Get-Command javac -ErrorAction SilentlyContinue
if (-not $JavacPath) {
    Write-Host "ERROR: javac not found. Please ensure JDK is installed and in PATH." -ForegroundColor Red
    exit 1
}

& javac @CompilerArgs 2>&1 | ForEach-Object { Write-Host $_ -ForegroundColor Yellow }

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build FAILED" -ForegroundColor Red
    exit 1
}

Write-Host "Build SUCCEEDED" -ForegroundColor Green