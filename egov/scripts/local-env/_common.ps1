# Shared settings/helpers for setup-env.ps1 and run-env.ps1 (dot-sourced; not run directly).
$ErrorActionPreference = 'Stop'
$script:Kit      = $PSScriptRoot                                            # egov/scripts/local-env
$script:EgovRoot = (Resolve-Path (Join-Path $Kit '..\..')).Path             # egov/  (Maven root)
$script:RepoRoot = (Resolve-Path (Join-Path $EgovRoot '..')).Path           # OBPS-New/ (git root)
if (-not $EnvDir) { $EnvDir = Join-Path $RepoRoot 'egov-testenv' }
$script:WfVer    = '36.0.1.Final'
$script:EsVer    = '7.17.28'
$script:PgDrvVer = '42.7.13'
$script:WfDir    = Join-Path $EnvDir "wildfly-$WfVer"
$script:EsDir    = Join-Path $EnvDir "elasticsearch-$EsVer"

function Find-Jdk17([string]$Hint) {
  $cands = @($Hint, $env:JAVA_HOME) + @(Get-ChildItem 'C:\Program Files\Java', 'C:\Program Files\Eclipse Adoptium', 'C:\Program Files\Microsoft', 'C:\Program Files\Zulu' -Directory -Filter '*17*' -ErrorAction SilentlyContinue | ForEach-Object FullName)
  foreach ($c in $cands) {
    if ($c -and (Test-Path (Join-Path $c 'bin\java.exe'))) {
      $v = (& cmd /c "`"$(Join-Path $c 'bin\java.exe')`" -version 2>&1") | Out-String
      if ($v -match 'version "17\.') { return $c }
    }
  }
  throw 'JDK 17 not found. Install it and pass -Jdk17Home "<path>" (JDK 8/11/21 will not work: WildFly 36 and the --add-opens flags are tuned for 17).'
}
function Find-Tool([string]$Name, [string[]]$Extra, [string]$Hint) {
  if ($Hint -and (Test-Path $Hint)) { return $Hint }
  $c = Get-Command $Name -ErrorAction SilentlyContinue
  if ($c) { return $c.Source }
  foreach ($p in $Extra) { $m = Get-ChildItem $p -ErrorAction SilentlyContinue | Select-Object -First 1; if ($m) { return $m.FullName } }
  return $null
}
function Find-Psql([string]$Hint)  { Find-Tool 'psql' @('C:\Program Files\PostgreSQL\*\bin\psql.exe') $Hint }
function Find-Redis([string]$Hint) { Find-Tool 'redis-server' @('C:\Program Files\Redis\redis-server.exe', 'C:\Program Files\Memurai\memurai.exe') $Hint }
function Find-Python { Find-Tool 'python' @() $null }
