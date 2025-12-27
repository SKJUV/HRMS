## 2. Recueil exhaustif des besoins (Business Requirements)

2.1 Gestion de la structure organisationnelle

- Identité Unique : génération d'un matricule formaté `EMP-<YYYY>-<NNN>` lors de la création d'un employé.
- Hiérarchie Dynamique : chaque employé a un éventuel `id_manager` pointant vers un autre employé. Un manager peut avoir des subordonnés.
- Départements : entité `Departement` catégorisant les postes.

2.2 Intelligence de la Gestion du Temps (Présence)

- Politique d'horaires : paramètre par contrat `heure_arrivee_contractuelle` et `heure_depart_contractuelle`.
- Moteur de tolérance : paramètre `marge_tolerance_minutes` qui décide du statut (ON_TIME, LATE).
- Détection d'absence : job planifié (cron) à `heure_detection_absence` qui marque `ABSENT` si pas de pointage avant l'heure.

2.3 Cycle de Vie des Congés

- Acquisition automatique : règle d'acquisition (ex. 2.5 jours/mois) appliquée mensuellement au `SoldeConge` par employé et par type.
- Validation à deux niveaux (optionnelle) : flux Emplaoyé -> Manager -> RH.
- Types : PAID (congés payés), SICK (congé maladie, peut demander justificatif), UNPAID.

2.4 Logique disciplinaire et explications

- Génération automatique d'une `DemandeExplication` pour tout retard non-justifié ou absence.
- Traitement : manager peut accepter/refuser ; décision enregistrée et déclenchement soit d'une régularisation soit d'une retenue sur salaire.

2.5 Moteur de Paie

- Intégration complète : paie calculée automatiquement à partir des données contractuelles, présences et congés.
- Calcul : SalaireBase + Primes - Retenues - Cotisations.
- Historique immuable : un `BulletinPaie` stocke le détail calculé pour un mois donné.

Contraintes non-fonctionnelles

- Traçabilité et immutabilité des paies.
- Performance raisonnable pour entreprises de taille moyenne (jusqu'à quelques milliers d'employés).
- Extensibilité : ajout futur de règles locales, nouveaux types de congés, intégration SSO.
