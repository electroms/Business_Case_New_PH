# Business Case Spring

A full-stack Spring Boot + Angular application for the Business Case project.

## Overview

This repository contains:

- a Java 25 backend built with Spring Boot 4.1.0
- an Angular 22 frontend
- JWT-based authentication using Spring Security and OAuth2 Resource Server
- a persistent user identity stored in MySQL instead of in-memory storage
- a strict production profile with required environment variables

## Tech stack

### Backend

- Java 25
- Spring Boot 4.1.0
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

- Java 25
- Maven Wrapper included: `./mvnw` or `./mvnw.cmd`
- Node.js 22+
- npm 10+
- MySQL for the production profile

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/electroms/Business_Case_New_PH.git
cd Business_Case_New_PH
```

### 2. Install backend dependencies

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
- [start-prod.ps1](start-prod.ps1)

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

The PowerShell launcher loads values from `.env` and starts the app with the `prod` profile:

```powershell
./start-prod.ps1
```

You can also pass a profile explicitly:

```powershell
./start-prod.ps1 -SpringProfile prod
```

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

The project is in a working state with:

- modern JWT security
- production-ready environment configuration
- a production startup script
- a Java 25 / Spring Boot 4.1.0 backend
- an Angular 22 frontend

## Author

Project maintained for the Business Case application.
