# Business Case Spring

Application Spring Boot Java pour le projet `businesscasespring`.

## Description

Ce projet est une application Spring Boot basique écrite en Java 21. Il contient une configuration Maven pour Spring Boot 3.2.6 et des dépendances courantes pour :

- Spring Web
- Spring Data JPA
- Validation Spring
- MySQL Connector
- DevTools pour le développement
- Tests avec Spring Boot Starter Test et H2 en mémoire

L'application démarre à partir de la classe `BusinesscasespringApplication`.

## Prérequis

- Java 21
- Maven (ou utilisation du wrapper `./mvnw`)
- MySQL si vous souhaitez exécuter l'application avec une base de données réelle

## Installation

1. Cloner le dépôt :

```bash
git clone git@github.com:electroms/Business_Case_New_PH.git
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

## Notes

- Le projet est configuré pour Java 21 via `maven.compiler.release`.
- La dépendance `mysql-connector-java` est déclarée en runtime pour la connexion MySQL.
- La base de données H2 est utilisée uniquement pour les tests.
- La sécurité Spring est activée avec authentification HTTP Basic, utilisateurs en mémoire et en-têtes HTTP renforcés.
- Le mot de passe de l’utilisateur administrateur peut être configuré via la variable d’environnement `SECURITY_USER_PASSWORD`.

## Auteur

- `fr.humanbooster.businesscasespring`

---

Ce README est écrit en français pour décrire l'ensemble du projet et faciliter la prise en main.