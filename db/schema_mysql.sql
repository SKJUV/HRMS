-- Schema MySQL pour HRMS (XAMPP)
-- Base de données: hrms

-- Création de la base de données
CREATE DATABASE IF NOT EXISTS hrms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hrms;

-- Table departement
CREATE TABLE IF NOT EXISTS departement (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    nom VARCHAR(200) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    INDEX idx_dept_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table employe
CREATE TABLE IF NOT EXISTS employe (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    matricule VARCHAR(40) UNIQUE NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    date_embauche DATE NOT NULL,
    id_manager CHAR(36) NULL,
    id_departement CHAR(36) NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    contrat JSON NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_employe_manager FOREIGN KEY (id_manager) REFERENCES employe (id) ON DELETE SET NULL,
    CONSTRAINT fk_employe_departement FOREIGN KEY (id_departement) REFERENCES departement (id) ON DELETE SET NULL,
    INDEX idx_employe_matricule (matricule),
    INDEX idx_employe_manager (id_manager)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table presence avec ENUM pour status
CREATE TABLE IF NOT EXISTS presence (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    id_employe CHAR(36) NOT NULL,
    date_jour DATE NOT NULL,
    heure_arrivee TIME NULL,
    heure_depart TIME NULL,
    status ENUM('ON_TIME', 'LATE', 'ABSENT') NOT NULL DEFAULT 'ON_TIME',
    id_justificatif CHAR(36) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_presence_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE,
    INDEX idx_presence_employe_date (id_employe, date_jour)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table justificatif
CREATE TABLE IF NOT EXISTS justificatif (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    id_presence CHAR(36) NOT NULL,
    type VARCHAR(100) NULL,
    url_document TEXT NULL,
    commentaire TEXT NULL,
    date_depot TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_justif_presence FOREIGN KEY (id_presence) REFERENCES presence (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table solde_conge avec ENUM pour type_conge
CREATE TABLE IF NOT EXISTS solde_conge (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    id_employe CHAR(36) NOT NULL,
    type_conge ENUM('PAID', 'SICK', 'UNPAID') NOT NULL,
    jours_restants DECIMAL(6,2) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_solde_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE,
    INDEX idx_solde_employe_type (id_employe, type_conge)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table demande_conge avec ENUM pour type_conge et statut
CREATE TABLE IF NOT EXISTS demande_conge (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    id_employe CHAR(36) NOT NULL,
    debut DATE NOT NULL,
    fin DATE NOT NULL,
    type_conge ENUM('PAID', 'SICK', 'UNPAID') NOT NULL,
    statut ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    id_manager_avis CHAR(36) NULL,
    id_rh_avis CHAR(36) NULL,
    commentaire TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_demande_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE,
    INDEX idx_demande_employe (id_employe, statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table bulletin_paie
CREATE TABLE IF NOT EXISTS bulletin_paie (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    id_employe CHAR(36) NOT NULL,
    mois_annee VARCHAR(7) NOT NULL, -- format YYYY-MM
    salaire_brut DECIMAL(12,2) NOT NULL DEFAULT 0,
    primes DECIMAL(12,2) NOT NULL DEFAULT 0,
    retenues DECIMAL(12,2) NOT NULL DEFAULT 0,
    cotisations DECIMAL(12,2) NOT NULL DEFAULT 0,
    salaire_net DECIMAL(12,2) NOT NULL DEFAULT 0,
    details JSON NULL,
    date_generation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bulletin_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE,
    UNIQUE INDEX ux_bulletin_employe_mois (id_employe, mois_annee)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table prime
CREATE TABLE IF NOT EXISTS prime (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    id_employe CHAR(36) NOT NULL,
    mois_annee VARCHAR(7) NOT NULL,
    montant DECIMAL(12,2) NOT NULL,
    raison TEXT NULL,
    CONSTRAINT fk_prime_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE,
    INDEX idx_prime_employe_mois (id_employe, mois_annee)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table retenue
CREATE TABLE IF NOT EXISTS retenue (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    id_employe CHAR(36) NOT NULL,
    mois_annee VARCHAR(7) NOT NULL,
    montant DECIMAL(12,2) NOT NULL,
    raison TEXT NULL,
    CONSTRAINT fk_retenue_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE,
    INDEX idx_retenue_employe_mois (id_employe, mois_annee)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fonction pour générer les matricules (via trigger)
DELIMITER //

CREATE TRIGGER before_insert_employe
BEFORE INSERT ON employe
FOR EACH ROW
BEGIN
    IF NEW.matricule IS NULL OR NEW.matricule = '' THEN
        SET NEW.matricule = CONCAT('EMP-', YEAR(NOW()), '-', LPAD((SELECT COALESCE(MAX(CAST(SUBSTRING_INDEX(matricule, '-', -1) AS UNSIGNED)), 0) + 1 FROM employe WHERE matricule LIKE CONCAT('EMP-', YEAR(NOW()), '-%')), 3, '0'));
    END IF;
END//

DELIMITER ;

-- Affichage de confirmation
SELECT 'Schema MySQL créé avec succès pour HRMS!' AS message;
