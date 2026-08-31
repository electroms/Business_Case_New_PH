#!/usr/bin/env bash
set -Eeuo pipefail

# Cross-platform production launcher for Linux/macOS.
# It loads a local .env file when present, validates required production settings,
# and starts the Spring Boot app in prod mode.
# This wrapper intentionally invokes Maven through bash because the project may
# contain Windows-style CRLF line endings in the mvnw file when checked out on Windows.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SPRING_PROFILE="${SPRING_PROFILE:-prod}"
ENV_FILE="${ENV_FILE:-$SCRIPT_DIR/.env}"

# Loads KEY=VALUE pairs from a .env file and exports them in the current shell.
# It also removes optional surrounding quotes and the optional 'export ' prefix.
load_env_file() {
  local file="$1"

  if [[ ! -f "$file" ]]; then
    echo "[WARN] No .env file found at $file. Required production variables must be exported in the environment."
    return
  fi

  while IFS= read -r line || [[ -n "$line" ]]; do
    line="${line%$'\r'}"
    [[ -z "$line" || "$line" =~ ^[[:space:]]*# ]] && continue
    line="${line#export }"
    if [[ "$line" =~ ^[A-Za-z_][A-Za-z0-9_]*= ]]; then
      key="${line%%=*}"
      value="${line#*=}"
      value="${value%\"}"
      value="${value#\"}"
      value="${value%\'}"
      value="${value#\'}"
      export "$key=$value"
    fi
  done < "$file"
}

load_env_file "$ENV_FILE"

export SPRING_PROFILES_ACTIVE="${SPRING_PROFILE}"
: "${SERVER_PORT:=8080}"
: "${APP_ADMIN_USERNAME:=prodadmin}"
: "${APP_ADMIN_PASSWORD:=CHANGE_ME_STRONG_ADMIN_PASSWORD}"
: "${APP_ADMIN_ROLES:=ROLE_ADMIN,ROLE_USER}"
: "${JWT_EXPIRATION_MS:=3600000}"
: "${APP_CORS_ALLOWED_ORIGINS:=https://your-domain.example}"
: "${DB_DRIVER_CLASS_NAME:=com.mysql.cj.jdbc.Driver}"
: "${DDL_AUTO:=update}"
: "${HIBERNATE_DIALECT:=org.hibernate.dialect.MySQLDialect}"

if [[ -z "${JWT_SECRET:-}" ]]; then
  echo "[ERROR] JWT_SECRET is required. Set it in .env or export it before starting the app." >&2
  exit 1
fi

if [[ -z "${DB_URL:-}" ]]; then
  echo "[ERROR] DB_URL is required. Set it in .env or export it before starting the app." >&2
  exit 1
fi

if [[ -z "${DB_USERNAME:-}" ]]; then
  echo "[ERROR] DB_USERNAME is required. Set it in .env or export it before starting the app." >&2
  exit 1
fi

if [[ -z "${DB_PASSWORD:-}" ]]; then
  echo "[ERROR] DB_PASSWORD is required. Set it in .env or export it before starting the app." >&2
  exit 1
fi

if (( ${#JWT_SECRET} < 32 )); then
  echo "[ERROR] JWT_SECRET must be at least 32 characters long." >&2
  exit 1
fi

echo "Launching Spring Boot in profile: ${SPRING_PROFILES_ACTIVE}"
echo "Server port: ${SERVER_PORT}"
echo "Database URL: ${DB_URL}"
echo "CORS allowed origins: ${APP_CORS_ALLOWED_ORIGINS}"

# Run the Maven wrapper via bash so it works even when the wrapper has CRLF line endings.
exec bash "$SCRIPT_DIR/mvnw" spring-boot:run
