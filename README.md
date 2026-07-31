# Business Case Spring

Application full-stack Spring Boot + Angular pour le projet `businesscasespring`.

## Description

Ce projet est une application full-stack composée :

- d'un **backend** Spring Boot 4.1.0 écrit en Java 25
- d'un **frontend** Angular 22 avec authentification HTTP Basic et proxy vers le backend

Le backend expose une API REST sécurisée (Spring Security, HTTP Basic). Le frontend Angular communique avec ce backend via un proxy de développement.

### Backend — dépendances principales

- Spring Web
- Spring Data JPA
- Validation Spring
- MySQL Connector
- DevTools pour le développement
- Tests avec Spring Boot Starter Test et H2 en mémoire

### Frontend — stack technique

- Angular 22.1.0 (standalone components, `provideRouter`)
- TypeScript 6.0.3
- RxJS 7.8.x
- Zone.js 0.16.x
- Tests unitaires avec **Vitest** (via `@angular/build:unit-test`)

## Prérequis

- Java 25
- Maven (ou utilisation du wrapper `./mvnw`)
- Node.js 22+ et npm 10+
- MySQL si vous souhaitez exécuter l'application avec une base de données réelle

## Installation

1. Cloner le dépôt :

   ```bash
   git clone https://github.com/electroms/Business_Case_New_PH.git
   cd Business_Case_New_PH
   ```

2. Construire le projet avec Maven :

   ```bash
   ./mvnw clean package
   ```

3. Installer les dépendances frontend :

   ```bash
   cd businesscase-frontend
   npm install
   ```

## Exécution

### Backend

```bash
./mvnw spring-boot:run
```

### Frontend

```bash
cd businesscase-frontend
npm start
```

Le frontend démarre sur `http://localhost:4200` et proxifie automatiquement les appels `/api/**` vers le backend Spring Boot sur `http://localhost:8080` (via `proxy.conf.json`).

## Artifact généré

Le build Maven génère le JAR dans :

```text
target/businesscasespring-0.0.1-SNAPSHOT.jar
```

## Tests

### Tests backend

```bash
./mvnw test
```

### Tests frontend (Vitest)

```bash
cd businesscase-frontend
npm test
```

Les tests frontend utilisent `@angular/build:unit-test` avec **Vitest** et l'environnement DOM **happy-dom**.

## Configuration

Les propriétés de configuration backend sont dans `src/main/resources/application.properties`.

Pour une exécution avec MySQL, ajouter les propriétés suivantes (à ne pas committer avec des identifiants réels) :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/businesscase?useSSL=false&serverTimezone=UTC
spring.datasource.username=<db_user>
spring.datasource.password=<db_password>
spring.jpa.hibernate.ddl-auto=update
```

Les identifiants par défaut de l'API sont `admin` / `ChangeMe123!` (modifiable via la variable d'environnement `SECURITY_USER_PASSWORD`).

## Notes

- Le projet est configuré pour Java 25 via la propriété `java.version` dans le parent Spring Boot.
- La dépendance `com.mysql:mysql-connector-j` est déclarée en runtime (coordonnées Maven officielles depuis MySQL 8.0.31, version gérée par le BOM Spring Boot).
- La base de données H2 est utilisée uniquement pour les tests (voir `src/test/resources/application.properties`).
- La sécurité Spring est activée avec authentification HTTP Basic, utilisateurs en mémoire et en-têtes HTTP renforcés. Le CSRF est désactivé pour permettre les appels API sans session.
- Le frontend Angular utilise le builder `@angular/build:unit-test` avec Vitest (runner par défaut d'Angular 22) en remplacement de Karma (déprécié).

## Auteur

Ce README est écrit en français pour décrire l'ensemble du projet et faciliter la prise en main.

Le frontend contient:

- un composant `Home` (page d'accueil)
- un composant `Login` pour saisir nom d'utilisateur et mot de passe (stockés en mémoire côté client pour tests)
- un `AuthService` et un `AuthInterceptor` pour joindre l'en-tête Basic Authorization aux requêtes HTTP
- un fichier `proxy.conf.json` pour proxier `/api` vers le backend pendant le développement

Note: pour des usages en production, remplacer l'authentification Basic en mémoire par une solution sécurisée (JWT, OAuth2, stockage persistant des utilisateurs), et déployer le frontend séparément.
