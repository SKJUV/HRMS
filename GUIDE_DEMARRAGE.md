# Guide de Démarrage - HRMS Semaine 2

## 📋 Configuration Initiale

### 1. Installer les Prérequis

**Java JDK 17 ou supérieur**
```bash
java -version
```

**PostgreSQL**
```bash
psql --version
```

**Maven**
```bash
mvn -version
```

### 2. Créer la Base de Données

Ouvrir un terminal PostgreSQL :
```bash
psql -U postgres
```

Exécuter les commandes :
```sql
-- Créer la base de données
CREATE DATABASE hrms;

-- Se connecter à la base
\c hrms

-- Exécuter le schéma principal
\i 'C:/Users/DELL/Desktop/HRMS-1/db/schema.sql'

-- Exécuter le script users
\i 'C:/Users/DELL/Desktop/HRMS-1/db/users_table.sql'

-- Vérifier que tout est OK
\dt
SELECT * FROM users;
```

### 3. Configurer l'Application

Vérifier le fichier `src/main/resources/database.properties` :
```properties
db.url=jdbc:postgresql://localhost:5432/hrms
db.username=postgres
db.password=VOTRE_MOT_DE_PASSE
```

### 4. Compiler et Lancer

```bash
# Se positionner dans le dossier du projet
cd C:\Users\DELL\Desktop\HRMS-1

# Nettoyer et compiler
mvn clean compile

# Lancer l'application
mvn javafx:run
```

## 🔐 Test de l'Application

### Scénario 1 : Connexion Admin
1. Lancer l'application
2. Username: `admin`
3. Password: `admin`
4. Cliquer sur "Se connecter"
5. Vérifier l'accès au tableau de bord

### Scénario 2 : Gestion des Utilisateurs
1. Se connecter en tant qu'admin
2. Cliquer sur "👤 Utilisateurs" dans le menu
3. Cliquer sur "➕ Nouvel Utilisateur"
4. Créer un utilisateur :
   - Username: `test`
   - Password: `test123`
   - Rôle: EMPLOYEE
5. Vérifier qu'il apparaît dans le tableau

### Scénario 3 : Test des Rôles
1. Se déconnecter
2. Se connecter avec `manager` / `manager`
3. Vérifier que le bouton "Utilisateurs" est désactivé
4. Se déconnecter
5. Se connecter avec `employee` / `employee`
6. Vérifier que seuls certains menus sont accessibles

## 🐛 Résolution des Problèmes

### Erreur : "Driver PostgreSQL non trouvé"
```bash
mvn clean install
```

### Erreur : "Connexion refusée"
- Vérifier que PostgreSQL est démarré
- Vérifier les identifiants dans `database.properties`
- Tester la connexion : `psql -U postgres -d hrms`

### Erreur : "Module javafx not found"
- Vérifier que JavaFX est dans le pom.xml
- Utiliser `mvn javafx:run` au lieu de `java -jar`

### Erreur : "Table users does not exist"
- Exécuter le script : `\i db/users_table.sql`

## 📦 Structure des Fichiers Créés

```
✅ src/main/java/com/hrms/
    ✅ Main.java
    ✅ model/User.java
    ✅ model/Role.java
    ✅ dao/UserDAO.java
    ✅ service/AuthenticationService.java
    ✅ controller/LoginController.java
    ✅ controller/DashboardController.java
    ✅ controller/UserManagementController.java
    ✅ util/DatabaseConnection.java

✅ src/main/resources/
    ✅ com/hrms/view/login.fxml
    ✅ com/hrms/view/dashboard.fxml
    ✅ com/hrms/view/user-management.fxml
    ✅ com/hrms/css/style.css
    ✅ database.properties

✅ db/users_table.sql
✅ pom.xml
✅ module-info.java
```

## ✅ Checklist Semaine 2

- [x] Structure du projet créée
- [x] Modèle User avec rôles
- [x] Connexion JDBC fonctionnelle
- [x] UserDAO (CRUD complet)
- [x] Service d'authentification
- [x] Interface de connexion
- [x] Tableau de bord avec menu
- [x] Gestion des utilisateurs
- [x] Restrictions par rôle
- [x] Table users en base de données

## 🎉 Félicitations !

Vous avez terminé la **Semaine 2** du projet HRMS !

Les objectifs suivants ont été atteints :
- ✅ Authentification fonctionnelle
- ✅ Gestion des rôles (ADMIN, MANAGER, EMPLOYEE)
- ✅ Interface d'administration des utilisateurs
- ✅ CRUD complet sur les utilisateurs
- ✅ Vérification des identifiants via JDBC

Prochaine étape : **Semaine 3 - Gestion des employés et départements**
