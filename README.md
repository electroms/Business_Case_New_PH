# Business Case Spring

Application Spring Boot Java pour le projet `businesscasespring`.

## Description

Ce projet est une application Spring Boot basique écrite en Java 25. Il contient une configuration Maven pour Spring Boot 3.5.15 et des dépendances courantes pour :

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
- MySQL si vous souhaitez exécuter l'application avec une base de données réelle

## Installation

1. Cloner le dépôt :

```bash
git clone https://github.com/electroms/Business_Case_New_PH.git
cd Business_Case_New_PH
```

1. Construire le projet avec Maven :

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

## Notes

- Le projet est configuré pour Java 25 via `maven.compiler.release`.
- La dépendance `com.mysql:mysql-connector-j` est déclarée en runtime pour la connexion MySQL (nouvelles coordonnées Maven officielles depuis MySQL 8.0.31, version gérée par le BOM Spring Boot).
- La base de données H2 est utilisée uniquement pour les tests.
- La sécurité Spring est activée avec authentification HTTP Basic, utilisateurs en mémoire et en-têtes HTTP renforcés.
- Le mot de passe de l’utilisateur administrateur peut être configuré via la variable d’environnement `SECURITY_USER_PASSWORD`.

## Auteur

Ce README est écrit en français pour décrire l'ensemble du projet et faciliter la prise en main.

## Frontend (Angular)

Un frontend Angular 19 minimal a été ajouté dans le dossier `businesscase-frontend`.

Pour lancer le frontend en mode développement (proxy vers le backend Spring Boot sur `http://localhost:8080`):

```bash
cd businesscase-frontend
npm install
npm start -- --proxy-config proxy.conf.json
```

Le frontend contient:

- un composant `Home` (page d'accueil)
- un composant `Login` pour saisir nom d'utilisateur et mot de passe (stockés en mémoire côté client pour tests)
- un `AuthService` et un `AuthInterceptor` pour joindre l'en-tête Basic Authorization aux requêtes HTTP
- un fichier `proxy.conf.json` pour proxier `/api` vers le backend pendant le développement

Note: pour des usages en production, remplacer l'authentification Basic en mémoire par une solution sécurisée (JWT, OAuth2, stockage persistant des utilisateurs), et déployer le frontend séparément.
