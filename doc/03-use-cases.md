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
flowchart LR
	%% acteurs
	E([Employé])
	M([Manager])
	H([RH])

	%% cas d'utilisation
	UC1[Pointage de présence]
	UC2[Demande de congé]
	UC3[Consulter bulletins et solde]
	UC4[Valider/Refuser congé]
	UC5[Traiter demande d'explication]
	UC6[Gérer contrats]
	UC7[Déclencher paie]
	UC8[Consulter rapports]

	%% relations
	E --> UC1
	E --> UC2
	E --> UC3
	M --> UC4
	M --> UC5
	H --> UC6
	H --> UC7
	H --> UC8

	%% note (lié visuellement à la paie)
	note_paie["Synthèse automatique : présences, congés, primes, retenues."]
	UC7 --> note_paie

	classDef actorStyle fill:#f3f4f6,stroke:#333,stroke-width:1px;
	class E,M,H actorStyle;
```

Chaque cas d'utilisation doit être accompagné d'un scénario happy-path et d'au moins un scénario alternatif (ex : manager absent -> escalade vers RH).
