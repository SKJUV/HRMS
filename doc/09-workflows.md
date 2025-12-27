## 9. Synthèse des flux métiers principaux

### Flux Arrivée (Activity)

```mermaid
flowchart TD
  A[Employé pointe] --> B{Système récupère heure standard}
  B --> C[Compare arrivée avec heureStandard]
  C --> D{Retard > marge ?}
  D -- Non --> E[Status = ON_TIME]
  D -- Oui --> F[Status = LATE]
  F --> G[Créer DemandeExplication]
  E --> H[Enregistrer Presence]
  G --> H
  H --> I[Notifie manager si incident]
```

### Flux Fin de Mois (Activity)

```mermaid
flowchart TD
  R[RH déclenche paie] --> P[PayrollService récupère données]
  P --> Q[Calculer retenues pour retards/absences]
  Q --> S[Appliquer primes et cotisations]
  S --> T[Créer BulletinPaie immuable]
  T --> U[Mettre à jour SoldeConge]
  U --> V[Notifier employé et stocker rapport]
```

### Flux Congé (Sequence)

```mermaid
sequenceDiagram
    participant Emp as Employé
    participant Sys as Système
    participant Man as Manager
    participant RH as RH

    Emp->>Sys: Soumet DemandeConge
    Sys->>Man: Notifie validation
    Man-->>Sys: Approuve/Rejette
    alt Approuvé
      Sys->>RH: Notification finale
      RH-->>Sys: Validation RH
    else Rejeté
      Sys-->>Emp: Notifier rejet
    end
```
