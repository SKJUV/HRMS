package com.hrms.dao;

import com.hrms.model.Role;
import com.hrms.model.User;
import com.hrms.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object pour la gestion des utilisateurs
 * Gère toutes les opérations CRUD sur la table users
 */
public class UserDAO {

    /**
     * Crée un nouvel utilisateur dans la base de données
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (id, username, password, role, employe_id, active) VALUES (UUID(), ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // À hasher en production!
            stmt.setString(3, user.getRole().name());
            
            if (user.getEmployeId() != null) {
                stmt.setString(4, user.getEmployeId().toString());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            
            stmt.setBoolean(5, user.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Authentifie un utilisateur avec son username et password
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND active = true";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password); // À hasher en production!
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                
                // Mise à jour de la dernière connexion
                updateLastLogin(user.getId());
                user.setLastLogin(LocalDateTime.now());
                
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public User getUserById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Récupère un utilisateur par son username
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }

    /**
     * Met à jour un utilisateur existant
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, " +
                     "employe_id = ?, active = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().name());
            
            if (user.getEmployeId() != null) {
                stmt.setString(4, user.getEmployeId().toString());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            
            stmt.setBoolean(5, user.isActive());
            stmt.setString(6, user.getId().toString());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime un utilisateur (soft delete - désactive l'utilisateur)
     */
    public boolean deleteUser(UUID id) {
        String sql = "UPDATE users SET active = false WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id.toString());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime définitivement un utilisateur (hard delete)
     */
    public boolean hardDeleteUser(UUID id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id.toString());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression définitive de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour la dernière date de connexion
     */
    private void updateLastLogin(UUID userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId.toString());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de last_login: " + e.getMessage());
        }
    }

    /**
     * Extrait un objet User depuis un ResultSet
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(UUID.fromString(rs.getString("id")));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(Role.valueOf(rs.getString("role")));
        
        String employeId = rs.getString("employe_id");
        if (employeId != null) {
            user.setEmployeId(UUID.fromString(employeId));
        }
        
        user.setActive(rs.getBoolean("active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        return user;
    }

    /**
     * Vérifie si un username existe déjà
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du username: " + e.getMessage());
        }
        
        return false;
    }
}
