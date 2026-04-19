$ProjectDir = $PSScriptRoot
$BinDir = "$ProjectDir\bin"
$LibsDir = "$ProjectDir\libs"
$MainClass = "main.InterfaceLauncher"

$Libs = Get-ChildItem -Path $LibsDir -Filter "*.jar" | ForEach-Object { $_.FullName }
$Classpath = ($Libs -join ";") + ";$SrcDir;$BinDir"

Write-Host "Running game..." -ForegroundColor Cyan
Write-Host ""

$JavaArgs = @(
    "-cp", $Classpath,
    $MainClass
)

& java @JavaArgs