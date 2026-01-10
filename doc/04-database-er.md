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
    PRESENCE ||--o{ JUSTIFICATIF : has


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
      date date_jour
      time heure_arrivee
      time heure_depart
      string status
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
      string type_conge
      decimal jours_restants
    }

    DEMANDE_CONGE {
      string id PK
      string id_employe FK
      date debut
      date fin
      string type_conge
      string statut
      string id_manager_avis FK
      string id_rh_avis FK
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
