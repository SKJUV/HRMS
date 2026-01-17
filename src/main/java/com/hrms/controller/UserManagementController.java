package com.hrms.controller;

import com.hrms.dao.UserDAO;
import com.hrms.model.Role;
import com.hrms.model.User;
import com.hrms.service.AuthenticationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des utilisateurs
 */
public class UserManagementController {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TextField searchField;

    @FXML
    private Button addUserButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label totalUsersLabel;

    private UserDAO userDAO;
    private AuthenticationService authService;
    private ObservableList<User> usersList;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public UserManagementController() {
        this.userDAO = new UserDAO();
        this.authService = AuthenticationService.getInstance();
        this.usersList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        loadUsers();

        // Listener pour la sélection dans le tableau
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                statusLabel.setText("Utilisateur sélectionné: " + newSelection.getUsername());
            } else {
                statusLabel.setText("Aucun utilisateur sélectionné");
            }
        });
    }

    private void setupTableColumns() {
        // Configuration des colonnes existantes avec PropertyValueFactory
        TableColumn<User, String> usernameCol = (TableColumn<User, String>) usersTable.getColumns().get(0);
        TableColumn<User, Role> roleCol = (TableColumn<User, Role>) usersTable.getColumns().get(1);
        TableColumn<User, Boolean> activeCol = (TableColumn<User, Boolean>) usersTable.getColumns().get(2);
        TableColumn<User, String> createdCol = (TableColumn<User, String>) usersTable.getColumns().get(3);
        TableColumn<User, String> lastLoginCol = (TableColumn<User, String>) usersTable.getColumns().get(4);
        TableColumn<User, Void> actionsCol = (TableColumn<User, Void>) usersTable.getColumns().get(5);

        // Format pour les dates
        createdCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedAt() != null) {
                return new SimpleStringProperty(cellData.getValue().getCreatedAt().format(DATE_FORMATTER));
            }
            return new SimpleStringProperty("-");
        });

        lastLoginCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLastLogin() != null) {
                return new SimpleStringProperty(cellData.getValue().getLastLogin().format(DATE_FORMATTER));
            }
            return new SimpleStringProperty("Jamais");
        });

        // Colonne d'actions avec boutons
        actionsCol.setCellFactory(new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    private final Button editButton = new Button("✏️");
                    private final Button deleteButton = new Button("🗑️");
                    private final HBox pane = new HBox(5, editButton, deleteButton);

                    {
                        pane.setAlignment(Pos.CENTER);
                        editButton.setStyle("-fx-cursor: hand;");
                        deleteButton.setStyle("-fx-cursor: hand;");

                        editButton.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleEditUser(user);
                        });

                        deleteButton.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleDeleteUser(user);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        });

        usersTable.setItems(usersList);
    }

    @FXML
    private void refreshTable() {
        loadUsers();
    }

    private void loadUsers() {
        usersList.clear();
        usersList.addAll(userDAO.getAllUsers());
        totalUsersLabel.setText("Total: " + usersList.size() + " utilisateur(s)");
        statusLabel.setText("Liste actualisée");
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadUsers();
            return;
        }

        ObservableList<User> filteredList = FXCollections.observableArrayList();
        for (User user : userDAO.getAllUsers()) {
            if (user.getUsername().toLowerCase().contains(searchTerm)) {
                filteredList.add(user);
            }
        }

        usersList.clear();
        usersList.addAll(filteredList);
        totalUsersLabel.setText("Résultats: " + filteredList.size() + " utilisateur(s)");
    }

    @FXML
    private void handleAddUser() {
        Dialog<User> dialog = createUserDialog(null);
        Optional<User> result = dialog.showAndWait();

        result.ifPresent(user -> {
            if (userDAO.createUser(user)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur créé avec succès");
                loadUsers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de créer l'utilisateur");
            }
        });
    }

    private void handleEditUser(User user) {
        Dialog<User> dialog = createUserDialog(user);
        Optional<User> result = dialog.showAndWait();

        result.ifPresent(updatedUser -> {
            if (userDAO.updateUser(updatedUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur modifié avec succès");
                loadUsers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier l'utilisateur");
            }
        });
    }

    private void handleDeleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'utilisateur");
        alert.setContentText("Voulez-vous vraiment désactiver l'utilisateur " + user.getUsername() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userDAO.deleteUser(user.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur désactivé");
                loadUsers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de désactiver l'utilisateur");
            }
        }
    }

    private Dialog<User> createUserDialog(User existingUser) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(existingUser == null ? "Nouvel Utilisateur" : "Modifier Utilisateur");
        dialog.setHeaderText(existingUser == null ? "Créer un nouvel utilisateur" : "Modifier les informations");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Créer le formulaire
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        if (existingUser != null) {
            usernameField.setText(existingUser.getUsername());
        }

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        ComboBox<Role> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(Role.values());
        if (existingUser != null) {
            roleComboBox.setValue(existingUser.getRole());
        } else {
            roleComboBox.setValue(Role.EMPLOYEE);
        }

        CheckBox activeCheckBox = new CheckBox("Actif");
        if (existingUser != null) {
            activeCheckBox.setSelected(existingUser.isActive());
        } else {
            activeCheckBox.setSelected(true);
        }

        grid.add(new Label("Nom d'utilisateur:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Mot de passe:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Rôle:"), 0, 2);
        grid.add(roleComboBox, 1, 2);
        grid.add(activeCheckBox, 1, 3);

        if (existingUser != null) {
            Label note = new Label("Laissez le mot de passe vide pour ne pas le modifier");
            note.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");
            grid.add(note, 1, 4);
        }

        dialog.getDialogPane().setContent(grid);

        // Conversion du résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String username = usernameField.getText().trim();
                String password = passwordField.getText();
                Role role = roleComboBox.getValue();
                boolean active = activeCheckBox.isSelected();

                if (username.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom d'utilisateur est requis");
                    return null;
                }

                if (existingUser == null) {
                    // Nouveau utilisateur
                    if (password.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Le mot de passe est requis");
                        return null;
                    }

                    // Vérifier si le username existe déjà
                    if (userDAO.usernameExists(username)) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Ce nom d'utilisateur existe déjà");
                        return null;
                    }

                    return new User(username, password, role);
                } else {
                    // Modification
                    User updatedUser = existingUser;
                    updatedUser.setUsername(username);
                    if (!password.isEmpty()) {
                        updatedUser.setPassword(password);
                    }
                    updatedUser.setRole(role);
                    updatedUser.setActive(active);
                    return updatedUser;
                }
            }
            return null;
        });

        return dialog;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
