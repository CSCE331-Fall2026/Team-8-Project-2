[CmdletBinding()]
param(
  [string]$JavaFxHome = $env:JAVAFX_HOME,
  [string]$OutputDirectory = "docs\javadocs"
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot

if (-not $JavaFxHome) {
  foreach ($candidate in @("javafx-sdk-25", "javafx-sdk-27")) {
    $candidatePath = Join-Path $projectRoot $candidate
    if (Test-Path (Join-Path $candidatePath "lib")) {
      $JavaFxHome = $candidatePath
      break
    }
  }
}

if (-not $JavaFxHome -or -not (Test-Path (Join-Path $JavaFxHome "lib"))) {
  throw "JavaFX SDK not found. Install it in the project root or set JAVAFX_HOME to its directory."
}

$javaFxLib = Join-Path $JavaFxHome "lib"
$outputPath = Join-Path $projectRoot $OutputDirectory
$sourceFiles = @(Get-ChildItem (Join-Path $projectRoot "src") -Filter "*.java" -Recurse |
  Where-Object { $_.Name -ne "dbSetup.java" } |
  Select-Object -ExpandProperty FullName)

if ($sourceFiles.Count -eq 0) {
  throw "No Java source files were found under src."
}

if (Test-Path $outputPath) {
  Remove-Item $outputPath -Recurse -Force
}

New-Item $outputPath -ItemType Directory -Force | Out-Null
& javadoc -private -Xdoclint:all,-missing -d $outputPath --module-path $javaFxLib --add-modules javafx.controls,javafx.fxml $sourceFiles
if ($LASTEXITCODE -ne 0) {
  throw "Javadoc generation failed with exit code $LASTEXITCODE."
}

Write-Host "Javadocs written to $outputPath"
