# Business Case Spring

Application Spring Boot Java pour le projet `businesscasespring`.

## Description

Ce projet est une application Spring Boot basique écrite en Java 25. Il contient une configuration Maven pour Spring Boot 4.1.0 et des dépendances courantes pour :

- Spring Web
- Spring Data JPA
- Validation Spring
- MySQL Connector
- DevTools pour le développement
- Tests avec Spring Boot Starter Test et H2 en mémoire

L'application démarre à partir de la classe `BusinesscasespringApplication`.

## Prérequis

- Java 25
- Maven (ou utilisation du wrapper `./mvnw`)
- Node.js 18+ et npm (pour le frontend Angular)
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

## Exécution

Pour lancer l'application :

```bash
./mvnw spring-boot:run
```

## Artifact généré

Le build Maven génère le JAR dans :

```text
target/businesscasespring-0.0.1-SNAPSHOT.jar
```

## Tests

Pour exécuter les tests :

```bash
./mvnw test
```

## Configuration

Les propriétés de configuration sont dans `src/main/resources/application.properties`.

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
- La dépendance `com.mysql:mysql-connector-j` est déclarée en runtime pour la connexion MySQL (nouvelles coordonnées Maven officielles depuis MySQL 8.0.31, version gérée par le BOM Spring Boot).
- La base de données H2 est utilisée uniquement pour les tests (voir `src/test/resources/application.properties`).
- La sécurité Spring est activée avec authentification HTTP Basic, utilisateurs en mémoire et en-têtes HTTP renforcés. Le CSRF est désactivé pour permettre les appels API sans session.

## Auteur

Ce README est écrit en français pour décrire l'ensemble du projet et faciliter la prise en main.

## Frontend (Angular)

Un frontend Angular 19 minimal a été ajouté dans le dossier `businesscase-frontend`.

Pour lancer le frontend en mode développement (proxy vers le backend Spring Boot sur `http://localhost:8080` configuré automatiquement via `proxy.conf.json`):

```bash
cd businesscase-frontend
npm install
npm start
```

Le frontend contient:

- un composant `Home` (page d'accueil)
- un composant `Login` pour saisir nom d'utilisateur et mot de passe (stockés en mémoire côté client pour tests)
- un `AuthService` et un `AuthInterceptor` pour joindre l'en-tête Basic Authorization aux requêtes HTTP
- un fichier `proxy.conf.json` pour proxier `/api` vers le backend pendant le développement

Note: pour des usages en production, remplacer l'authentification Basic en mémoire par une solution sécurisée (JWT, OAuth2, stockage persistant des utilisateurs), et déployer le frontend séparément.
