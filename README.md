# Business Case Spring

Application web full-stack développée avec Java / Spring Boot et Angular pour la gestion des utilisateurs, l'authentification et l'administration sécurisée.

## Vue d'ensemble

Ce projet met en place :

- un backend Java 21 avec Spring Boot 4.0.8
- un frontend Angular 22 en composants standalone
- une authentification JWT stateless avec Spring Security
- des utilisateurs persistés en base de données avec rôles et accès administrateur
- un dashboard protégé et une zone d'administration pour gérer les comptes
- un profil de développement local (H2) et un profil production (MySQL + variables d'environnement)
- des protections de sécurité renforcées côté backend et côté frontend

## Stack technique

### Backend

- Java 21
- Spring Boot 4.0.8
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- JWT avec Nimbus JOSE
- H2 pour le développement et les tests
- MySQL pour la production

### Frontend

- Angular 22
- TypeScript
- RxJS
- Standalone components
- JWT conservé uniquement en mémoire côté navigateur
- routes protégées selon les rôles `ROLE_USER` et `ROLE_ADMIN`

## Fonctionnalités implémentées

- connexion utilisateur avec génération d'un JWT
- validation des identifiants via Spring Security
- protection des routes côté backend via `@PreAuthorize` et règles HTTP
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
- blocage temporaire des tentatives de connexion répétées sur `/api/auth/login`

## Prérequis

- Java 21
- Maven Wrapper inclus : `./mvnw` / `./mvnw.cmd`
- Node.js 22+
- npm 10+
- MySQL pour le profil production
- environnement Linux/Debian pour le service système (optionnel)

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

### Backend

```bash
./mvnw spring-boot:run
```

Sous PowerShell :

```powershell
./mvnw.cmd spring-boot:run
```

### Frontend

```bash
cd businesscase-frontend
npm start
```

Le frontend est servi par défaut sur `http://localhost:4200`.

## Configuration et environnement

Le projet utilise des variables d'environnement pour sécuriser la production et garder une configuration locale simple.

### Fichiers de configuration

- [src/main/resources/application.properties](src/main/resources/application.properties)
- [src/main/resources/application-dev.properties](src/main/resources/application-dev.properties)
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

Les points d'entrée de sécurité sont centralisés dans :

- [src/main/java/fr/humanbooster/businesscasespring/SecurityConfig.java](src/main/java/fr/humanbooster/businesscasespring/SecurityConfig.java)
- [src/main/java/fr/humanbooster/businesscasespring/api/AuthController.java](src/main/java/fr/humanbooster/businesscasespring/api/AuthController.java)
- [businesscase-frontend/src/app/auth.service.ts](businesscase-frontend/src/app/auth.service.ts)

Le flux fonctionne ainsi :

1. l'utilisateur se connecte via le backend
2. le backend valide les identifiants et délivre un JWT signé
3. le frontend conserve le token en mémoire uniquement
4. chaque requête passe le token dans le header `Authorization`
5. les contrôles d'accès sont vérifiés côté backend sur les rôles utilisateur

### Sécurités actuellement mises en place

- sessions serveur désactivées pour un mode stateless JWT
- validation du secret JWT au démarrage
- hachage des mots de passe avec BCrypt
- contrôle strict des origines CORS
- protection des routes sensibles via `@PreAuthorize("hasRole('ADMIN')")`
- HSTS, referrer policy et `frameOptions` renforcés
- blocage temporaire des tentatives de connexion répétées sur `/api/auth/login`
- mot de passe soumis à une politique de complexité minimale et à l'interdiction des valeurs trop communes
- JWT non persisté dans `sessionStorage`/`localStorage` pour limiter l'exposition en cas de XSS

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
├── .env                          # variables locales non versionnées (à créer localement)
└── target/                       # artefacts Maven / rapports OWASP (généré)
```

## Vérification et tests

### Backend

```bash
./mvnw -q test
./mvnw -q verify
```

Pour valider le scan OWASP Dependency-Check avec la base NVD et obtenir un état de vulnérabilité fiable, il faut injecter la clé API avant l’exécution :

```powershell
cd "C:\Users\pierr\OneDrive\Documents Pierre-Henry\Electroms\Workspace\Business_Case_New_PH"
$env:NVD_API_KEY="COLLER_VOTRE_CLE_NVD_ICI"
.\mvnw.cmd -DskipTests verify
```

Commande alternative ciblée sur le scan OWASP uniquement :

```powershell
cd "C:\Users\pierr\OneDrive\Documents Pierre-Henry\Electroms\Workspace\Business_Case_New_PH"
$env:NVD_API_KEY="COLLER_VOTRE_CLE_NVD_ICI"
.\mvnw.cmd -DskipTests dependency-check:check
```

> Sans clé NVD valide, le plugin OWASP peut échouer avec `401 Unauthorized` lors de la récupération des rapports de composants, même si le projet compile correctement.

Pour démarrer localement avec le profil `dev` :

```bash
./mvnw -q spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend

```bash
cd businesscase-frontend
npm run build
npm audit
```

Le scan OWASP Maven est exécuté pendant la phase `verify` et peut faire échouer le build si des dépendances dépassent le seuil de criticité configuré. Le frontend est surveillé côté audit npm pour les vulnérabilités connues.

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
- stocker les secrets dans un gestionnaire de secrets ou un service cloud dédié
- utiliser un compte système dédié pour l'exécution de l'application
- maintenir les dépendances à jour et relancer le scan OWASP régulièrement
- surveiller les échecs d'authentification et les actions admin via logs centralisés

## État de sécurité actuel

Le code est dans une configuration de sécurité fonctionnelle pour un projet de type MVP / application interne, avec des protections sérieuses sur :

- l'authentification JWT
- le hachage des mots de passe
- les accès administrateurs
- la validation des secrets et des rôles
- la réduction de la surface d'exposition côté navigateur

Les points à surveiller avant mise en production stricte :

- ajout d'un mécanisme de rate limiting plus robuste si nécessaire
- renforcement de la validation DTO avec annotations Bean Validation
- centralisation complète des logs de sécurité et de l'audit des actions sensibles
- revue régulière des dépendances et du scan de vulnérabilités

## Notes et précautions

- H2 est réservé au développement et aux tests.
- En production, `DB_*` variables doivent être définies et MySQL doit être accessible.
- Le fichier `.env` doit rester local et ne doit pas être commit.
- Pour un déploiement réel, il est recommandé d'utiliser un gestionnaire de secrets tel que Azure Key Vault, Vault ou Docker secrets.
- Le dépôt ne contient ni configuration Apache2, ni service Apache, ni Dockerfile Apache ; toute mise à jour Apache2 correspond à un composant système ou à un autre environnement, hors périmètre de ce projet.

## État du projet

Le projet est prêt pour un environnement serveur Debian Linux avec :

- un backend Java 21 / Spring Boot 4 compatible Debian 12/13
- une configuration de production sécurisée
- des scripts de démarrage Linux/Windows
- un profil `prod` renforcé
- un frontend Angular 22

## Auteur

Projet Business Case – Spring Boot + Angular.
