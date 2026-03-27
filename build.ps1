$ErrorActionPreference = "Stop"

$projectRoot = $PSScriptRoot
$srcDir = Join-Path $projectRoot "src"
$outDir = Join-Path $projectRoot "out"
$libDir = Join-Path $projectRoot "lib"
$classesDir = Join-Path (Join-Path $outDir "production") "moba_backup"

# Download dependencies if not present
$dependencies = @{
    "log4j-1.2.17.jar" = "https://repo1.maven.org/maven2/log4j/log4j/1.2.17/log4j-1.2.17.jar"
}

if (-not (Test-Path $libDir)) {
    New-Item -ItemType Directory -Force -Path $libDir | Out-Null
}

foreach ($dep in $dependencies.GetEnumerator()) {
    $jarName = $dep.Key
    $jarUrl = $dep.Value
    $jarPath = Join-Path $libDir $jarName
    
    if (-not (Test-Path $jarPath)) {
        Write-Host "Downloading $jarName..."
        Invoke-WebRequest -Uri $jarUrl -OutFile $jarPath
    }
}

# Build classpath from lib/*.jar
$libJars = @()
if (Test-Path $libDir) {
    $libJars = (Get-ChildItem -Path $libDir -Filter *.jar | ForEach-Object { $_.FullName }) -join ";"
}

# Clean previous build
if (Test-Path $outDir) {
    Remove-Item -Recurse -Force $outDir
}

# Collect all .java source files
$javaFiles = Get-ChildItem -Path $srcDir -Recurse -Filter *.java | ForEach-Object { $_.FullName }

if ($javaFiles.Count -eq 0) {
    Write-Error "No .java files found in $srcDir"
    exit 1
}

Write-Host "Compiling $($javaFiles.Count) Java files..."

# Create output directory and compile
New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

if ($libJars) {
    javac -encoding UTF-8 -cp $libJars -d $classesDir $javaFiles
} else {
    javac -encoding UTF-8 -d $classesDir $javaFiles
}

if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed."
    exit 1
}

# Copy non-Java resources (images, json, etc.) preserving package structure
Get-ChildItem -Path $srcDir -Recurse -File | Where-Object { $_.Extension -ne ".java" } | ForEach-Object {
    $relPath = $_.FullName.Substring($srcDir.Length + 1)
    $destPath = Join-Path $classesDir $relPath
    $destDir = Split-Path $destPath
    if (-not (Test-Path $destDir)) {
        New-Item -ItemType Directory -Force -Path $destDir | Out-Null
    }
    Copy-Item $_.FullName $destPath -Force
}

Write-Host "Build successful -> $classesDir"
