# Business Case Spring

A full-stack Spring Boot + Angular application for the Business Case project.

## Overview

This repository contains:

- a Java 21 backend built with Spring Boot 3.5.x
- an Angular 22 frontend
- JWT-based authentication using Spring Security and OAuth2 Resource Server
- a persistent user identity stored in MySQL instead of in-memory storage
- a strict production profile with required environment variables
- a Debian Linux server deployment profile for production environments

## Tech stack

### Backend

- Java 21 LTS
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security
- OAuth2 Resource Server
- MySQL Connector J
- H2 for test usage only

### Frontend

- Angular 22
- TypeScript
- RxJS
- Standalone components
- JWT auth interceptor for API requests

## Prerequisites

- OpenJDK 21 LTS (recommended on Debian 12/13)
- Maven Wrapper included: `./mvnw` or `./mvnw.cmd`
- Node.js 22+
- npm 10+
- MySQL for the production profile
- Debian Linux server ready for systemd service deployment

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/electroms/Business_Case_New_PH.git
cd Business_Case_New_PH
```

### 2. Install backend dependencies

On a Debian server, ensure Java 21 is installed:

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk-headless
```

Then build the backend:

```bash
./mvnw clean package
```

### 3. Install frontend dependencies

```bash
cd businesscase-frontend
npm install
```

## Configuration

The project uses environment variables for sensitive runtime settings.

### Main configuration files

- [src/main/resources/application.properties](src/main/resources/application.properties)
- [src/main/resources/application-prod.properties](src/main/resources/application-prod.properties)
- [prod.env.example](prod.env.example)
- [.env](.env)
- [start-prod.sh](start-prod.sh)
- [start-prod.ps1](start-prod.ps1)
- [businesscase.service](businesscase.service)

### Production environment variables

Create a local file named [.env](.env) at the project root and fill it with real production values. This file is ignored by Git and must never be committed.

```dotenv
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

APP_ADMIN_USERNAME=prodadmin
APP_ADMIN_PASSWORD=CHANGE_ME_STRONG_ADMIN_PASSWORD
APP_ADMIN_ROLES=ROLE_ADMIN,ROLE_USER

JWT_SECRET=CHANGE_ME_A_STRONG_SECRET_AT_LEAST_32_CHARS
JWT_EXPIRATION_MS=3600000
APP_CORS_ALLOWED_ORIGINS=https://your-domain.example

DB_URL=jdbc:mysql://localhost:3306/businesscase?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=20000
DB_USERNAME=businesscase_user
DB_PASSWORD=CHANGE_ME_DB_PASSWORD
DB_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

DDL_AUTO=update
HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
```

Required production rules:

- `JWT_SECRET` must be at least 32 characters and generated from a secure random source.
- `APP_ADMIN_PASSWORD` and `DB_PASSWORD` must be strong, unique, and stored in a real secret manager in production.
- `APP_CORS_ALLOWED_ORIGINS` must list the allowed frontend origins exactly, without wildcards in production.
- `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` must point to the real MySQL production instance.
- Leave [.env](.env) local-only; use Azure Key Vault, Vault, or another secret manager in a real deployment.

> The repository includes [prod.env.example](prod.env.example) as a template, but the real values must live only in the local [.env](.env) file.

## Running the project

### Backend in development mode

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
./mvnw.cmd spring-boot:run
```

### Backend in production mode

On Debian Linux, use the bash launcher or a systemd service.

```bash
chmod +x ./mvnw ./start-prod.sh
./start-prod.sh
```

You can also pass a profile explicitly:

```bash
SPRING_PROFILE=prod ./start-prod.sh
```

## Production server mode on Debian

This project is designed to run on a Debian Linux server using a standard systemd service. The following procedure is the recommended setup for production.

### 1. Install Java 21 and required packages

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk-headless curl unzip git mysql-server nginx
```

If you use MySQL locally on the same server, make sure the database is started and the user is configured:

