param(
    [string]$SpringProfile = "prod",
    [string]$EnvFile = ".env"
)

if (Test-Path $EnvFile) {
    Get-Content $EnvFile | ForEach-Object {
        $line = $_.Trim()
        if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith("#")) {
            return
        }

        $parts = $line -split "=", 2
        if ($parts.Count -eq 2) {
            $name = $parts[0].Trim()
            $value = $parts[1].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
}

$env:SPRING_PROFILES_ACTIVE = $SpringProfile
if (-not $env:SERVER_PORT) { $env:SERVER_PORT = "8080" }
if (-not $env:APP_ADMIN_USERNAME) { $env:APP_ADMIN_USERNAME = "prodadmin" }
if (-not $env:APP_ADMIN_PASSWORD) { $env:APP_ADMIN_PASSWORD = "CHANGE_ME_STRONG_ADMIN_PASSWORD" }
if (-not $env:APP_ADMIN_ROLES) { $env:APP_ADMIN_ROLES = "ROLE_ADMIN,ROLE_USER" }
if (-not $env:JWT_SECRET) { throw "JWT_SECRET is required. Fill the .env file or export it before launching the app." }
if (-not $env:JWT_EXPIRATION_MS) { $env:JWT_EXPIRATION_MS = "3600000" }
if (-not $env:DB_URL) { throw "DB_URL is required. Fill the .env file or export it before launching the app." }
if (-not $env:DB_USERNAME) { throw "DB_USERNAME is required. Fill the .env file or export it before launching the app." }
if (-not $env:DB_PASSWORD) { throw "DB_PASSWORD is required. Fill the .env file or export it before launching the app." }
if (-not $env:DB_DRIVER_CLASS_NAME) { $env:DB_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver" }
if (-not $env:DDL_AUTO) { $env:DDL_AUTO = "update" }
if (-not $env:HIBERNATE_DIALECT) { $env:HIBERNATE_DIALECT = "org.hibernate.dialect.MySQLDialect" }

Write-Host "Launching Spring Boot in profile: $env:SPRING_PROFILES_ACTIVE"
Write-Host "Server port: $env:SERVER_PORT"
Write-Host "Database URL: $env:DB_URL"

& "./mvnw.cmd" spring-boot:run
