package com.hrms.controller;

import com.hrms.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Contrôleur pour la page de connexion
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    private AuthenticationService authService;

    public LoginController() {
        this.authService = AuthenticationService.getInstance();
    }

    @FXML
    private void initialize() {
        // Permettre la connexion avec la touche Entrée
        passwordField.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        // Tentative d'authentification
        boolean success = authService.login(username, password);

        if (success) {
            // Connexion réussie - charger le dashboard
            loadDashboard();
        } else {
            // Échec de la connexion
            showError("Nom d'utilisateur ou mot de passe incorrect");
            passwordField.clear();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        // Cacher le message après 3 secondes
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> errorLabel.setVisible(false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hrms/view/dashboard.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/hrms/css/style.css").toExternalForm());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("HRMS - Tableau de Bord");
            stage.centerOnScreen();

            System.out.println("Dashboard chargé avec succès");

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du dashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du chargement de l'interface");
        }
    }
}
