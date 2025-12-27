## 3.1 Logique des Cas d'Utilisation (Use Case Narrative)

Acteurs principaux

- Employé : pointe sa présence, soumet des demandes de congés, consulte son historique.
- Manager : valide/conteste demandes, traite explications, consulte l'assiduité de son équipe.
- RH : gère contrats, déclenche la paie, consulte rapports globaux.

Récits principaux

- Pointage de présence : L'employé effectue un "check-in"; le système calcule le statut et enregistre.
- Demande de congé : L'employé soumet -> Manager avis -> RH validation finale (optionnel).
- Traitement disciplinaire : Retard/absence génère une demande d'explication ; manager répond ; action appliquée.

### Diagramme Use Case (Mermaid)

```mermaid
%%{init: { 'theme':'default' }}%%
actor Employee as E
actor Manager as M
actor HR as H

E -- (Pointage de présence)
E -- (Demande de congé)
E -- (Consulter bulletins et solde)

M -- (Valider/Refuser congé)
M -- (Traiter demande d'explication)

H -- (Gérer contrats)
H -- (Déclencher paie)
H -- (Consulter rapports)

note right of (Déclencher paie)
  Synthèse automatiques : présences, congés, primes, retenues.
end note
```

Chaque cas d'utilisation doit être accompagné d'un scénario happy-path et d'au moins un scénario alternatif (ex : manager absent -> escalade vers RH).
