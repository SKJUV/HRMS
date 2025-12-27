## 4. Couche Modèles (Domain Models) et Logique Métier

Principe

Les entités contiennent de la logique métier. On adopte une approche DDD (Domain-Driven Design) légère : entités riches, services pour orchestration.

Entités clés et responsabilités

- Employe
  - Attributs : id, matricule, prenom, nom, dateEmbouche, contrat (type, salaireBase), idManager, actif
  - Méthodes : calculerAnciennete(), estActif(), getMatriculeFormate()

- Presence
  - Attributs : id, idEmploye, date, heureArrivee, heureDepart, status
  - Méthodes : verifierRetard(heureStandard, margeToleranceMin), calculerTempsPresence()

- DemandeConge
  - Attributs : id, idEmploye, debut, fin, statut
  - Méthodes : estValide(), chevaucheAvec(autreDemande)

- Paie / BulletinPaie
  - Méthodes : calculerSalairePourMois(mois, annee) — exécute le moteur en se basant sur présences, congés, primes, retenues, cotisations.

Exemples de signatures Java (pseudo)

```java
public class Employe {
    private UUID id;
    private String matricule;
    private LocalDate dateEmbauche;
    private boolean actif;

    public int calculerAnciennete() {
        return Period.between(this.dateEmbauche, LocalDate.now()).getYears();
    }

    public boolean estActif() {
        return this.actif;
    }
}
```

Edge cases à prévoir

- Emploi à temps partiel (prorata de congés et salaire)
- Employé avec plusieurs contrats successifs (historique contractuel pour le calcul de la paie)
- Pointages manquants ou corrompus (job de reconciliation)
