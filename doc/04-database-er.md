## 3.2 Architecture de la Base de Données (Schéma Relationnel)

Description rapide

La base est organisée en quatre pôles : Core (Employe, Departement), Temps (Presence, Justificatif), Social (SoldeConge, DemandeConge) et Financier (BulletinPaie, Retenue, Prime).

### Diagramme ER (Mermaid)

```mermaid
erDiagram
    EMPLOYE ||--o{ PRESENCE : has
    EMPLOYE ||--o{ DEMANDE_CONGE : files
    EMPLOYE ||--o{ SOLDE_CONGE : has
    EMPLOYE ||--o{ BULLETIN_PAIE : receives
    DEPARTEMENT ||--o{ EMPLOYE : contains

    EMPLOYE {
      string id PK "uuid or serial"
      string matricule
      string prenom
      string nom
      date date_embauche
      string id_manager FK
      string id_departement FK
      boolean actif
    }

    DEPARTEMENT {
      string id PK
      string nom
      string code
    }

    PRESENCE {
      string id PK
      string id_employe FK
      datetime date
      time heure_arrivee
      time heure_depart
      enum status "ON_TIME, LATE, ABSENT"
      string id_justificatif FK
    }

    JUSTIFICATIF {
      string id PK
      string id_presence FK
      string type
      string url_document
      text commentaire
      datetime date_depot
    }

    SOLDE_CONGE {
      string id PK
      string id_employe FK
      enum type_conge "PAID,SICK,UNPAID"
      decimal jours_restants
    }

    DEMANDE_CONGE {
      string id PK
      string id_employe FK
      date debut
      date fin
      enum statut "PENDING, APPROVED, REJECTED"
      string id_manager_avis
      string id_rh_avis
    }

    BULLETIN_PAIE {
      string id PK
      string id_employe FK
      string mois_annee
      decimal salaire_brut
      decimal primes
      decimal retenues
      decimal cotisations
      decimal salaire_net
      text details_json
      datetime date_generation
    }

    PRIME {
      string id PK
      string id_employe FK
      string mois_annee
      decimal montant
      string raison
    }

    RETENUE {
      string id PK
      string id_employe FK
      string mois_annee
      decimal montant
      string raison
    }
```

Remarques

- `BulletinPaie.details_json` contient la photo complète du calcul (déductions par absence, minutes de retard, etc.).
- Les clefs primaires peuvent être des UUIDs ou des séquences selon le SGBD.
