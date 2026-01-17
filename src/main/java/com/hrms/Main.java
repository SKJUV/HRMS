package com.hrms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale de l'application HRMS
 * Point d'entrée JavaFX
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger la vue de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hrms/view/login.fxml"));
            Parent root = loader.load();

            // Créer la scène
            Scene scene = new Scene(root);
            
            // Charger le fichier CSS
            scene.getStylesheets().add(getClass().getResource("/com/hrms/css/style.css").toExternalForm());

            // Configurer la fenêtre principale
            primaryStage.setTitle("HRMS - Système de Gestion des Ressources Humaines");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(700);
            primaryStage.setMinHeight(500);
            
            // Afficher la fenêtre
            primaryStage.show();

            System.out.println("Application HRMS démarrée avec succès");

        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage de l'application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.out.println("Fermeture de l'application HRMS");
        // Fermer les connexions à la base de données
        com.hrms.util.DatabaseConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
