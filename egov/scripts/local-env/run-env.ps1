<#
 Start/stop the local OBPS environment: Redis, Elasticsearch, captcha stub, WildFly 36.
   powershell -File run-env.ps1 start|stop|status|fixdb|deploy [-EnvDir <dir>] [-Ear <path>]
   start   starts everything (refuses if WildFly is already running: a 2nd instance breaks the first)
   stop    stops WildFly, Elasticsearch, the stub and Redis
   status  shows which ports are listening and the EAR deployment marker
   fixdb   runs post-first-start.sql (needed once, after the first start created the tables)
   deploy  copies the built EAR into WildFly deployments (default egov-ear\target\egov-ear-*.ear)
#>
param(
  [Parameter(Position = 0)][ValidateSet('start', 'stop', 'status', 'fixdb', 'deploy')][string]$Action = 'status',
  [string]$EnvDir, [string]$Jdk17Home, [string]$RedisExe, [string]$PsqlExe, [string]$Ear,
  [string]$DbName = 'egovtest', [string]$DbHost = 'localhost', [int]$DbPort = 5432, [string]$DbUser = 'postgres', [string]$DbPassword = 'postgres'
)
. (Join-Path $PSScriptRoot '_common.ps1')

function Test-Port([int]$p) { [bool](Get-NetTCPConnection -State Listen -LocalPort $p -ErrorAction SilentlyContinue) }
function Show-Status {
  foreach ($s in @(@('PostgreSQL', $DbPort), @('Redis', 6379), @('Elasticsearch', 9200), @('Captcha stub', 9099), @('WildFly https', 8443), @('WildFly mgmt', 9990))) {
    '{0,-14} :{1,-5} {2}' -f $s[0], $s[1], $(if (Test-Port $s[1]) { 'UP' } else { 'down' })
  }
  Get-ChildItem (Join-Path $WfDir 'standalone\deployments') -Filter 'egov.ear.*' -ErrorAction SilentlyContinue | ForEach-Object { "marker: $($_.Name)" }
}

switch ($Action) {
  'status' { Show-Status }
  'stop' {
    $full = [System.IO.Path]::GetFullPath($EnvDir)
    Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -and $_.CommandLine.Contains($full) -and $_.CommandLine -match 'jboss-modules|elasticsearch|captcha_stub' } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force }
    Get-Process redis-server, memurai -ErrorAction SilentlyContinue | Stop-Process -Force
    'stopped'
  }
  'start' {
    if ((Test-Port 8443) -or (Test-Port 9990)) { throw 'WildFly already running (port 8443/9990 in use). Run "stop" first; never start twice.' }
    $env:JAVA_HOME = Find-Jdk17 $Jdk17Home; $env:NOPAUSE = 'true'
    $redis = Find-Redis $RedisExe
    if (-not (Test-Port 6379)) { if ($redis) { Start-Process $redis -WindowStyle Hidden } else { Write-Warning 'Redis not running and redis-server not found: start Redis on 6379 yourself.' } }
    if (-not (Test-Port 9200)) {
      $env:ES_JAVA_OPTS = '-Xms512m -Xmx512m'
      Start-Process (Join-Path $EsDir 'bin\elasticsearch.bat') -WindowStyle Hidden
    }
    $py = Find-Python
    if ($py -and -not (Test-Port 9099)) { Start-Process $py -ArgumentList "`"$(Join-Path $Kit 'captcha_stub.py')`"" -WindowStyle Hidden }
    $env:JAVA_OPTS = '-Xms1g -Xmx3g --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.desktop/java.awt.font=ALL-UNNAMED -Djava.awt.headless=true'
    # The app calls itself over https (BPA -> EDCR); trust WildFly's self-signed cert via a truststore = JDK cacerts + that cert.
    $conf = Join-Path $WfDir 'standalone\configuration'; $ts = Join-Path $conf 'local-truststore.jks'
    if (-not (Test-Path $ts) -and (Test-Path (Join-Path $conf 'application.keystore'))) {
      $kt = Join-Path $env:JAVA_HOME 'bin\keytool.exe'; $cer = Join-Path $conf 'server.cer'
      Copy-Item (Join-Path $env:JAVA_HOME 'lib\security\cacerts') $ts
      & $kt -exportcert -alias server -keystore (Join-Path $conf 'application.keystore') -storepass password -file $cer | Out-Null
      & $kt -importcert -noprompt -alias localhost-wildfly -file $cer -keystore $ts -storepass changeit | Out-Null
    }
    if (Test-Path $ts) { $env:JAVA_OPTS += " -Djavax.net.ssl.trustStore=$ts -Djavax.net.ssl.trustStorePassword=changeit" }
    Start-Process (Join-Path $WfDir 'bin\standalone.bat') -ArgumentList '-c', 'standalone-full.xml' -WindowStyle Hidden
    "started (WildFly ~1 min to listen, ES ~30 s). Log: $WfDir\standalone\log\server.log`nApp: https://localhost:8443/egi/  (egovernments / demo)"
  }
  'deploy' {
    if (-not $Ear) { $Ear = (Get-ChildItem (Join-Path $EgovRoot 'egov-ear\target') -Filter 'egov-ear-*.ear' -ErrorAction SilentlyContinue | Select-Object -First 1).FullName }
    if (-not $Ear -or -not (Test-Path $Ear)) { throw 'EAR not found. Build it first (docs/LOCAL-SETUP.md step 4) or pass -Ear <path>.' }
    Copy-Item $Ear (Join-Path $WfDir 'standalone\deployments\egov.ear') -Force
    "copied $Ear. Wait for egov.ear.deployed (1-5 min; 404s meanwhile). Check with: run-env.ps1 status"
  }
  'fixdb' {
    $psql = Find-Psql $PsqlExe; if (-not $psql) { throw 'psql not found (pass -PsqlExe).' }
    $env:PGPASSWORD = $DbPassword
    & $psql -h $DbHost -p $DbPort -U $DbUser -d $DbName -v ON_ERROR_STOP=1 -f (Join-Path $Kit 'post-first-start.sql')
  }
}
