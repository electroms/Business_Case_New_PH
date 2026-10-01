# Business Case Spring

Application web full-stack développée avec Spring Boot et Angular pour la gestion d'utilisateurs, d'authentification et d'administration sécurisée.

## Vue d'ensemble

Ce projet met en place :

- un backend Java 21 avec Spring Boot 3.5.x
- un frontend Angular 22 en composants standalone
- une authentification JWT stateless avec Spring Security
- des utilisateurs persistés en base de données avec rôles et activation
- un dashboard protégé et une zone d'administration pour gérer les comptes
- un profil de développement local (H2) et un profil production (MySQL + variables d'environnement)
- des protections de sécurité renforcées côté backend : validation des secrets JWT, hachage BCrypt, règles d'accès strictes, politique de mots de passe plus forte et blocage des tentatives de connexion répétées

## Stack technique

### Backend (stack)

- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- JWT avec Nimbus JOSE
- H2 pour les tests et le développement
- MySQL pour la production

### Lancer le frontend

- Angular 22
- TypeScript
- RxJS
- Standalone components
- JWT stocké dans le navigateur
- routes protégées selon les rôles `ROLE_USER` et `ROLE_ADMIN`

## Fonctionnalités implémentées

- connexion utilisateur avec génération de JWT
- validation des identifiants via Spring Security
- protection des routes côté backend via politique HTTP et annotations `@PreAuthorize`
- dashboard connecté avec informations utilisateur et statut admin
- écran d'administration pour :
  - lister les utilisateurs
  - créer un utilisateur
  - supprimer un utilisateur
- validation métier côté backend pour empêcher :
  - l'auto-suppression d'un compte
  - la suppression du dernier administrateur
  - la création d'un compte déjà existant
  - l'ajout de rôles invalides
  - l'usage de mots de passe trop faibles ou trop communs
- protection contre les attaques de force brute sur le point d'entrée `/api/auth/login`

## Prérequis

- Java 21
- Maven Wrapper inclus : `./mvnw` / `./mvnw.cmd`
- Node.js 22+
- npm 10+
- MySQL pour le profil production
- optionnel : environnement Linux/Debian pour le service système

## Installation locale

### 1. Cloner le dépôt

```bash
git clone https://github.com/electroms/Business_Case_New_PH.git
cd Business_Case_New_PH
```

### 2. Installer le backend

```bash
./mvnw clean install
```

### 3. Installer le frontend

```bash
cd businesscase-frontend
npm install
```

## Démarrage

### Lancer le backend

```bash
./mvnw spring-boot:run
```

Sous PowerShell :

```powershell
./mvnw.cmd spring-boot:run
```

### Frontend (démarrage)

```bash
cd businesscase-frontend
npm start
```

Le frontend est servi par défaut sur `http://localhost:4200`.

## Configuration et environnement

Le projet utilise des variables d'environnement pour sécuriser la production et garder une configuration locale simple.

### Fichiers de configuration

- [src/main/resources/application.properties](src/main/resources/application.properties)
- [src/main/resources/application-prod.properties](src/main/resources/application-prod.properties)
- [prod.env.example](prod.env.example)
- [start-prod.sh](start-prod.sh)
- [start-prod.ps1](start-prod.ps1)
- [businesscase.service](businesscase.service)

### Exemple de configuration production

```dotenv
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

APP_ADMIN_USERNAME=prodadmin
APP_ADMIN_PASSWORD=CHANGE_ME_STRONG_ADMIN_PASSWORD
APP_ADMIN_ROLES=ROLE_ADMIN,ROLE_USER

JWT_SECRET=CHANGE_ME_A_STRONG_SECRET_AT_LEAST_32_CHARS
JWT_EXPIRATION_MS=3600000
APP_CORS_ALLOWED_ORIGINS=http://localhost:4200

DB_URL=jdbc:mysql://localhost:3306/businesscase?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=20000
DB_USERNAME=businesscase_user
DB_PASSWORD=CHANGE_ME_DB_PASSWORD
DB_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

DDL_AUTO=update
HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
```

> Les secrets réels doivent rester hors dépôt, par exemple dans un fichier `.env` local ou dans un gestionnaire de secrets.

## Authentification et sécurité

La configuration de sécurité est centralisée dans :

- [src/main/java/fr/humanbooster/businesscasespring/SecurityConfig.java](src/main/java/fr/humanbooster/businesscasespring/SecurityConfig.java)
- [src/main/java/fr/humanbooster/businesscasespring/api/AuthController.java](src/main/java/fr/humanbooster/businesscasespring/api/AuthController.java)

Le flux fonctionne ainsi :

1. l'utilisateur se connecte via le backend
2. le backend valide les identifiants et délivre un JWT signé
3. le frontend enregistre le token
4. chaque requête passe le token dans le header `Authorization`
5. les contrôles d'accès sont vérifiés sur les rôles côté backend

### Sécurités actuellement mises en place

- sessions serveur désactivées pour un mode stateless JWT
- validation de la clé JWT au démarrage
- hachage des mots de passe avec BCrypt
- contrôle strict des origines CORS
- protection des routes sensibles via `@PreAuthorize("hasRole('ADMIN')")`
- HSTS, referrer policy et `frameOptions` réduits à un comportement plus sûr
- blocage temporaire des tentatives de connexion répétées sur `/api/auth/login`
- mots de passe exigent un niveau minimum de complexité et refusent les valeurs trop communes

## Administration des utilisateurs

Les points d'entrée de gestion des comptes sont définis dans :

- [src/main/java/fr/humanbooster/businesscasespring/api/UserController.java](src/main/java/fr/humanbooster/businesscasespring/api/UserController.java)
- [src/main/java/fr/humanbooster/businesscasespring/user/UserManagementService.java](src/main/java/fr/humanbooster/businesscasespring/user/UserManagementService.java)

L'API permet :

- la liste des utilisateurs pour les admins
- la création d'un compte depuis un formulaire admin
- la suppression d'un utilisateur avec validation métier
- la protection du dernier compte administrateur et de l'auto-suppression

## Structure du projet

```text
Business_Case_New_PH/
├── businesscase-frontend/         # application Angular
├── src/main/java/                 # code backend Java / Spring
├── src/main/resources/            # configuration Spring et ressources
├── src/test/java/                 # tests backend
├── mvnw / mvnw.cmd                # wrapper Maven
├── pom.xml                       # configuration Maven
├── README.md                     # documentation du projet
├── prod.env.example              # variables d'environnement de production
├── start-prod.sh                 # démarrage production Linux
├── start-prod.ps1                # démarrage production Windows
├── businesscase.service          # service systemd Linux
├── .gitignore                    # règles de versionnement
└── .env                          # variables locales non versionnées (à créer localement)
```

## Vérification et tests

### Backend

```bash
./mvnw -q test
```

### Frontend (build)

```bash
cd businesscase-frontend
npm run build
```

## Déploiement production

Le projet est prêt pour un déploiement Linux/Debian avec :

- service système `systemd`
- variables d'environnement sécurisées
- profil Spring `prod`
- MySQL comme base de données principale
- reverse proxy Nginx optionnel

Les scripts de lancement sont disponibles dans :

- [start-prod.sh](start-prod.sh)
- [start-prod.ps1](start-prod.ps1)
- [businesscase.service](businesscase.service)

## Bonnes pratiques de production

- ne jamais versionner les secrets réels
- sécuriser la clé JWT en environnement réel
- vérifier les règles CORS avant mise en production
- conserver au moins un administrateur actif dans la base
- stocker les secrets dans un gestionnaire de secrets ou dans un service cloud dédié
- utiliser un compte système dédié pour l'exécution de l'application
- maintenir les dépendances à jour et relancer le scan OWASP régulièrement
- surveiller les échecs d'authentification et les actions admin via logs centralisés

## État de sécurité actuel

Le code est dans une configuration de sécurité fonctionnelle pour un projet de type MVP / application interne, avec des protections sérieuses sur :

- l'authentification JWT
- le hachage des mots de passe
- les accès administrateurs
- la validation des secrets et des rôles

Les points à surveiller avant mise en production stricte :

- ajout d'un mécanisme de rate limiting plus robuste si nécessaire
- renforcement de la validation DTO avec annotations Bean Validation
- centralisation complète des logs de sécurité et de l'audit des actions sensibles
- revue régulière des dépendances et du scan de vulnérabilités

## Auteur

Projet Business Case – Spring Boot + Angular.

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
