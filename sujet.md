# PROJET JAVA – APPLICATION DE GESTION DES RESSOURCES HUMAINES (HRMS)

## 1. Contexte général

Ce projet consiste à développer une **application desktop de gestion des ressources humaines (HRMS)** en **Java natif**, utilisant **JavaFX** pour l’interface graphique et **MySQL** pour la base de données.

L’objectif est de mettre en pratique :
- la programmation orientée objet (POO),
- l’architecture logicielle MVC,
- la connexion à une base de données via JDBC,
- la conception d’interfaces graphiques avec JavaFX.

Le projet est réalisé de manière **incrémentale sur 7 semaines**.

---

## 2. Technologies utilisées

- Java SE  
- JavaFX (FXML recommandé)
- JDBC
- MySQL
- Scene Builder
- IDE : IntelliJ IDEA / Eclipse / NetBeans

---

## 3. Architecture logicielle

Architecture recommandée (MVC étendu) :












---

## 4. Développement incrémental

### Semaine 1 : Analyse et mise en place du projet

#### Objectif pédagogique
Comprendre l’architecture MVC et configurer un projet JavaFX.

#### Travaux à réaliser
- Analyse du système RH
- Identification des classes principales :
  - User
  - Employee
  - Department
  - Attendance
  - Leave
  - Salary
  - ExplanationRequest
- Création de la base de données MySQL
- Mise en place de la connexion JDBC
- Création du projet JavaFX
- Test de connexion à la base de données

#### Livrables
- Script SQL
- Projet JavaFX initial
- Diagramme de classes UML
- Test JDBC fonctionnel

---

### Semaine 2 : Authentification et gestion des utilisateurs

#### Objectif pédagogique
Implémenter la sécurité de base en Java.

#### Travaux à réaliser
- Interface de connexion JavaFX
- Gestion des utilisateurs :
  - login
  - mot de passe
  - rôle (ADMIN, MANAGER, EMPLOYEE)
- Vérification des identifiants via JDBC
- Restriction des fonctionnalités selon le rôle
- CRUD des utilisateurs

#### Livrables
- Authentification fonctionnelle
- Gestion des rôles
- Interface d’administration des utilisateurs

---

### Semaine 3 : Gestion des employés et départements

#### Objectif pédagogique
Maîtriser les relations entre objets Java et la persistance.

#### Travaux à réaliser
- CRUD des employés
- CRUD des départements
- Association Employé → Département
- Recherche par nom ou matricule
- Affichage avec TableView

#### Livrables
- Interfaces CRUD JavaFX
- Données persistées en base
- Relations fonctionnelles

---

### Semaine 4 : Gestion des présences

#### Objectif pédagogique
Appliquer la logique métier en Java.

#### Travaux à réaliser
- Enregistrement des heures d’entrée et de sortie
- Calcul automatique des heures travaillées
- Validation ou correction par le manager
- Historique des présences par employé

#### Livrables
- Interface de pointage
- Calcul automatique des heures
- Validation par le manager

---

### Semaine 5 : Gestion des congés

#### Objectif pédagogique
Mettre en place un workflow métier.

#### Travaux à réaliser
- Demande de congé via interface JavaFX
- Validation ou rejet par le manager
- Historique des congés
- Vérification des chevauchements de dates

#### Livrables
- Module de gestion des congés
- Workflow fonctionnel
- Historique consultable

---

### Semaine 6 : Salaires et demandes d’explication

#### Objectif pédagogique
Automatiser les traitements RH.

#### Travaux à réaliser
- Calcul du salaire mensuel selon :
  - heures travaillées
  - absences
  - congés
  - primes
- Gestion des demandes d’explication :
  - absences
  - retards
  - paie
- Traitement des demandes par le manager

#### Livrables
- Module de calcul des salaires
- Interface de gestion des demandes RH
- Données cohérentes en base

---

### Semaine 7 : Statistiques et finalisation

#### Objectif pédagogique
Finaliser une application Java professionnelle.

#### Travaux à réaliser
- Génération de statistiques :
  - présences
  - congés
  - salaires
- Tableaux et graphiques (TableView, Chart)
- Tableau de bord Administrateur / Manager
- Nettoyage du code
- Documentation complète

#### Livrables
- Dashboard fonctionnel
- Application stable
- Rapport de projet (PDF)
- Présentation et démonstration finale

---

## 5. Contraintes techniques

- Java natif obligatoire
- JavaFX obligatoire pour l’interface
- JDBC obligatoire (pas d’ORM)
- Architecture MVC respectée
- Code clair, commenté et structuré

---

## 6. Résultat attendu

Une application desktop JavaFX complète permettant :
- la gestion des employés,
- le suivi des présences et congés,
- le calcul des salaires,
- la gestion RH,
- l’affichage de statistiques.

---

**Fin du document**
