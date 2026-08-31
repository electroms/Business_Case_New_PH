param(
    [string]$SpringProfile = "prod",
    [string]$EnvFile = ".env"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Import-EnvFile {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    if (-not (Test-Path -LiteralPath $Path)) {
        throw "Environment file not found: $Path"
    }

    foreach ($line in Get-Content -LiteralPath $Path) {
        $trimmed = $line.Trim()
        if ([string]::IsNullOrWhiteSpace($trimmed) -or $trimmed.StartsWith("#")) {
            continue
        }

        if ($trimmed.StartsWith("export ")) {
            $trimmed = $trimmed.Substring(7).Trim()
        }

        $equalsIndex = $trimmed.IndexOf('=')
        if ($equalsIndex -lt 0) {
            continue
        }

        $name = $trimmed.Substring(0, $equalsIndex).Trim()
        $value = $trimmed.Substring($equalsIndex + 1).Trim()

        if ([string]::IsNullOrWhiteSpace($name)) {
            continue
        }

        if (($value.StartsWith('"') -and $value.EndsWith('"')) -or
            ($value.StartsWith("'") -and $value.EndsWith("'"))) {
            $value = $value.Substring(1, $value.Length - 2)
        }

        [Environment]::SetEnvironmentVariable($name, $value, "Process")
        Set-Item -Path "Env:$name" -Value $value
    }
}

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$resolvedEnvFile = if ([System.IO.Path]::IsPathRooted($EnvFile)) {
    $EnvFile
} else {
    Join-Path $scriptRoot $EnvFile
}

if (Test-Path -LiteralPath $resolvedEnvFile) {
    Import-EnvFile -Path $resolvedEnvFile
} else {
    Write-Warning "No .env file found at $resolvedEnvFile. Required production variables must be provided in the environment."
}

$env:SPRING_PROFILES_ACTIVE = $SpringProfile
if (-not $env:SERVER_PORT) { $env:SERVER_PORT = "8080" }
if (-not $env:APP_ADMIN_USERNAME) { $env:APP_ADMIN_USERNAME = "prodadmin" }
if (-not $env:APP_ADMIN_PASSWORD) { $env:APP_ADMIN_PASSWORD = "CHANGE_ME_STRONG_ADMIN_PASSWORD" }
if (-not $env:APP_ADMIN_ROLES) { $env:APP_ADMIN_ROLES = "ROLE_ADMIN,ROLE_USER" }
if (-not $env:JWT_SECRET) { throw "JWT_SECRET is required. Fill the .env file or export it before launching the app." }
if (-not $env:JWT_EXPIRATION_MS) { $env:JWT_EXPIRATION_MS = "3600000" }
if (-not $env:APP_CORS_ALLOWED_ORIGINS) { $env:APP_CORS_ALLOWED_ORIGINS = "https://your-domain.example" }
if (-not $env:DB_URL) { throw "DB_URL is required. Fill the .env file or export it before launching the app." }
if (-not $env:DB_USERNAME) { throw "DB_USERNAME is required. Fill the .env file or export it before launching the app." }
if (-not $env:DB_PASSWORD) { throw "DB_PASSWORD is required. Fill the .env file or export it before launching the app." }
if (-not $env:DB_DRIVER_CLASS_NAME) { $env:DB_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver" }
if (-not $env:DDL_AUTO) { $env:DDL_AUTO = "update" }
if (-not $env:HIBERNATE_DIALECT) { $env:HIBERNATE_DIALECT = "org.hibernate.dialect.MySQLDialect" }

if ($env:JWT_SECRET.Length -lt 32) {
    throw "JWT_SECRET must be at least 32 characters long."
}

Write-Host "Launching Spring Boot in profile: $env:SPRING_PROFILES_ACTIVE"
Write-Host "Server port: $env:SERVER_PORT"
Write-Host "Database URL: $env:DB_URL"
Write-Host "CORS allowed origins: $env:APP_CORS_ALLOWED_ORIGINS"

$projectRoot = $scriptRoot
$launcher = Join-Path $projectRoot "mvnw.cmd"
if (-not (Test-Path -LiteralPath $launcher)) {
    throw "Launcher not found: $launcher"
}

& $launcher spring-boot:run
