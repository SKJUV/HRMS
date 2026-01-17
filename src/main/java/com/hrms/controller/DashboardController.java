package com.hrms.controller;

import com.hrms.model.Role;
import com.hrms.model.User;
import com.hrms.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Contrôleur pour le tableau de bord principal
 */
public class DashboardController {

    @FXML
    private Label userInfoLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label employeeCountLabel;

    @FXML
    private Label departmentCountLabel;

    @FXML
    private Label attendanceCountLabel;

    @FXML
    private Label versionLabel;

    // Boutons du menu (avec restriction d'accès)
    @FXML
    private Button usersMenuButton;

    @FXML
    private Button employeesMenuButton;

    @FXML
    private Button departmentsMenuButton;

    @FXML
    private Button attendanceMenuButton;

    @FXML
    private Button leaveMenuButton;

    @FXML
    private Button settingsMenuButton;

    private AuthenticationService authService;

    public DashboardController() {
        this.authService = AuthenticationService.getInstance();
    }

    @FXML
    private void initialize() {
        User currentUser = authService.getCurrentUser();

        if (currentUser != null) {
            // Afficher les informations de l'utilisateur
            userInfoLabel.setText(currentUser.getUsername() + " (" + currentUser.getRole().getDisplayName() + ")");

            // Appliquer les restrictions d'accès selon le rôle
            applyRoleRestrictions(currentUser.getRole());
        }

        // Charger les statistiques initiales
        loadStatistics();
    }

    /**
     * Applique les restrictions d'accès selon le rôle de l'utilisateur
     */
    private void applyRoleRestrictions(Role role) {
        switch (role) {
            case ADMIN:
                // L'admin a accès à tout
                break;

            case MANAGER:
                // Le manager n'a pas accès à la gestion des utilisateurs
                usersMenuButton.setDisable(true);
                settingsMenuButton.setDisable(true);
                break;

            case EMPLOYEE:
                // L'employé a un accès limité
                usersMenuButton.setDisable(true);
                employeesMenuButton.setDisable(true);
                departmentsMenuButton.setDisable(true);
                settingsMenuButton.setDisable(true);
                break;
        }
    }

    /**
     * Charge les statistiques du tableau de bord
     */
    private void loadStatistics() {
        // TODO: Charger les vraies statistiques depuis la base de données
        employeeCountLabel.setText("0");
        departmentCountLabel.setText("0");
        attendanceCountLabel.setText("0");
    }

    @FXML
    private void handleLogout() {
        authService.logout();
        loadLoginScreen();
    }

    @FXML
    private void showDashboard() {
        System.out.println("Affichage du tableau de bord");
        // TODO: Charger le contenu du dashboard
    }

    @FXML
    private void showEmployees() {
        System.out.println("Affichage des employés");
        // TODO: Charger la vue des employés
    }

    @FXML
    private void showDepartments() {
        System.out.println("Affichage des départements");
        // TODO: Charger la vue des départements
    }

    @FXML
    private void showAttendance() {
        System.out.println("Affichage des présences");
        // TODO: Charger la vue des présences
    }

    @FXML
    private void showLeave() {
        System.out.println("Affichage des congés");
        // TODO: Charger la vue des congés
    }

    @FXML
    private void showUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hrms/view/user-management.fxml"));
            Parent userManagementView = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(userManagementView);

            System.out.println("Vue de gestion des utilisateurs chargée");

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la gestion des utilisateurs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showSettings() {
        System.out.println("Affichage des paramètres");
        // TODO: Charger la vue des paramètres
    }

    private void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hrms/view/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/hrms/css/style.css").toExternalForm());

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("HRMS - Connexion");
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'écran de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
