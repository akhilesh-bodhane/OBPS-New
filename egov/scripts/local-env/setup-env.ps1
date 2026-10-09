<#
 One-time local environment setup for OBPS (WildFly 36 / Jakarta EE 10 / JDK 17).
 Downloads WildFly + Elasticsearch, installs the PostgreSQL driver module, configures datasources/JMS/cache,
 writes the settings override, creates the database + schemas. Safe to re-run (skips what exists).

   powershell -File setup-env.ps1 [-EnvDir <dir>] [-DbName egovtest] [-DbHost localhost] [-DbPort 5432]
                                  [-DbUser postgres] [-DbPassword postgres] [-Jdk17Home <path>] [-PsqlExe <path>]
 Does NOT build the EAR (see docs/LOCAL-SETUP.md step 4) and does NOT install PostgreSQL or Redis.
#>
param(
  [string]$EnvDir, [string]$DbName = 'egovtest', [string]$DbHost = 'localhost', [int]$DbPort = 5432,
  [string]$DbUser = 'postgres', [string]$DbPassword = 'postgres', [string]$Jdk17Home, [string]$PsqlExe, [string]$RedisExe
)
. (Join-Path $PSScriptRoot '_common.ps1')
$ProgressPreference = 'SilentlyContinue'   # Invoke-WebRequest is very slow with the progress bar

Write-Host '== 1/7 Prerequisites'
$jdk = Find-Jdk17 $Jdk17Home;  Write-Host "JDK 17   : $jdk"
$psql = Find-Psql $PsqlExe;    if (-not $psql) { throw 'psql not found. Install PostgreSQL 12+ (or pass -PsqlExe).' }; Write-Host "psql     : $psql"
$redis = Find-Redis $RedisExe
if (-not $redis) { Write-Warning 'redis-server not found. Install Redis/Memurai for Windows (or run Redis in Docker on 6379) before run-env.ps1 start.' } else { Write-Host "Redis    : $redis" }
if (-not (Find-Python)) { Write-Warning 'python not found: needed for the captcha stub (login page).' }
if ([System.IO.Path]::GetFullPath($EnvDir).Length -gt 80) { throw "EnvDir path is too long (Windows 260-char limit breaks the WildFly unzip). Use a short folder, e.g. -EnvDir C:\obps-env" }
New-Item -ItemType Directory -Force $EnvDir, (Join-Path $EnvDir 'downloads') | Out-Null

function Get-Zip([string]$Url, [string]$Name) {
  $f = Join-Path $EnvDir "downloads\$Name"
  if (-not (Test-Path $f)) { Write-Host "Downloading $Url"; Invoke-WebRequest $Url -OutFile $f -UseBasicParsing }
  return $f
}

function Expand-Zip([string]$Zip, [string]$Dest) {
  Add-Type -AssemblyName System.IO.Compression.FileSystem
  [System.IO.Compression.ZipFile]::ExtractToDirectory($Zip, $Dest)
}

Write-Host "== 2/7 WildFly $WfVer"
if (-not (Test-Path $WfDir)) {
  $z = Get-Zip "https://github.com/wildfly/wildfly/releases/download/$WfVer/wildfly-$WfVer.zip" "wildfly-$WfVer.zip"
  Expand-Zip $z $EnvDir
}
Write-Host "== 3/7 Elasticsearch $EsVer"
if (-not (Test-Path $EsDir)) {
  $z = Get-Zip "https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-$EsVer-windows-x86_64.zip" "elasticsearch-$EsVer.zip"
  Expand-Zip $z $EnvDir
}
$esYml = Join-Path $EsDir 'config\elasticsearch.yml'
if (-not (Select-String -Path $esYml -Pattern '^xpack.security.enabled' -Quiet)) {
  Add-Content $esYml "`nxpack.security.enabled: false`ndiscovery.type: single-node`nhttp.port: 9200"
}