```bash
sudo systemctl enable --now mysql
```

### 2. Create the application directory

```bash
sudo mkdir -p /opt/businesscase
sudo chown -R www-data:www-data /opt/businesscase
```

Then copy the project files into `/opt/businesscase`.

### 3. Create a secure `.env` file

Create `/opt/businesscase/.env` with production values only:

```dotenv
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

APP_ADMIN_USERNAME=prodadmin
APP_ADMIN_PASSWORD=CHANGE_ME_STRONG_ADMIN_PASSWORD
APP_ADMIN_ROLES=ROLE_ADMIN,ROLE_USER

JWT_SECRET=CHANGE_ME_A_STRONG_SECRET_AT_LEAST_32_CHARS
JWT_EXPIRATION_MS=3600000
APP_CORS_ALLOWED_ORIGINS=https://app.example.com

DB_URL=jdbc:mysql://localhost:3306/businesscase?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=20000
DB_USERNAME=businesscase_user
DB_PASSWORD=CHANGE_ME_DB_PASSWORD
DB_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

DDL_AUTO=update
HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
```

Important rules:

- `JWT_SECRET` must be at least 32 characters long
- `APP_ADMIN_PASSWORD` and `DB_PASSWORD` must be strong and secret
- never commit a real `.env` to Git
- prefer a real secret manager such as Vault, Azure Key Vault, or an equivalent solution in production

### 4. Install the systemd service

```bash
sudo cp businesscase.service /etc/systemd/system/businesscase.service
sudo systemctl daemon-reload
sudo systemctl enable businesscase
```

Then start the service:

```bash
sudo systemctl start businesscase
sudo systemctl status businesscase
```

To watch logs:

```bash
sudo journalctl -u businesscase -f
```

### 5. Configure a reverse proxy with nginx

A common Debian production pattern is to put nginx in front of the Spring Boot app.

Example `/etc/nginx/sites-available/businesscase`:

```nginx
server {
    listen 80;
    server_name app.example.com;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

Activate it:

```bash
sudo ln -s /etc/nginx/sites-available/businesscase /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 6. Security hardening for Debian

Recommended production hygiene:

- run the app as a dedicated system user, not as `root`
- keep `MYSQL` and the application on the same private network when possible
- restrict firewall access to ports 22, 80, and 443 only
- use TLS certificates through Let’s Encrypt or a corporate PKI
- keep the JVM and OS packages updated regularly
- store secrets outside the repository, ideally in a secure vault or managed secret store

### 7. Production health check

Once the service is running, verify that the app responds correctly:

```bash
curl -i http://127.0.0.1:8080/actuator/health
```

If `spring-boot-starter-actuator` is not enabled in the project, use a direct API route or check the Spring Boot logs instead.

### 8. Updating the deployment

When you deploy a new version:

```bash
cd /opt/businesscase
git pull
./mvnw clean package
sudo systemctl restart businesscase
```

For a server deployment, a systemd unit is provided at [businesscase.service](businesscase.service). Install it with:

```bash
sudo cp businesscase.service /etc/systemd/system/businesscase.service
sudo systemctl daemon-reload
sudo systemctl enable --now businesscase
```

## Debian production

This section is focused only on the server-side system service setup for Debian. It describes a production-ready configuration using a systemd service, Nginx as a reverse proxy, and TLS termination.

### 1. Service installation details

The application is expected to run in `/opt/businesscase` with the `.env` file located at `/opt/businesscase/.env` and the launcher at `/opt/businesscase/start-prod.sh`.

Example service file:

```ini
[Unit]
Description=Business Case Spring Boot application
After=network.target mysql.service
Wants=network-online.target
After=network-online.target

[Service]
Type=simple
User=www-data
Group=www-data
WorkingDirectory=/opt/businesscase
EnvironmentFile=/opt/businesscase/.env
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=SERVER_PORT=8080
ExecStart=/usr/bin/bash /opt/businesscase/start-prod.sh
Restart=on-failure
RestartSec=10
LimitNOFILE=65536
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ProtectHome=yes
ReadWritePaths=/opt/businesscase

[Install]
WantedBy=multi-user.target
```

