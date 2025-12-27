## 6. Diagrammes de classes et signatures Java

### Diagramme de classes (Mermaid)

```mermaid
classDiagram
    class Employe {
      +UUID id
      +String matricule
      +String prenom
      +String nom
      +LocalDate dateEmbauche
      +boolean actif
      +int calculerAnciennete()
      +boolean estActif()
    }

    class Presence {
      +UUID id
      +UUID idEmploye
      +LocalDate date
      +LocalTime heureArrivee
      +LocalTime heureDepart
      +Status status
      +Status verifierRetard(LocalTime heureStandard, int margeMinutes)
      +Duration calculerTempsPresence()
    }

    class DemandeConge {
      +UUID id
      +UUID idEmploye
      +LocalDate debut
      +LocalDate fin
      +Statut statut
      +boolean estValide()
      +boolean chevaucheAvec(DemandeConge autre)
    }

    class BulletinPaie {
      +UUID id
      +UUID idEmploye
      +String moisAnnee
      +BigDecimal salaireBrut
      +BigDecimal primes
      +BigDecimal retenues
      +BigDecimal cotisations
      +BigDecimal salaireNet
      +Map<String,Object> details
      +static BulletinPaie calculerPour(Employe e, YearMonth ym)
    }

    Employe "1" o-- "*" Presence : has
    Employe "1" o-- "*" DemandeConge : files
    Employe "1" o-- "*" BulletinPaie : receives

      %% UI layer (JavaFX)
      class MainApp {
        +void start(Stage primaryStage)
        +static void main(String[] args)
      }

      class BaseController {
        +void initialize()
      }

      class PointageController {
        +void onPointageClick()
      }

      MainApp --> BaseController : loads FXML
      BaseController <|-- PointageController

  ```

  ### Organisation recommandée des packages

  - `com.example.hrms` : racine
    - `com.example.hrms.domain` : entités métiers (Employe, Presence, DemandeConge, BulletinPaie)
    - `com.example.hrms.repository` : interfaces/impl pour la persistence
    - `com.example.hrms.service` : services métiers (PayrollService, PresenceService)
    - `com.example.hrms.ui` : JavaFX views et controllers
    - `com.example.hrms.app` : classe `MainApp` et bootstrap

  ### Exemple `module-info.java` (si vous utilisez modules)

  ```java
  module com.example.hrms {
      requires javafx.controls;
      requires javafx.fxml;
      requires java.sql;
      requires spring.context; // si Spring est utilisé

      opens com.example.hrms.ui to javafx.fxml;
      exports com.example.hrms.app;
  }
  ```

  ### Conseils d'implémentation

  - Séparer la logique UI et la logique métier : Controller appelle `service` qui appelle `repository`.
  - Favoriser des DTOs pour l'échange entre client JavaFX et backend REST (JSON).
  - Tester la logique métier avec JUnit (mock des repos). Test UI léger via TestFX si nécessaire.
```

### Méthodes clés et logique

- Presence.verifierRetard(heureStandard, margeMinutes)
  - Compare heureArrivee à heureStandard.
  - Si heureArrivee == null -> status ABSENT (si passé l'heure limite) sinon PENDING.
  - Calcul du retard en minutes : ChronoUnit.MINUTES.between(heureStandard, heureArrivee)

- DemandeConge.estValide()
  - debut.isBefore(fin)
  - pas de chevauchement avec demandes APPROVED existantes

- BulletinPaie.calculerPour()
  - Récupère présences et congés du mois
  - Calcule retenues : minuteRetard * tarifMinute + joursSansSolde * prorata
  - Applique cotisations (pourcentage paramétrable)
