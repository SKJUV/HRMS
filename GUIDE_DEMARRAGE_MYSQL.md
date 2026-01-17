# Guide de Démarrage - HRMS avec MySQL (XAMPP)

## 📋 Configuration Initiale

### 1. Installer les Prérequis

**Java JDK 17 ou supérieur**
```bash
java -version
```

**XAMPP avec MySQL**
- Télécharger et installer XAMPP depuis https://www.apachefriends.org/
- Démarrer le module MySQL depuis le panneau de contrôle XAMPP

**Maven**
```bash
mvn -version
```

### 2. Créer la Base de Données MySQL

#### Option 1 : Via phpMyAdmin
1. Ouvrir http://localhost/phpmyadmin
2. Cliquer sur "Nouvelle base de données"
3. Nom: `hrms`
4. Interclassement: `utf8mb4_unicode_ci`
5. Aller dans l'onglet "SQL"
6. Copier-coller le contenu de `db/schema_mysql.sql` et exécuter
7. Copier-coller le contenu de `db/users_table_mysql.sql` et exécuter

#### Option 2 : Via ligne de commande
```bash
# Ouvrir le terminal MySQL (depuis XAMPP/mysql/bin)
mysql -u root -p
# (Laisser vide si pas de mot de passe)

# Exécuter les commandes :
CREATE DATABASE hrms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hrms;
SOURCE C:/Users/DELL/Desktop/HRMS-1/db/schema_mysql.sql;
SOURCE C:/Users/DELL/Desktop/HRMS-1/db/users_table_mysql.sql;

# Vérifier que tout est OK
SHOW TABLES;
SELECT * FROM users;
```

### 3. Configurer l'Application

Le fichier `src/main/resources/database.properties` est déjà configuré pour XAMPP :
```properties
db.url=jdbc:mysql://localhost:3306/hrms?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=
```

**Note** : Si vous avez défini un mot de passe pour MySQL dans XAMPP, modifiez la ligne `db.password=`

### 4. Compiler et Lancer

```bash
# Se positionner dans le dossier du projet
cd C:\Users\DELL\Desktop\HRMS-1

# Nettoyer et compiler (Maven téléchargera automatiquement le driver MySQL)
mvn clean compile

# Lancer l'application
mvn javafx:run
```

## 🔐 Test de l'Application

### Scénario 1 : Connexion Admin
1. Lancer l'application avec `mvn javafx:run`
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

### Erreur : "Communications link failure"
- Vérifier que MySQL est démarré dans XAMPP
- Vérifier le port (par défaut 3306)
- Tester : `mysql -u root -p -h localhost`

### Erreur : "Access denied for user 'root'"
- Vérifier le mot de passe dans `database.properties`
- Dans XAMPP, MySQL n'a généralement pas de mot de passe par défaut

### Erreur : "Driver MySQL non trouvé"
```bash
mvn clean install
```

### Erreur : "Unknown database 'hrms'"
- Créer la base de données :
```sql
CREATE DATABASE hrms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Erreur : "Table 'users' doesn't exist"
- Exécuter le script : `SOURCE db/users_table_mysql.sql;`

### Port MySQL déjà utilisé
- Dans XAMPP, vérifier que le port MySQL est 3306
- Modifier `database.properties` si nécessaire

## 📦 Structure des Fichiers Créés

```
✅ src/main/java/com/hrms/
    ✅ Main.java
    ✅ model/User.java
    ✅ model/Role.java
    ✅ dao/UserDAO.java (adapté pour MySQL)
    ✅ service/AuthenticationService.java
    ✅ controller/LoginController.java
    ✅ controller/DashboardController.java
    ✅ controller/UserManagementController.java
    ✅ util/DatabaseConnection.java (driver MySQL)

✅ src/main/resources/
    ✅ com/hrms/view/login.fxml
    ✅ com/hrms/view/dashboard.fxml
    ✅ com/hrms/view/user-management.fxml
    ✅ com/hrms/css/style.css
    ✅ database.properties (MySQL)

✅ db/
    ✅ schema_mysql.sql (MySQL)
    ✅ users_table_mysql.sql (MySQL)

✅ pom.xml (avec driver MySQL)
✅ module-info.java
```

## 🔍 Vérification de la Configuration

### Tester la connexion MySQL
```bash
# Dans le dossier XAMPP/mysql/bin
mysql -u root -p
USE hrms;
SHOW TABLES;
SELECT * FROM users;
```

### Tester l'application
```bash
mvn clean compile
mvn javafx:run
```

## ✅ Différences MySQL vs PostgreSQL

| Aspect | PostgreSQL | MySQL |
|--------|-----------|--------|
| **UUID** | Type natif `UUID` | `CHAR(36)` + `UUID()` |
| **ENUM** | Type personnalisé | Type natif `ENUM()` |
| **Auto-increment** | `SERIAL` | `AUTO_INCREMENT` |
| **Driver JDBC** | `postgresql` | `mysql-connector-j` |
| **Port** | 5432 | 3306 |
| **Timestamp** | `now()` | `NOW()` |
| **Cast** | `::type` | Conversion implicite |

## ✅ Checklist Semaine 2 (MySQL)

- [x] Structure du projet créée
- [x] Modèle User avec rôles
- [x] Connexion JDBC MySQL fonctionnelle
- [x] UserDAO adapté pour MySQL
- [x] Service d'authentification
- [x] Interface de connexion
- [x] Tableau de bord avec menu
- [x] Gestion des utilisateurs
- [x] Restrictions par rôle
- [x] Schéma MySQL créé
- [x] Table users MySQL créée
- [x] Driver MySQL configuré

## 🎉 Félicitations !

Vous avez terminé la **Semaine 2** du projet HRMS avec **MySQL (XAMPP)** !

Les objectifs suivants ont été atteints :
- ✅ Authentification fonctionnelle
- ✅ Gestion des rôles (ADMIN, MANAGER, EMPLOYEE)
- ✅ Interface d'administration des utilisateurs
- ✅ CRUD complet sur les utilisateurs
- ✅ Vérification des identifiants via JDBC MySQL

**Configuration MySQL** :
- ✅ Schémas adaptés pour MySQL
- ✅ Driver JDBC MySQL configuré
- ✅ Compatible avec XAMPP

Prochaine étape : **Semaine 3 - Gestion des employés et départements**