Install it with:

```bash
sudo cp businesscase.service /etc/systemd/system/businesscase.service
sudo systemctl daemon-reload
sudo systemctl enable --now businesscase
sudo systemctl status businesscase
```

### 2. Nginx reverse proxy configuration

Create the site configuration:

```bash
sudo nano /etc/nginx/sites-available/businesscase
```

Example file:

```nginx
server {
    listen 80;
    listen [::]:80;
    server_name app.example.com;

    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name app.example.com;

    ssl_certificate /etc/letsencrypt/live/app.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/app.example.com/privkey.pem;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port 443;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

Activate the site:

```bash
sudo ln -s /etc/nginx/sites-available/businesscase /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 3. TLS certificates with Let’s Encrypt

On Debian, install certbot and request a certificate:

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d app.example.com
```

This creates the certificate and updates the nginx configuration automatically.

### 4. Firewall rules

A typical Debian production rule set:

```bash
sudo ufw allow OpenSSH
sudo ufw allow 'Nginx Full'
sudo ufw enable
```

This allows inbound HTTPS and lets SSH remain available.

### 5. Monitoring and logs

Check application health and service state:

```bash
sudo systemctl status businesscase
sudo journalctl -u businesscase -f
sudo nginx -t
```

### 6. Recommended production checklist

- run the app as `www-data` or a dedicated service account
- keep the app and database on the same protected network
- use TLS termination at nginx or a reverse proxy
- keep `JAVA_HOME` and Debian packages up to date
- store secrets in a vault or managed secret store
- do not expose raw Spring Boot port 8080 directly to the internet
- keep a regular backup strategy for MySQL and uploaded data

### Frontend in development mode

```bash
cd businesscase-frontend
npm start
```

The Angular app runs on `http://localhost:4200` and proxies `/api/**` requests to the backend on `http://localhost:8080`.

## Authentication

The backend uses stateless JWT authentication with Spring Security.

### Key components

- `SecurityConfig`: JWT resource server setup and stateless security configuration
- `AuthController`: login endpoint and JWT issuance
- `AppUser`: persisted user entity in JPA
- `AppUserRepository`: repository for user lookup
- `DatabaseUserDetailsService`: converts persisted users into Spring `UserDetails`
- `AppUserInitializer`: ensures the admin user exists at startup

### Authentication flow

1. The user submits credentials to the backend.
2. The backend validates the username and password.
3. The backend issues a JWT signed with `JWT_SECRET`.
4. The frontend stores the token and sends it in the `Authorization: Bearer ...` header.

## Tests

### Backend tests

```bash
./mvnw test
```

### Frontend tests

```bash
cd businesscase-frontend
npm test
```

## Production hardening

The project has been prepared for a safer production setup:

- user identities stored in the database instead of memory
- secrets externalized through environment variables
- stricter `prod` configuration with MySQL JDBC settings
- required `JWT_SECRET` with minimum length validation
- explicit CORS configuration for production origins
- HTTP security headers hardened with HSTS and referrer policy restrictions
- cleaner production logging
- disabled stack traces in production HTTP error responses
- no hardcoded sensitive credentials in default config files
- local environment files protected by Git ignore rules

## Notes and precautions

- H2 is reserved for test execution only.
- In production, `DB_*` variables must be set and MySQL must be reachable.
- The `.env` file must stay local and should not be committed to source control.
- For real deployment, consider using a secret manager such as Azure Key Vault, Vault, or Docker secrets.

## Current project status

The project is adapted for a Debian Linux server environment with:

- a Java 21 / Spring Boot 3.5.x backend compatible with Debian 12/13
- a production-ready environment configuration
- a Linux startup script and systemd service
- a hardened production profile
- an Angular 22 frontend

## Author

Project maintained for the Business Case application.