Write-Host '== 4/7 Modules (org.postgres driver, org.egov.settings)'
$pgMod = Join-Path $WfDir 'modules\org\postgres\main'
New-Item -ItemType Directory -Force $pgMod | Out-Null
$jar = Join-Path $pgMod "postgresql-$PgDrvVer.jar"
if (-not (Test-Path $jar)) { Invoke-WebRequest "https://repo1.maven.org/maven2/org/postgresql/postgresql/$PgDrvVer/postgresql-$PgDrvVer.jar" -OutFile $jar -UseBasicParsing }
$pgXml = @"
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="org.postgres">
    <resources><resource-root path="postgresql-$PgDrvVer.jar"/></resources>
    <dependencies>
        <module name="java.sql"/><module name="java.naming"/><module name="java.logging"/><module name="java.xml"/><module name="java.management"/><module name="java.security.sasl"/><module name="java.security.jgss"/><module name="java.desktop"/>
    </dependencies>
</module>
"@
Set-Content (Join-Path $pgMod 'module.xml') $pgXml -Encoding UTF8
$setMod = Join-Path $WfDir 'modules\org\egov\settings\main'
New-Item -ItemType Directory -Force (Join-Path $setMod 'config') | Out-Null
$setXml = @'
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="org.egov.settings">
    <resources><resource-root path="."/></resources>
</module>
'@
Set-Content (Join-Path $setMod 'module.xml') $setXml -Encoding UTF8
Copy-Item (Join-Path $Kit 'egov-erp-override.properties') (Join-Path $setMod 'config\egov-erp-override.properties') -Force

Write-Host '== 5/7 WildFly datasources / JMS queues / master-data cache'
$marker = Join-Path $WfDir '.obps-cli-applied'
if (-not (Test-Path $marker)) {
  $cli = (Get-Content (Join-Path $Kit 'wildfly-setup.cli') -Raw).Replace('@DB_URL@', "jdbc:postgresql://${DbHost}:$DbPort/$DbName").Replace('@DB_USER@', $DbUser).Replace('@DB_PASSWORD@', $DbPassword)
  $tmp = Join-Path $EnvDir 'wildfly-setup.generated.cli'
  Set-Content $tmp $cli -Encoding ASCII
  $env:JAVA_HOME = $jdk; $env:NOPAUSE = 'true'
  & (Join-Path $WfDir 'bin\jboss-cli.bat') "--file=$tmp"
  if ($LASTEXITCODE -ne 0) { throw 'jboss-cli failed (see output above). The marker file is only written on success, so re-running retries this step; if it half-applied, delete the wildfly folder and re-run.' }
  Remove-Item $tmp
  New-Item $marker -ItemType File | Out-Null
} else { Write-Host "already applied ($marker)" }

Write-Host "== 6/7 Database $DbName (schemas state, generic)"
$env:PGPASSWORD = $DbPassword
$exists = & $psql -h $DbHost -p $DbPort -U $DbUser -tAc "SELECT 1 FROM pg_database WHERE datname='$DbName'"
if (-not $exists) { & $psql -h $DbHost -p $DbPort -U $DbUser -c "CREATE DATABASE $DbName" }
& $psql -h $DbHost -p $DbPort -U $DbUser -d $DbName -c 'CREATE SCHEMA IF NOT EXISTS state' -c 'CREATE SCHEMA IF NOT EXISTS generic'

Write-Host '== 7/7 Keep the env folder out of git'
$ex = Join-Path $RepoRoot '.git\info\exclude'
$full = [System.IO.Path]::GetFullPath($EnvDir)
if ((Test-Path $ex) -and $full.StartsWith($RepoRoot + '\')) {
  $rel = $full.Substring($RepoRoot.Length + 1).Replace('\', '/') + '/'
  if (-not (Select-String -Path $ex -SimpleMatch $rel -Quiet)) { Add-Content $ex $rel }
}

Write-Host @"

Setup done.  Env dir: $EnvDir
Next: 1) build the EAR      (docs/LOCAL-SETUP.md step 4)
      2) run-env.ps1 start   (once)
      3) run-env.ps1 deploy, wait ~5 min (Flyway creates the tables on first boot)
      4) run-env.ps1 fixdb   then open https://localhost:8443/egi/   (egovernments / demo)
(pass the same -EnvDir / -Db* values to run-env.ps1 if you changed the defaults)
"@
