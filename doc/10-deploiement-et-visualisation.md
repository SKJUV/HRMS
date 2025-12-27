## 10. Visualiser les diagrammes Mermaid et déploiement

Visualiser Mermaid

- VSCode : installer l'extension "Markdown Preview Mermaid" ou "vstirbu.vscode-mermaid-preview". Ouvrir le fichier Markdown et activer la preview.
- MkDocs : utiliser `mkdocs` avec le plugin `mkdocs-mermaid2-plugin` pour rendre les diagrammes côté site.

Exemples de commandes (Linux, bash)

```bash
# installer mkdocs et plugin
python -m pip install mkdocs mkdocs-material mkdocs-mermaid2-plugin

# lancer un site local
mkdocs serve
```

Déploiement recommandé

Architecture et packaging ciblés (Java + JavaFX)

- Application : backend Java (Spring Boot ou Jakarta EE) pour les services et un client riche en JavaFX pour l'interface.
- JavaFX : séparer la couche UI (JavaFX, FXML, Controllers) de la couche service (REST client) pour conserver une architecture propre (MVC / MVVM).
- Base de données : PostgreSQL (UUID support natif), jobs planifiés via Spring Scheduled ou Quartz.
- Stockage des justificatifs : objet storage (S3) ou dossier sécurisé.

Dépendances JavaFX (OpenJFX)

Si vous utilisez Maven, ajoutez les dépendances OpenJFX adaptées à votre plateforme :

```xml
<!-- fragment pom.xml -->
<dependencies>
	<dependency>
		<groupId>org.openjfx</groupId>
		<artifactId>javafx-controls</artifactId>
		<version>20</version>
	</dependency>
	<dependency>
		<groupId>org.openjfx</groupId>
		<artifactId>javafx-fxml</artifactId>
		<version>20</version>
	</dependency>
</dependencies>
```

Build et packaging (Maven / Gradle)

Exemple Maven (plugin javafx-maven-plugin)

```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-maven-plugin</artifactId>
			<version>0.0.8</version>
			<configuration>
				<mainClass>com.example.hrms.app.MainApp</mainClass>
			</configuration>
		</plugin>
	</plugins>
</build>
```

Créer un runtime natif et un installeur

- Utiliser `jlink` pour produire un runtime minimal contenant seulement les modules nécessaires.
- Utiliser `jpackage` (JDK 14+) pour produire un installeur natif (deb, rpm, dmg, exe).

Exemple rapide (bash)

```bash
# Créer le JAR
mvn clean package

# Exemple d'utilisation de jlink (simplifié)
jlink --module-path $JAVA_HOME/jmods:target/mods --add-modules com.example.hrms --output runtime

# Créer un installeur avec jpackage
jpackage --name MonHRMS --input target --main-jar monhrms.jar --main-class com.example.hrms.app.MainApp --type deb
```

Conseils supplémentaires

- Utiliser module-info.java pour mieux contrôler les modules embarqués si vous construisez un runtime réduit.
- Tester sur chaque plateforme cible (Linux, Windows, macOS) car JavaFX dépend de librairies natives.
- Pour simplifier les builds multiplateformes, considérer des pipelines CI qui génèrent les packages pour chaque OS.

Surveillance et opérations

- Jobs batch (paie, acquisition congés) monitorés et alertés via Prometheus/Grafana.
- Backup quotidien de la base et rétention des bulletins de paie pour conformité.
