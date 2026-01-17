package com.hrms.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe représentant un utilisateur du système HRMS
 */
public class User {
    private UUID id;
    private String username;
    private String password; // Stocké en clair pour le moment (à hasher en production)
    private Role role;
    private UUID employeId; // Lien vers l'employé si applicable
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    // Constructeur vide
    public User() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    // Constructeur complet
    public User(UUID id, String username, String password, Role role, UUID employeId, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeId = employeId;
        this.active = active;
        this.createdAt = LocalDateTime.now();
    }

    // Constructeur pour création
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UUID getEmployeId() {
        return employeId;
    }

    public void setEmployeId(UUID employeId) {
        this.employeId = employeId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}
