package com.hrms.service;

import com.hrms.dao.UserDAO;
import com.hrms.model.Role;
import com.hrms.model.User;

/**
 * Service d'authentification et de gestion de session
 * Gère l'utilisateur connecté et les permissions
 */
public class AuthenticationService {
    
    private static AuthenticationService instance;
    private User currentUser;
    private final UserDAO userDAO;
    
    // Constructeur privé (Singleton)
    private AuthenticationService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Retourne l'instance unique du service
     */
    public static AuthenticationService getInstance() {
        if (instance == null) {
            synchronized (AuthenticationService.class) {
                if (instance == null) {
                    instance = new AuthenticationService();
                }
            }
        }
        return instance;
    }
    
    /**
     * Authentifie un utilisateur
     * @return true si l'authentification réussit
     */
    public boolean login(String username, String password) {
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            this.currentUser = user;
            System.out.println("Utilisateur connecté: " + user.getUsername() + " (" + user.getRole() + ")");
            return true;
        }
        
        System.out.println("Échec de l'authentification pour: " + username);
        return false;
    }
    
    /**
     * Déconnecte l'utilisateur actuel
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("Déconnexion de: " + currentUser.getUsername());
            this.currentUser = null;
        }
    }
    
    /**
     * Retourne l'utilisateur actuellement connecté
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Vérifie si un utilisateur est connecté
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }
    
    /**
     * Vérifie si l'utilisateur connecté a le rôle ADMIN
     */
    public boolean isAdmin() {
        return isAuthenticated() && currentUser.getRole() == Role.ADMIN;
    }
    
    /**
     * Vérifie si l'utilisateur connecté a le rôle MANAGER
     */
    public boolean isManager() {
        return isAuthenticated() && currentUser.getRole() == Role.MANAGER;
    }
    
    /**
     * Vérifie si l'utilisateur connecté a le rôle EMPLOYEE
     */
    public boolean isEmployee() {
        return isAuthenticated() && currentUser.getRole() == Role.EMPLOYEE;
    }
    
    /**
     * Vérifie si l'utilisateur connecté est ADMIN ou MANAGER
     */
    public boolean isAdminOrManager() {
        return isAdmin() || isManager();
    }
    
    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(Role role) {
        return isAuthenticated() && currentUser.getRole() == role;
    }
    
    /**
     * Change le mot de passe de l'utilisateur connecté
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!isAuthenticated()) {
            return false;
        }
        
        // Vérifier l'ancien mot de passe
        if (!currentUser.getPassword().equals(oldPassword)) {
            System.out.println("Ancien mot de passe incorrect");
            return false;
        }
        
        // Mettre à jour le mot de passe
        currentUser.setPassword(newPassword);
        boolean success = userDAO.updateUser(currentUser);
        
        if (success) {
            System.out.println("Mot de passe changé avec succès");
        }
        
        return success;
    }
}
