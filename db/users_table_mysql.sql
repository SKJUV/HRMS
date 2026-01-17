-- Script SQL pour ajouter la table users à MySQL (XAMPP)
-- À exécuter après schema_mysql.sql

USE hrms;

-- Table users pour l'authentification
CREATE TABLE IF NOT EXISTS users (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'MANAGER', 'EMPLOYEE') NOT NULL DEFAULT 'EMPLOYEE',
    employe_id CHAR(36) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    CONSTRAINT fk_user_employe FOREIGN KEY (employe_id) REFERENCES employe (id) ON DELETE SET NULL,
    INDEX idx_users_username (username),
    INDEX idx_users_role (role),
    INDEX idx_users_employe (employe_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertion d'utilisateurs de test
INSERT INTO users (id, username, password, role, active) 
VALUES 
    (UUID(), 'admin', 'admin', 'ADMIN', true),
    (UUID(), 'manager', 'manager', 'MANAGER', true),
    (UUID(), 'employee', 'employee', 'EMPLOYEE', true)
ON DUPLICATE KEY UPDATE username = username;

-- Affichage de confirmation
SELECT 'Table users créée avec succès!' AS message;
SELECT 'Utilisateurs de test créés:' AS info;
SELECT username, role, active FROM users WHERE username IN ('admin', 'manager', 'employee');
