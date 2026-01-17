-- Script SQL pour ajouter la table users à la base de données HRMS
-- À exécuter après schema.sql

-- Création du type ENUM pour les rôles utilisateur
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
        CREATE TYPE user_role AS ENUM ('ADMIN', 'MANAGER', 'EMPLOYEE');
    END IF;
END$$;

-- Table users pour l'authentification
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- À hasher en production (BCrypt recommandé)
    role user_role NOT NULL DEFAULT 'EMPLOYEE',
    employe_id UUID NULL, -- Référence vers la table employe
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    last_login TIMESTAMP WITH TIME ZONE NULL,
    CONSTRAINT fk_user_employe FOREIGN KEY (employe_id) REFERENCES employe (id) ON DELETE SET NULL
);

-- Index pour optimiser les recherches
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_employe ON users(employe_id);

-- Insertion d'utilisateurs de test
INSERT INTO users (username, password, role, active) 
VALUES 
    ('admin', 'admin', 'ADMIN', true),
    ('manager', 'manager', 'MANAGER', true),
    ('employee', 'employee', 'EMPLOYEE', true)
ON CONFLICT (username) DO NOTHING;

-- Commentaires pour la documentation
COMMENT ON TABLE users IS 'Table des utilisateurs du système HRMS avec authentification et rôles';
COMMENT ON COLUMN users.username IS 'Nom d''utilisateur unique pour la connexion';
COMMENT ON COLUMN users.password IS 'Mot de passe (à hasher en production avec BCrypt)';
COMMENT ON COLUMN users.role IS 'Rôle de l''utilisateur: ADMIN, MANAGER ou EMPLOYEE';
COMMENT ON COLUMN users.employe_id IS 'Référence vers l''employé associé (optionnel)';
COMMENT ON COLUMN users.active IS 'Indique si le compte utilisateur est actif';
COMMENT ON COLUMN users.last_login IS 'Date et heure de la dernière connexion';

-- Affichage de confirmation
SELECT 'Table users créée avec succès!' AS message;
SELECT 'Utilisateurs de test créés:' AS message;
SELECT username, role FROM users WHERE username IN ('admin', 'manager', 'employee');
